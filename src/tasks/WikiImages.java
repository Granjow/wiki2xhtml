/*
 *   Copyright (C) 2010-2011 Simon A. Eugster <simon.eu@gmail.com>

 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package src.tasks;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import src.Constants.Template_ImagePage;
import src.Constants;
import src.Statistics;
import src.Constants.Template_Images;
import src.project.FallbackFile;
import src.project.WikiProject;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.project.settings.ImageProperties;
import src.ptm.PTMObject.RecursionException;
import src.ptm.PTMRootNode;
import src.resources.RegExpressions;
import src.resources.ResProjectSettings.EImageContext;
import src.resources.ResProjectSettings.EImageProperties;
import src.tasks.Tasks.Task;
import src.utilities.IOWrite_Stats;

/**
 * Inserts images.
 */
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

		ImageProperties prop;
		m = RegExpressions.images.matcher(in);
		int last = 0;

		while (m.find()) {
			out.append(in.substring(last, m.start()));
			Statistics.getInstance().counter.imagesTotal.increase();

			// Read the image's arguments
			prop = new ImageProperties(file);
			file.addImageProperties(prop);
			prop.readArguments(m.group(1));
			prop.set_(EImageProperties.context, EImageContext.thumb.property);
			
			// Generate an image page if they are to be generated, a isThumbDesired has to be inserted and it's no direct link.
			generateImagepage(prop);

			// Insert a placeholder
			out.append(prop.getPlaceholder());

			last = m.end();
		}
		out.append(in.substring(last, in.length()));
		prop = null;
		
		// Link Images
		int i;
		ImageProperties currentIP;
		ImageProperties nextIP = null;
		for (i = 0; i < file.imagePropertiesList.size(); i++) {
			if (EImageContext.thumb.property.equals(file.imagePropertiesList.get(i).get_(EImageProperties.context))) {
				nextIP = file.imagePropertiesList.get(i); 
				break;
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
				placeholder = p.getPlaceholder();
				last = out.indexOf(placeholder);
				try {
					code = generateThumbnailEntry(p).toString();
				} catch (IOException e) {
					code = e.getMessage();
					e.printStackTrace();
				}
				out = out.replace(last, last+placeholder.length(), code);
			}
		}
		

		/* Statistics */
		Statistics.getInstance().counter.imagesTotal.increase(counter);
		Statistics.getInstance().sw.timeInsertingImages.stop();

		file.setContent(out);
	}
	
	public static StringBuffer generateThumbnailEntry(ImageProperties prop) throws IOException {

		prop.resolvePaths();

		FallbackFile tp = prop.getTemplate();
//		System.out.printf("Arguments: %s.\n", prop.getList("|", "=", false));
		
		PTMRootNode root = new PTMRootNode(tp.getContent(), prop.argumentBindings);
		
//		System.out.println("Thumbnail entry: ");
//		root.printTree(System.out, " ");
//		System.out.println("States: ");
//		prop.argumentBindings.printValues();
		
		try {
			return new StringBuffer(root.evaluate());
		} catch (RecursionException e) {
			return new StringBuffer();
		}
	}

	/**
	 * Generates an image page if {@link Template_Images#direct} is not set to {@code true}.
	 */
	public static boolean generateImagepage(ImageProperties prop) {
		boolean generated = false;
		if (!"true".equals(prop.argumentBindings.resolve(Template_Images.direct))) {
			try {
				FallbackFile template = prop.parentFile.project.locate(Constants.Templates.sTplImagepage);
				
				if (prop.nextIP != null) {
					String s = prop.nextIP.getImagepagePath();
					prop.argumentBindings.b(Template_ImagePage.nextPage, s.substring(s.lastIndexOf('/')+1));
				}
				if (prop.previousIP != null) {
					String s = prop.previousIP.getImagepagePath();
					prop.argumentBindings.b(Template_ImagePage.prevPage, s.substring(s.lastIndexOf('/')+1));
				}
				
				StringBuffer back = new StringBuffer();
				String ppath = prop.getImagepagePath();
				int depth = 0;
				int index = 0;
				while ((index = ppath.indexOf('/', ++index)) > 0) { depth++; }
				for (int i = 0; i < depth; i++) {
					back.append("../");
				}
				prop.argumentBindings.b(Template_ImagePage.back, back.toString());
				
				prop.argumentBindings.b(Template_ImagePage.sourcePage, prop.parentFile.internalName());
				
				
				PTMRootNode root = new PTMRootNode(template.getContent(), prop.argumentBindings);
				
				String s = root.evaluate();
				
				File f = new File(prop.parentFile.project.outputDirectory().getAbsolutePath() + File.separator + prop.getImagepagePath());
				IOWrite_Stats.writeString(f, s, false);
				if (prop.parentFile.sitemap) {
					prop.parentFile.project.sitemap.add(prop.getImagepagePath());
				}
	
				prop.imagepageCreated = true;
				generated = true;
	
			} catch (IOException e) {
	//			ca.ol("Error: Image page " + is.image.getImagepagePath(true, false) + " couldn't be created!\n", CALevel.ERRORS);
				e.printStackTrace();
	
				prop.set_(EImageProperties.pageCreated, prop.nullValue());
			} catch (RecursionException e) {
				e.printStackTrace();
	
				prop.set_(EImageProperties.pageCreated, prop.nullValue());
			}
		}
		return generated;
	}
	
	public static void main(String[] args) {
		WikiProject wp = new WikiProject(".");
		StringBuffer sb = new StringBuffer("[[Image:a.jpg|400px|hallo.]]");
		VirtualWikiFile vf = new VirtualWikiFile(wp, "name", false, sb);
		vf.removeAllTasks();
		vf.addTask(Task.Images);
		vf.parse();
		System.out.println(vf.getContent());
	}
	
}
