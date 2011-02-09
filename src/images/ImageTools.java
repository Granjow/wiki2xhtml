/*
 *   Copyright (C) 2011 Simon Eugster <granjow@users.sf.net>

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

package src.images;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import src.Container_Resources;
import src.project.settings.GalleryProperties;
import src.project.settings.ImageProperties;
import src.resources.ResProjectSettings.EImageProperties;
import src.resources.ResProjectSettings.SettingsE;
import src.templateHandler.Template;
import src.templateHandler.Template.WarningType;
import src.utilities.IOWrite_Stats;

public class ImageTools {
	
	/**
	 * Generates a thumbnail entry.
	 * @return Thumbnail code
	 * @throws IOException
	 */
	public static StringBuffer generateThumbnailEntry(ImageProperties prop) throws IOException {
		StringBuffer output = new StringBuffer();

		if (prop.imagepageCreated) {
			prop.set_(EImageProperties.link, prop.getImagepagePath(false, false));
		} else {
			prop.set_(EImageProperties.link, prop.getImagePathHtml());
		}

		Template tp = prop.getTemplate();
		System.out.printf("Arguments: %s.\n", prop.getList("|", "=", false));
		
		output = tp.applyTemplate(prop.getBase64List("|", "="), prop.parentFile.project, true, null, null, WarningType.NONE);
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
		return output;
	}
	
	public static StringBuffer generateGalleryContainer(GalleryProperties gp) throws FileNotFoundException {
		Template tp = new Template(Container_Resources.sTplGalleryContainer, gp.parentFile.project);
		return tp.applyTemplate(gp.getBase64List("|", "="), gp.parentFile.project, true, null, null, null);
	}

	/**
	 * Generates an image page
	 * @since wiki2xhtml 3.4: Titles for image pages
	 */
	public static void generateImagepage(ImageProperties prop) {

		try {
			StringBuffer sb = new StringBuffer();
			Template tpl = new Template(Container_Resources.sTplImagepage, prop.parentFile.project);
			
			String title = prop.parentFile.getProperty(SettingsE.titleRule, true);
			if (title == null) {
				title = "%caption %s";
			}
			if (prop.isSet(EImageProperties.caption)) {
				title = title.replaceAll("%caption", prop.get_(EImageProperties.caption));
			} else {
				title = title.replaceAll("%caption", "");
			}
			
			title = title.replaceAll("%path", prop.getImagePathHtml());
			title = title.replaceAll("%name", prop.getImageFilenameHtml());
			title = prop.parentFile.generators.title(title);
			
//			String args = Container_Resources.sTplImagepage;
//			for (EImageProperties p : EImageProperties.values()) {
//				if (prop.get_(p) != null) {
//					args += "|" + prop.get_(p);
//				}
//			}
			sb = tpl.applyTemplate(prop.getBase64List("|", "="), prop.parentFile.project, true, null, null, null);
			
			// ReplaceTags
			// Wiki tasks
			
			File f = new File(prop.getImagepagePath(true, false));
			IOWrite_Stats.writeString(f, sb.toString(), false);
			if (prop.parentFile.sitemap) {
				prop.parentFile.project.sitemap.add(f);
			}

			prop.set_(EImageProperties.pageCreated, "true");

		} catch (IOException e) {
//			ca.ol("Error: Image page " + is.image.getImagepagePath(true, false) + " couldn't be created!\n", CALevel.ERRORS);
			e.printStackTrace();

			prop.set_(EImageProperties.pageCreated, prop.nullValue());
		}

	}
	
}
