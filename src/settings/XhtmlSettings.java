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
import src.Template;
import src.WikiHeadings;
import src.WikiLists;
import src.Constants.SettingsE;
import src.WikiLinks.LinkObject;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.utilities.Sort;
import src.utilities.StringTools;

import static src.Constants.SettingsE;
import static src.Constants.SettingsLocalE;


/*
 *   Copyright (C) 2007-2009 Simon Eugster <granjow@users.sf.net>

 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

/**
 *
 * A container for various settings applying header, homelink etc.
 *
 * @since wiki2xhtml 3.3: Rewritten
 * @since wiki2xhtml 3.4: Rewritten, using HashMap instead of 1000 variables
 *
 * @author Simon Eugster
 */
public class XhtmlSettings {

	public GlobalSettings global = new GlobalSettings();

	public LocalSettings local = new LocalSettings();

	private static I18n i18n = I18nFactory.getI18n(XhtmlSettings.class, "bin.l10n.Messages", src.Globals.getLocale());

	private XhtmlSettings() { }

	private static XhtmlSettings xhs = new XhtmlSettings();

	/** Singleton access */
	public static XhtmlSettings getInstance() {
		return xhs;
	}

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

	/**
	 * Prepares for a new page by resetting all local settings.
	 */
	public void init() {
		local = new LocalSettings();
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
				
				mSimple = Resources.Regex.argMetaSimple.matcher(s);
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

	public static class Settings extends src.settings.Settings<SettingsE, String> {
		
		public void append_(final SettingsE property, final String value) {
			if (contains(property)) {
				set_(property, get_(property) + value);
			} else {
				set_(property, value);
			}
		}
		
		@Override
		public String nullValue() {
			return "null";
		}
		
		@Override
		boolean setCheck(SettingsE property, String value) {
			boolean ok = true;
			// Manage special cases
			switch (property) {
			case galleryImagesPerLine:
				try {
					// Don't use negative values
					byte b = Byte.parseByte(value);
					if (b < 0) {
						throw new NegativeValueException(value);
					}
				} catch (NumberFormatException e) {
					ok = false;
					e.printStackTrace();
					CommentAtor.getInstance().ol("Number format exception with " + value + ": " + e.getMessage(), CALevel.ERRORS);
				} catch (NegativeValueException e) {
					ok = false;
					CommentAtor.getInstance().ol(e.getMessage(), CALevel.ERRORS);
				}
				break;
			}
			return ok;
		}
		
	}

	/**
	 * Global settings for all pages
	 */
	public static class GlobalSettings extends Settings {
		
		public String footer = null;

		/** Initialize settings */
		public GlobalSettings() {
			set_(SettingsE.imagepagesDir, Constants.Directories.imagePages);
			set_(SettingsE.imagepageImgWidth, Constants.Standards.widthImgImagepages);
			set_(SettingsE.thumbWidth, Constants.Standards.widthThumbs);
			set_(SettingsE.descForCaption, Constants.Standards.useImageDescAsCaption);
			set_(SettingsE.nameForCaption, Constants.Standards.useImagenameAsCaption);
			set_(SettingsE.galleryImagesPerLine, Constants.Standards.galleryImagesPerLine);
		}


	}


	/**
	 * Local settings for a specific page
	 */
	public static class LocalSettings extends Settings {
		
		private HashMap<SettingsLocalE, String> localSettingsMap = new HashMap<SettingsLocalE, String>();
		private ArrayList<NamespaceObject> linkNamespaces = null;
		
		public StringBuffer content;
		
		public boolean contains(SettingsLocalE property) {
			return localSettingsMap.containsKey(property);
		}
		
		public String get_(SettingsLocalE property) {
			return localSettingsMap.get(property);
		}
		
		/**
		 * @return The local property, if available, and the global otherwise.
		 */
		public String getLocalOrGlobal_(SettingsE property) {
			if (contains(property)) return get_(property);
			else return xhs.global.get_(property);
		}
		
		public boolean set_(SettingsLocalE property, String value) {
			boolean success = true;
			localSettingsMap.put(property, value);
			return success;
		}
		
		
		/*
		 ******************************************* 
		 */
		
		public final LinkObject applyNamespace(LinkObject lo) {
			if (lo.uri.indexOf(':') < 0) {
				return lo;
			}
			
			// Put all namespace into linkNamespaces if it hasn't been initialized yet
			if (linkNamespaces == null) {
				StringBuilder allNamespaces = new StringBuilder();
				if (xhs.global.contains(SettingsE.namespace)) {
					allNamespaces.append(xhs.global.get_(SettingsE.namespace));
				}
				if (contains(SettingsE.namespace)) {
					allNamespaces.append(get_(SettingsE.namespace));
				}
				linkNamespaces = new ArrayList<NamespaceObject>();
				Matcher m = Resources.Regex.namespace.matcher(allNamespaces.toString());
				while (m.find()) {
					linkNamespaces.add(new NamespaceObject(m.group(1), m.group(2)));
				}
				if (linkNamespaces.size() == 0) {
					linkNamespaces = null;
					return lo;
				}
			}
			
			for (NamespaceObject no : linkNamespaces) {
				if (no.canReplace(lo)) {
					lo = no.replace(lo);
					break;
				}
			}
			
			return lo;
		}
		
		public static enum DisplayRule {
			pageXofY, pageX, X;
		}
		public String page(final DisplayRule displayRule) {
			String page;
			String number = get_(SettingsLocalE.pageNumber);
			String totalPages = get_(SettingsLocalE.pagesTotal);
			switch (displayRule) {
			case pageXofY:
				page = i18n.tr("Page {0} of {1}", number, totalPages);
				break;
			case pageX:
				page = i18n.tr("Page {0}", number);
				break;
			case X:
			default:
				page = number;
				break;
			}
			
			return page;
			
		}

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
				Matcher m = Resources.Regex.head.matcher(content);

				if (m.find()) {
					output.append(content.substring(0, m.start()));
					set_(SettingsLocalE.head, m.group(1));
					output.append(content.substring(m.end(), content.length()));
					content = output;
				}

			}
		}

		/**
		 * @return The standard reck file or, if an alternative reck file has been provided, the alternative one.
		 */
		public String reck() {
			String s;
			if (contains(SettingsE.reckAlternative)) {
				s = get_(SettingsE.reckAlternative);
			} else if (xhs.global.contains(SettingsE.reckAlternative)) {
				s = xhs.global.get_(SettingsE.reckAlternative);
			} else {
				s = Constants.Files.reck;
			}
			return s;
		}


		/**
		 * @return The page title, replacing %s and %p by standard title and page number
		 */
		public String title() {
			String title = xhs.local.get_(SettingsE.title);
			if (title == null) {
				title = xhs.local.get_(SettingsE.h1);
			} else {
				title = title(title);
			}
			if (title == null) title = "";
			return title;
		}
		/**
		 * 
		 * @param pattern Input title
		 * @return The pattern with %s and %p replaced; see {@link #title()}
		 */
		public String title(String pattern) {
			String title = pattern;
			int pos = title.indexOf(Constants.Tags.Title.titleTag);
			if (pos >= 0) {
				String defaultTitle = xhs.global.get_(SettingsE.defaultTitle);
				if (defaultTitle == null) defaultTitle = "";
				title = title.substring(0, pos)
						+ defaultTitle
						+ title.substring(pos + 2, title.length());
			}

			pos = title.indexOf(Constants.Tags.Title.pageTag);
			if (pos >= 0) {
				title = title.substring(0, pos)
						+ page(DisplayRule.pageXofY)
						+ title.substring(pos + 2, title.length());
			}
			return title;
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

	public static class NegativeValueException extends Exception {
		private static final long serialVersionUID = 1L;

		public NegativeValueException(String s) {
			super("Negative value obtained: " + s);
		}
	}

	/** @since wiki2xhtml 3.4 */
	private static class NamespaceObject {
		
		private static final char sep = ':';
		
		private String key;
		private String value;
		private boolean cut = false;
		
		public NamespaceObject(String key, String value) {
			this.key = key;
			if (value.endsWith(Constants.Settings.argCut)) {
				cut = true;
				this.value = value.substring(0, value.length() - Constants.Settings.argCut.length());
			} else {
				this.value = value;
			}
		}
		
		public boolean canReplace(LinkObject lo) {
			return lo.uri.startsWith(key + sep);
		}
		
		public LinkObject replace(LinkObject lo) {
			lo.uri = value.replace("%s", lo.uri.substring(key.length()+1));
			if (cut) {
				// Mark link object that the beginning of the name will have to be cut off
				lo.key = key + sep;
			}
			return lo;
		}
		
	}

}
