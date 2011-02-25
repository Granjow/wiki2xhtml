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

import src.Constants;
import src.Container_Resources;
import src.Statistics;
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
			int id = file.addImageProperties(prop);	// no new()? TODO
			prop.set_(EImageProperties.number, Integer.toString(id));
			prop.readArguments(m.group(1));
			prop.set_(EImageProperties.context, EImageContext.thumb.property);
			
			// Generate an image page if they are to be generated, a isThumbDesired has to be inserted and it's no direct link.
			if ("true".equals(prop.getValue(Constants.Images.thumb)) && !"direct".equals(prop.getValue(Constants.Images.direct))) {
				generateImagepage(prop);
			}

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

		if (prop.imagepageCreated) {
			prop.set_(EImageProperties.link, prop.getImagepagePath(false, false));
		} else {
			prop.set_(EImageProperties.link, prop.getImagePathHtml());
		}

		FallbackFile tp = prop.getTemplate();
		System.out.printf("Arguments: %s.\n", prop.getList("|", "=", false));
		
		PTMRootNode root = new PTMRootNode(tp.getContent(), prop.argumentBindings);
		
		System.out.println("Thumbnail entry: ");
		root.printTree(System.out, " ");
		System.out.println("States: ");
		prop.argumentBindings.printValues();
		
		try {
			return new StringBuffer(root.evaluate());
		} catch (RecursionException e) {
			return new StringBuffer();
		}
		
//		output = tp.applyTemplate(prop.getBase64List("|", "="), prop.parentFile.project, true, null, null, WarningType.NONE);
//		output = Parser.parse(output); // parser functions?
		
//		tp.replaceAll(Constants.TemplateTags.alt, //No special things for alt description: escaping
//					  src.utilities.WikiStringTools.disableWikilinks(
//						  desc.replaceAll(RegExpressions.tagContent.pattern(), "$1"))
//					  .replaceAll("\"", "&quot;")
//					 );
//		tp.replaceAll(Constants.TemplateTags.pos, prop.isSet(SettingsImgE.thumbPosition) ? prop.get_(SettingsImgE.thumbPosition) : "");
//		tp.replaceAll(Constants.TemplateTags.link, link);
//		tp.replaceAll(Constants.TemplateTags.thumb, prop.getThumbSrc());
//		tp.replaceAll(Constants.TemplateTags.imagePath, prop.getImagePathHtml());
//		tp.replaceAll(Constants.TemplateTags.width, prop.getWidthThumbAttrib());
//		tp.replaceAll(Constants.TemplateTags.desc, desc);
//		tp.replaceAll(Constants.TemplateTags.descXmlName, XMLTools.getXmlNameChar(desc, "\\s"));
//		tp.replaceAll(Constants.TemplateTags.id, prop.getID());
//		tp.replaceAll(Constants.TemplateTags.args, prop.isSet(SettingsImgE.args) ? prop.get_(SettingsImgE.args) : "");
//		tp.replaceAll(Constants.TemplateTags.argsLink, prop.isSet(SettingsImgE.argsLink) ? prop.get_(SettingsImgE.argsLink) : "");
//		if (prop.isSet(SettingsImgE.clearBefore)) {
//			output.append("<p style=\"clear: both; margin: 0pt; padding: 0pt; height: 0pt;;\"></p>\n");
//		}
//		output.append(tp.getOutput());
//		if (prop.isSet(SettingsImgE.clearAfter)) {
//			output.append("<p style=\"clear: both; margin: 0pt; padding: 0pt; height: 0pt;;\"></p>\n");
//		}
//		return output;
	}

	/**
	 * Generates an image page
	 */
	public static void generateImagepage(ImageProperties prop) {

		try {
			FallbackFile template = prop.parentFile.project.locate(Container_Resources.sTplImagepage);
			
			PTMRootNode root = new PTMRootNode(template.getContent(), prop.argumentBindings);
			
			String s = root.evaluate();
			
//			String title = prop.parentFile.getProperty(SettingsE.titleRule, true);
//			if (title == null) {
//				title = "%caption %s";
//			}
//			if (prop.isSet(EImageProperties.caption)) {
//				title = title.replaceAll("%caption", prop.get_(EImageProperties.caption));
//			} else {
//				title = title.replaceAll("%caption", "");
//			}
//			
//			title = title.replaceAll("%path", prop.getImagePathHtml());
//			title = title.replaceAll("%name", prop.getImageFilenameHtml());
//			title = prop.parentFile.generators.title(title);
			
//			String args = Container_Resources.sTplImagepage;
//			for (EImageProperties p : EImageProperties.values()) {
//				if (prop.get_(p) != null) {
//					args += "|" + prop.get_(p);
//				}
//			}
//			sb = tpl.applyTemplate(prop.getBase64List("|", "="), prop.parentFile.project, true, null, null, null);
			
			// ReplaceTags
			// Wiki tasks
			
			File f = new File(prop.getImagepagePath(true, false));
			IOWrite_Stats.writeString(f, s, false);
			if (prop.parentFile.sitemap) {
				prop.parentFile.project.sitemap.add(f);
			}

			prop.set_(EImageProperties.pageCreated, "true");

		} catch (IOException e) {
//			ca.ol("Error: Image page " + is.image.getImagepagePath(true, false) + " couldn't be created!\n", CALevel.ERRORS);
			e.printStackTrace();

			prop.set_(EImageProperties.pageCreated, prop.nullValue());
		} catch (RecursionException e) {
			e.printStackTrace();

			prop.set_(EImageProperties.pageCreated, prop.nullValue());
		}

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
