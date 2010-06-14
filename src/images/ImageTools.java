package src.images;

import java.io.File;
import java.io.IOException;

import src.Container_Resources;
import src.parserFunctions.Parser;
import src.project.settings.ImageProperties;
import src.resources.ResProjectSettings.EImageProperties;
import src.resources.ResProjectSettings.SettingsE;
import src.templateHandler.Template;
import src.templateHandler.Template.WarningType;
import src.utilities.IOWrite_Stats;

public class ImageTools {
	
	/**
	 * @return A placeholder for an image. Used for replacing image tags and insert the code
	 * afterwards after the images have been linked.
	 */
	public static String getPlaceholder(ImageProperties prop) {
		return String.format(">>>image-%s-placeholder<<<", prop.get_(EImageProperties.number));
	}
	
	/**
	 * Generates a thumbnail entry.
	 * @return Thumbnail code
	 * @throws IOException
	 */
	public static StringBuffer generateThumbnailEntry(ImageProperties prop) throws IOException {
		String args = new String();
		StringBuffer output = new StringBuffer();

		if (prop.imagepageCreated) {
			prop.set_(EImageProperties.link, prop.getImagepagePath(false, false));
		} else {
			prop.set_(EImageProperties.link, prop.getImagePathHtml());
		}

		Template tp = prop.getTemplate();
		args = "Image:" + tp.templateFilename;
		args += "|" + prop.getList("|", "=", false);
		System.out.printf("Arguments: %s.", args);
		
		output = tp.applyTemplate(args, null, null, WarningType.NONE);
		output = Parser.parse(output);
		
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
			title = title.replaceAll("%caption", prop.get_(EImageProperties.caption));
			title = title.replaceAll("%path", prop.getImagePathHtml());
			title = title.replaceAll("%name", prop.getImageFilenameHtml());
			title = prop.parentFile.generators.title(title);
			
			String args = Container_Resources.sTplImagepage;
			for (EImageProperties p : EImageProperties.values()) {
				if (prop.get_(p) != null) {
					args += "|" + prop.get_(p);
				}
			}
			sb = tpl.applyTemplate(args, null, null, null);
			
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
