package src.tasks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import src.Statistics;
import src.images.ImageTools;
import src.project.WikiProject;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.project.settings.GalleryProperties;
import src.project.settings.ImageProperties;
import src.resources.ResProjectSettings.EGalleryProperties;
import src.resources.ResProjectSettings.EImageContext;
import src.resources.ResProjectSettings.EImageProperties;
import src.tasks.Tasks.Task;

public class WikiGalleries extends WikiTask {

	public Task desc() {
		// TODO check: Galleries before lists?
		return Task.Galleries;
	}
	public WikiTask nextTask() {
		return new WikiInsertNowikiContent();
	}
	/**
	 * <p>Builds all galleries.</p>
	 * <ul>
	 * <li>Image entries start with <code>Image:</code>.</li>
	 * <li>Text entries start with text only</li>
	 * <li>Comments start with <code>//</code> (will not be shown on the page)</li>
	 * </ul>
	 */
	public void parse(WikiFile file) {

		Statistics.getInstance().sw.timeInsertingImages.continueTime();

		BufferedReader b = new BufferedReader(new StringReader(file.getContent().toString()));
		StringBuffer out = new StringBuffer();

		GalleryProperties gp = null;

		byte counter = 0;

		try {
			String line;
			boolean open = false;

			for (line = b.readLine(); line != null; line = b.readLine()) {

				if (open) {
					if (line.startsWith("</gallery>")) {

						open = false;
						out.append(gp.getPlaceholder());

					} else {
						
						if (line.startsWith("//")) {
							// Comment. Do not add image.
							continue;
						}

						// Add a new item
						ImageProperties prop = new ImageProperties(file);
						int id = file.addImageProperties(prop);
						prop.set_(EImageProperties.number, Integer.toString(id));
						gp.imagePropertiesList.add(prop);
						prop.readArguments(line);
						prop.set_(EImageProperties.context, EImageContext.gallery.property);
						prop.set_(EImageProperties.galleryNumber, gp.get_(EGalleryProperties.number));
					}
				} else {
					if (line.startsWith("<gallery ") || line.startsWith("<gallery>")) {
						// add gallery start
						open = true;
						gp = new GalleryProperties(file);
						int id = file.addGalleryProperties(gp);
						gp.set_(EGalleryProperties.number, Integer.toString(id));
						
					} else {
						// add line
						out.append(line + '\n');
					}
				}


			}

		} catch (IOException e) {
			e.printStackTrace();
//			CommentAtor.getInstance().ol(
//				String.format("IO Exception while trying to read content in file %s!", Container_Files.getInstance().currentFilename),
//				CALevel.ERRORS);
			return;
		}
		gp = null;
		
		// Link all items
		ImageProperties previousIP = null;
		for (ImageProperties currentIP : file.imagePropertiesList) {
			if (EImageContext.gallery.property.equals(currentIP.get_(EImageProperties.context))) {
				if (previousIP != null) {
					if (currentIP.get_(EImageProperties.galleryNumber).equals(previousIP.get_(EImageProperties.galleryNumber))) {
						previousIP.nextIP = currentIP;
						currentIP.previousIP = previousIP;
					}
				}
				previousIP = currentIP;
			}
		}
		
		// Create all image pages
		for (GalleryProperties gap : file.galleryPropertiesList) {
			for (ImageProperties p : gap.imagePropertiesList) {
				if (p.isSet(EImageProperties.path) && !p.isSet(EImageProperties.direct)) {
					ImageTools.generateImagepage(p);
				}
			}
		}
		
		// Insert the galleries by replacing their placeholders
		int last = 0;
		for (GalleryProperties gap : file.galleryPropertiesList) {
			final String placeholder = gap.getPlaceholder();
			for (ImageProperties p : gap.imagePropertiesList) {
				try {
					gap.append_(EGalleryProperties.content, ImageTools.generateThumbnailEntry(p).toString());
				} catch (IOException e) {
					System.err.println("Error in file " + file.name);
					e.printStackTrace();
				}
			}
			last = out.indexOf(placeholder);
			if (last >= 0) {
				try {
					out = out.replace(last, last+placeholder.length(), ImageTools.generateGalleryContainer(gap).toString());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		

		/* Statistics */
		Statistics.getInstance().counter.galleries.increase(counter);
		Statistics.getInstance().sw.timeInsertingImages.stop();

		file.setContent(out);
	}
	
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("<gallery>\nImage:test.jpg\n</gallery>");
		WikiProject wp = new WikiProject(".");
		VirtualWikiFile vf = new VirtualWikiFile(wp, "name", false, true, sb);
		vf.removeAllTasks();
		vf.addTask(Task.Galleries);
		vf.parse();
		System.out.println(vf.getContent());
	}
	

}
