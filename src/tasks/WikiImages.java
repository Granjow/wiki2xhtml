package src.tasks;

import java.io.IOException;
import java.util.regex.Matcher;

import src.Statistics;
import src.images.ImageTools;
import src.project.WikiProject;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.project.settings.ImageProperties;
import src.resources.RegExpressions;
import src.resources.ResProjectSettings.EImageContext;
import src.resources.ResProjectSettings.EImageProperties;
import src.tasks.Tasks.Task;


public class WikiImages extends WikiTask {

	public Task desc() {
		return Task.Images;
	}
	public WikiTask nextTask() {
		return new WikiLinks();
	}
	
	/**
	 * Make the [[Image:xx]] to &lt;img src="xx" /&gt; tags.
	 * @since August 2008: rewritten
	 * @since wiki2xhtml 4.0: Rewritten using placeholder tags
	 */
	public void parse(WikiFile file) {
		Statistics.getInstance().sw.timeInsertingImages.continueTime();

		StringBuffer in = file.getContent();
		StringBuffer out = new StringBuffer();
		short counter = 0;

		Matcher m;

		ImageProperties prop = new ImageProperties(file);
		m = RegExpressions.images.matcher(in);
		int last = 0;

		while (m.find()) {
			out.append(in.substring(last, m.start()));
			Statistics.getInstance().counter.imagesTotal.increase();

			// Read the image's arguments
			int id = file.addImageProperties(prop);
			prop.set_(EImageProperties.number, Integer.toString(id));
			prop.readArguments(m.group(1));
			prop.set_(EImageProperties.context, EImageContext.thumb.property);
			
			// Generate an image page if they are to be generated, a isThumbDesired has to be inserted and it's no direct link.
			if (prop.isSet(EImageProperties.thumbEnabled) && !prop.isSet(EImageProperties.direct)) {
				ImageTools.generateImagepage(prop);
			}

			// Insert a placeholder
			out.append(ImageTools.getPlaceholder(prop));

			last = m.end();
		}
		out.append(in.substring(last, in.length()));
		
		// Link Images
		int i;
		ImageProperties currentIP;
		ImageProperties nextIP = null;
		for (i = 0; i < file.imagePropertiesList.size(); i++) {
			if (EImageContext.thumb.property.equals(file.imagePropertiesList.get(i).get_(EImageProperties.context))) {
				nextIP = file.imagePropertiesList.get(i); 
			}
		}
		for ( ; i < file.imagePropertiesList.size(); i++) {
			if (EImageContext.thumb.property.equals(file.imagePropertiesList.get(i).get_(EImageProperties.context))) {
				currentIP = nextIP;
				nextIP = file.imagePropertiesList.get(i);
				
				currentIP.nextIP = nextIP;
				nextIP.previousIP = currentIP;
			}
		}
		
		// Replace placeholders by code
		String placeholder;
		String code;
		for (ImageProperties p : file.imagePropertiesList) {
			if (EImageContext.thumb.property.equals(p.get_(EImageProperties.context))) {
				placeholder = ImageTools.getPlaceholder(p);
				last = out.indexOf(placeholder);
				try {
					code = ImageTools.generateThumbnailEntry(p).toString();
				} catch (IOException e) {
					code = e.getMessage();
					e.printStackTrace();
				}
				out = out.replace(last, code.length(), code);
			}
		}
		

		/* Statistics */
		Statistics.getInstance().counter.imagesTotal.increase(counter);
		Statistics.getInstance().sw.timeInsertingImages.stop();

		file.setContent(out);
	}
	
	public static void main(String[] args) {
		WikiProject wp = new WikiProject(".");
		StringBuffer sb = new StringBuffer("[[Image:a.jpg|400px|hallo.]]");
		VirtualWikiFile vf = new VirtualWikiFile(wp, "name", false, true, sb);
		vf.removeAllTasks();
		vf.addTask(Task.Images);
		vf.parse();
		System.out.println(vf.getContent());
	}
	
}
