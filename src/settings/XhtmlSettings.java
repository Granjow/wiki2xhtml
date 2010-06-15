package src.settings;

import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import java.util.regex.Matcher;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.Constants;
import src.Container_Files;
import src.Container_Resources;
import src.Resources;
import src.Statistics;
import src.tasks.WikiLinks.LinkObject;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.utilities.Sort;
import src.utilities.StringTools;
import src.resources.RegExpressions;
import src.resources.ResProjectSettings.SettingsE;
import src.resources.ResProjectSettings.SettingsLocalE;

/**
 *
 * @since wiki2xhtml 3.3: Rewritten
 * @since wiki2xhtml 3.4: Rewritten, using HashMap instead of 1000 variables
 */
public class XhtmlSettings {

	public LocalSettings local = new LocalSettings();

	/**
	 * Creates the link to the start page
	 * @return " href=\"homelink\""
	 */
	public String homelink() {
		if (local.contains(SettingsE.homelink) || global.contains(SettingsE.homelink)) {
			return " href=\"" + (local.contains(SettingsE.homelink) ? local.get_(SettingsE.homelink) : global.get_(SettingsE.homelink)) + "\"";
		}
		return "";
	}
	
	private static final String parseMetadata(String content) {
		BufferedReader br = new BufferedReader(new StringReader(content));
		StringBuilder metadata = new StringBuilder();
		Matcher mSimple;
		boolean addRobots = true;
		
		String s;
		try {
			while ((s = br.readLine()) != null) {
				if (s.contains("robots=")) addRobots = false;
				
				mSimple = RegExpressions.argMetaSimple.matcher(s);
				metadata.append("\n<meta ");
				if (mSimple.matches()) {
					metadata.append("name=\"" + mSimple.group(1) + "\" ");
					metadata.append("keyword=\"" + mSimple.group(2) + "\" ");
				} else {
					metadata.append(s);
				}
				metadata.append(" />");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (addRobots) {
			metadata.append("\n<meta name=\"robots\" content=\"all\" />");
		}
		
		return metadata.toString();
	}


	/**
	 * Local settings for a specific page
	 */
	public static class LocalSettings extends Settings {

		/** @return The image directory, with a / at the end */
		public String dirImages() {
			String dir = contains(SettingsE.imagesDir) ? get_(SettingsE.imagesDir) : xhs.global.get_(SettingsE.imagesDir);
			if (".".equals(dir)) dir = "";
			else if (dir == null) dir = "";
			
			if (!dir.endsWith("/")) dir += "/";
			
			if (dir.startsWith("/")) dir = "." + dir;

			return (dir.length() > 0) ? dir : "";
		}

		public byte galleryImagesPerLine() {
			String number = contains(SettingsE.galleryImagesPerLine) ? get_(SettingsE.galleryImagesPerLine) : xhs.global.get_(SettingsE.galleryImagesPerLine);
			return Byte.parseByte(number);
		}

		/**
		 * Creates a heading entry if there is a heading
		 * @return &lt;h1&gt;heading&lt;/h1&gt;
		 */
		public String h1() {
			if (contains(SettingsE.h1))
				return "<h1>" + get_(SettingsE.h1) + "</h1>";
			return "";
		}

		/**
		 * TODO 0 use rules for entire head
		 * @param rule What to prepend every line. t = tab, s = space.
		 */
		public String head(String rule) {
			if (rule == null || rule.length() == 0 || !contains(SettingsLocalE.head)) {
				return get_(SettingsLocalE.head);
			}
			String prepend = rule.replace('s', ' ').replace('t', '\t');

			return prepend + src.templateHandler.TemplateManager.applyTemplates(new StringBuffer(get_(SettingsLocalE.head)), null, null)
					.toString().replaceAll("\n", "\n" + prepend);
		}

		public final String keywords() {
			StringBuffer keywords = new StringBuffer()
			;
			if (xhs.global.contains(SettingsE.keywords)) {
				keywords.append(xhs.global.get_(SettingsE.keywords));
			}
			if (contains(SettingsE.keywords)) {
				if (keywords.length() > 0) keywords.append(", ");
				keywords.append(get_(SettingsE.keywords));
			}
			
			/** Statistics timer */
			Statistics.getInstance().sw.timeSortingKeywords.start();

			/* Read all keywords into an array */
			Vector<String> v = StringTools.importSimpleCSV(keywords.toString());
			String[] array = new String[v.size()];
			for (int i = 0; i < v.size(); i++)
				array[i] = v.get(i).toLowerCase();

			/* Sort the array */
			Sort.shellsort(array);

			/* Get the CSV list again */
			v = new Vector<String>();
			String lastItem = "";
			for (int i = 0; i < array.length; i++) {
				if (!array[i].equals(lastItem))
					v.add(array[i]);
				lastItem = array[i];
			}
			keywords = StringTools.exportSimpleCSV(v);

			Statistics.getInstance().sw.timeSortingKeywords.stop();

			return keywords.toString();
		}

		public final String lang() {
			String lang = contains(SettingsE.lang) ? get_(SettingsE.lang) : xhs.global.get_(SettingsE.lang);
			if (lang == null) lang = Constants.Standards.locale.toString();
			return lang;
		}

		public final String metadata() {
			StringBuilder meta = new StringBuilder();
			if (xhs.global.contains(SettingsE.keywords) || contains(SettingsE.keywords)) {
				meta.append(Resources.metaOpen + "keywords" + Resources.metaMiddle + keywords() + Resources.metaClose);
			}

			if (xhs.global.contains(SettingsE.desc) || contains(SettingsE.desc)) {
				meta.append(Resources.metaOpen + "description" + Resources.metaMiddle);
				if (contains(SettingsE.desc)) meta.append(get_(SettingsE.desc));
				else meta.append(xhs.global.get_(SettingsE.desc));
				meta.append(Resources.metaClose);
			}

			if (xhs.global.contains(SettingsE.author) || contains(SettingsE.author)) {
				meta.append(Resources.metaOpen + "author" + Resources.metaMiddle);
				if (contains(SettingsE.author)) meta.append(get_(SettingsE.author));
				else meta.append(xhs.global.get_(SettingsE.author));
				meta.append(Resources.metaClose);
			}

			if (xhs.global.contains(SettingsE.lang) || contains(SettingsE.lang)) {
				meta.append(Resources.metaOpenAlt + "content-language" + Resources.metaMiddle);
				if (contains(SettingsE.lang)) meta.append(get_(SettingsE.lang));
				else meta.append(xhs.global.get_(SettingsE.lang)); 
				meta.append(Resources.metaClose);
			}

			/* IE needs «shortcut icon», standards compliant only take «icon» */
			if (xhs.global.contains(SettingsE.icon) || contains(SettingsE.icon)) {
				meta.append("\n  <link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"");
				if (contains(SettingsE.icon)) meta.append(get_(SettingsE.icon));
				else meta.append(xhs.global.get_(SettingsE.icon));
				meta.append("\" />");
			}

			if (contains(SettingsLocalE.redirect)) {
				meta.append(get_(SettingsLocalE.redirect));
			}
			
			StringBuilder customMeta = new StringBuilder();
			if (xhs.global.contains(SettingsE.meta)) {
				customMeta.append(xhs.global.get_(SettingsE.meta));
			}
			if (contains(SettingsE.meta)) {
				customMeta.append(get_(SettingsE.meta));
			}
			meta.append(parseMetadata(customMeta.toString()));
			
			return meta.toString();
		}

		/**
		 * Reads the head content, if available.
		 * Head may contain JavaScript, CSS and so on.
		 * @param input Content
		 */
		public void readHtmlHead() {
			StringBuffer output = new StringBuffer();

			if (content != null && content.length() > 0) {
				Matcher m = RegExpressions.head.matcher(content);

				if (m.find()) {
					output.append(content.substring(0, m.start()));
					set_(SettingsLocalE.head, m.group(1));
					output.append(content.substring(m.end(), content.length()));
					content = output;
				}

			}
		}



		public String toc() {
			Template tp = new Template(
					   new File(Container_Files.getInstance().cont.styleDirAbsolutePath() + File.separatorChar + "tplTOC.txt"),
					   Container_Resources.readResource(Container_Resources.sTOC)
				   );
			
			tp.clear();
			StringBuffer tocSource = WikiHeadings.getTOC(content);
			StringBuffer toc = WikiLists.makeList(tocSource);
			tp.replaceAll(Constants.TemplateTags.toc, toc.toString());
			return tp.getOutput().toString();
		}
		
		
	}



}
