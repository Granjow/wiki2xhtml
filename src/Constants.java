package src;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import src.project.settings.Settings.Checker;
import src.project.settings.Settings.ValuePreparser;
import src.resources.ResProjectSettings;
import src.resources.ResProjectSettings.ImagepageCaptionAlternatives;
import src.resources.ResProjectSettings.SettingsE;


/*
 *   Copyright (C) 2007-2011 Simon Eugster <granjow@users.sf.net>

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
 *
 */

/**
 * Contains constants and standard settings.
 *
 * @since wiki2xhtml 3.3
 *
 * @author Simon Eugster
 */
public final class Constants {


	/** Directories */
	public static final class Directories {
		public static final String style = "style";
		public static final String userHome = java.lang.System.getProperty("user.home");
		public static final String workingDir = java.lang.System.getProperty("user.dir");
		public static final String workingDir2 = java.lang.System.getProperty("user.dir") + File.separatorChar;
		public static final String settingsDir = userHome + File.separatorChar + ".wiki2xhtml";
		public static final String imagePages = "imagePages";
		public static final String target = "wiki2xhtml-output";
	}

	/** Standard files, like the configuration file */
	public static final class Files {
		public static final File settings = new File(Constants.Directories.settingsDir + File.separatorChar + "settings.txt");
		public static final String reck = "reck.html";
		public static final String imageTemplate = "image-template.html";
		public static final String cssSettings = "css-settings.txt";
	}

	/** wiki2xhtml settings, like the version number */
	public static final class Wiki2xhtml {

		static {
			// Can be replaced by a script that dynamically builds the version number from the git commits
			String s = "$VERSION$";
			if (s.startsWith("$")) {
				// Hard-coded version number as fallback
				s = "4.0rc1";
			}
			versionNumber = s;
			s = "$DATE$";
			if (s.startsWith("$")) {
				s = "Mar 2011";
			}
			versionDate = s;
		}
		
		public static final String versionNumber;
		public static final String versionDate;
		/** Old versions which will be ignored by the update checker. */
		//TODO 0 update version numbers
		public static final String[] versionsTillNow = new String[] { "3.4b8", "3.3.2", "3.3.1", "3.3", "3.2.1", "3.1.0", "3.0.4" };
		public static final String webpage = "http://wiki2xhtml.sf.net";
		public static final URL[] getUpdateURLs() {
			try {
				return new URL[] { new URL("http://granjow.net/wiki2xhtml") };
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		}

	}

	/** Default settings like thumbnail width */
	public static final class Standards {

		public static final Locale locale = Locale.ENGLISH;

		public static String widthImgImagepages = "700px";
		public static String widthThumbs = "200px";
		public static String galleryWidthThumbs = "125px";

		public static String galleryImagesPerLine = "3";

		public static final String imagepageCaption = ResProjectSettings.ImagepageCaptionAlternatives.description.key();
	}


	/** Command line arguments for wiki2xhtml */
	public static final class Arguments {

		public static final class FileArgs {
			
			public static final String noSitemap = "nositemap";
			public static final String noParse = "noparse";
			
		}

	}
	
	public static final class Links {
		
		/**
		 * @since wiki2xhtml 3.4
		 * TODO 0 Doc: Link arguments
		 */
		public static enum LinksE {
			/**
			 * <p>Decides whether a link also has to be disabled
			 * when it links to an anchor in the current page.</p>
			 * 
			 * <p>Example: In navigation bars:<br/>
			 * <code>[[p1.html#top|First|disable]] [[p2.html#top|Second|disable]]</code><br/>
			 * If you're on page p1.html, the first link needs to be disabled. However links like<br/>
			 * <code>[[#top|Go to top of the page]]</code><br/>
			 * must _not_ be disabled, therefore this argument.</p>
			 */
			disable ("disable"),
			/**
			 * Additional arguments for the link
			 */
			args ("args");
			
			private final String arg;
			LinksE(String arg) {
				this.arg = arg;
			}
			public String arg() { return arg; }
		}
		
		/** Expression for URIs to be recognized.
		 *  @since wiki2xhtml 3.4 */
		public static final String supportedURIs = "https?://|ftps?://";
		public static final String supportedURIsShortened = "www\\.|ftp\\.";
		
	}

	/** Menu tags which may followed after a <code>#!</code> at line beginning in the menu file. */
	public static final class MenuTags {

		public static final String linkDeactivate = "link_deactivate";
		public static final String linkDeactivateNot = "link_deactivate0";

		public static final String linkStrong = "link_strong";
		public static final String linkStrongNot = "link_strong0";

	}

	/** Parser functions */
	public static final class ParserFunctions {

		public static final String pIf = "#if:";
		public static final String pIfeq = "#ifeq:";
		public static final String pSwitch = "#switch:";

		public static final String defaultValue = "#default";

	}

	/** Positions */
	public static final class Position {

		public static final String left = "left";
		public static final String center = "center";
		public static final String right = "right";

	}
	
	public static final class Template_Blocks {
		public static final String isBlock = "isBlock";
		public static final String text = "text";
		/** Additional tag parameters (style="..." e.g.) */
		public static final String args = "args";
		/** Additional class=".." definitions, without the class= (class names only) */
		public static final String classes = "classes";
		/** Additional style="..." definitions extracted from the arguments.
		 * Also for being used like <code>style="color: #444; {{{style|}}}"</code> in the template. */
		public static final String style = "style";
	}
	
	public static final class Template_Gallery {
		public static final String id = "id";
		public static final String number = "number";
	}

	/** Arguments available for image templates */
	public static final class Template_Images {

		/** Clear before and after (insert clear=both) to put it on a new line */
		public static final String clear = "clear";
		/** <p>Don't create an image page, link directly.</p>
		 * <p>contains {@code "true"} (i.e. is not empty) if this property is set.</p> */
		public static final String direct = "direct";
		/** Do not scale the image on the image page. */
		public static final String noscale = "noscale";
		/**This argument has two different meanings (which don't imply each other): 
		 * <ol>
		 * <li>Path to the thumbnail: {@code thumb=<path>} (results in {@code thumb=path} in the template)</li>
		 * <li>Use a thumbnail box: {@code thumb} (results in {@code type=thumb} in the template)</li>
		 * </ol>
		 * <p>If no thumbnail path is given explicitly, and if {@code type=thumb}, then wiki2xhtml tries to
		 * create a thumbnail path based on {@link SettingsE#thumbsDir}, or uses the image path if no thumbnail
		 * directory has been set.</p>
		 * <p>If {@code type != thumb} and if no thumbnail path has been set for this image, then {@code {{{thumb}}}} should 
		 * be empty. In templates, {@code {{{thumb|{{{path}}}}}}} should be safe to use.</p> */
		public static final String thumb = "thumb";
		/** Type: thumb (with image page) or nothing. Visual behaviour.
		 * @see #thumb */
		public static final String type = "type";
		/** Image path. Always resolves to the path given in {@code [[Image:PATH]]}. Only available in the template. */
		public static final String path = "path";
		/** Image width. */
		public static final String width = "width";
		/** Image height. */
		public static final String height = "height";
		/** Image description. */
		public static final String text = "text";
		/** Horizontal position. */
		public static final String location = "location";
		/** Argument: Custom link to use.<br/>
		 * Template: Target link.*/
		public static final String link = "link";
		/** Image ID for linking/anchoring */
		public static final String id = "id";
		/** Image caption */
		public static final String caption = "caption";
		/** Alternative description (if the image cannot be displayed). @since wiki2xhtml 4.0 */
		public static final String alt = "alt";
		
		/** Number of the image on the current page */
		public static final String number = "number";
		
		/** <p>Is {@code "true"} if this is the first image in a row.</p>
		 * <p>Available in the template only.</p> */
		public static final String rowStart = "rowStart";
		/** <p>Is {@code "true"} if this is the last image in a row.
		 * The row width is set with {@link SettingsE#galleryImagesPerLine}.</p>
		 * <p>Available in the template only.</p> */
		public static final String rowEnd = "rowEnd";

		/** Long description of the image */
		public static final String longDesc = "longdesc";
		
		/** To comment out a gallery entry */
		public static final String galleryComment = "//";

	}
	
	/** Parameters available for image page templates, additionally to {@link Template_Images}. */
	public static final class Template_ImagePage {
		public static final String nextPage = "nextPage";
		public static final String prevPage = "prevPage";
		public static final String back = "back";
		public static final String sourcePage = "sourcePage";
	}
	
	public static final class Template_Page {
		public static final String h1 = "h1";
		public static final String homelink = "homelink";
		public static final String icon = "icon";
		public static final String menu = "menu";
		public static final String text = "text";
		public static final String title = "title";
		// Metadata
		public static final String keywords = "keywords";
		public static final String lang = "lang";
		public static final String author = "author";
	}
	
	public static final class Template_References {
		/** Reference text */
		public static final String text = "text";
		/** Number of the reference (1, 2, ...) */
		public static final String number = "number";
		/** ID of the reference link (reference; in-text), for linking/anchoring */
		public static final String refID = "citeRefID";
		/** ID of the reference entry (note; page bottom), for linking/anchoring */
		public static final String noteID = "citeNoteID";
		/** For the references list.
		 * Intention: When isContainer is not empty, text is set to the 
		 * reference entry list, and the reference template can build
		 * additional tags around it, like: <code>&lt;ol&gt;{{{text|}}}&lt;/ol&gt;</code>. 
		 */
		public static final String container = "isContainer";
		
	}
	
	public static final class Template_TOC {
		public static final String isBlock = "isBlock";
		public static final String level = "level";
		public static final String text = "text";
		public static final String ol = "ol";
		public static final String ul = "ul";
		public static final String id = "id";
	}
	
	public static final class Checkers {
		private static final Pattern pSize = Pattern.compile("^\\d+(?:%|px)$");
		
		public static final Checker<String> positiveNonzeroByteChecker = new Checker<String>() {
			public boolean check(String value) {
				byte b = Byte.parseByte(value);
				return b > 0;
			}
		};
		public static final Checker<String> positiveByteChecker = new Checker<String>() {
			public boolean check(String value) {
				byte b = Byte.parseByte(value);
				return b >= 0;
			}
		}; 
		public static final Checker<String> sizeChecker = new Checker<String>() {
			public boolean check(String value) {
				Matcher m = pSize.matcher(value);
				return m.find();
			}
		};
		public static final Checker<String> boolChecker = new Checker<String>() {
			public boolean check(String value) {
				if ("true".equals(value) || "false".equals(value)) {
					return true;
				} else {
					System.err.println("Argument must be eiter true or false");
					return false;
				}
			};
		};
		public static final Checker<String> equalSignChecker = new Checker<String>() {
			public boolean check(String value) {
				return value.indexOf('=') >= 0;
			};
		};
		public static final Checker<String> captionAlternativeChecker = new Checker<String>() {
			public boolean check(String value) {
				String sep = ", ";
				StringBuilder sb = new StringBuilder();
				for (ImagepageCaptionAlternatives e : ImagepageCaptionAlternatives.values()) {
					if (e.key().equals(value)) { return true; }
					else { sb.append(e.key() + sep); }
				}
				if (sb.length() > 0) { sb.delete(sb.length() - sep.length(), sb.length()); }
				System.err.println("Alternative must be one of the following: " + sb);
				return false;
			};
		};
	}
	
	public static final class Preparsers {
		/** Ensures that a string ends with a <code>/</code> */
		public static final ValuePreparser<String> directoryTrailingSlashPreparser = new ValuePreparser<String>() {
			public String adjust(String value) {
				if (value != null && !value.endsWith("/")) {
					value += "/";
				}
				return value;
			}
		};
	}

	/** Tags which will be replaced in the document, e.g. by the current version */
	public static final class Tags {

//		public static final String splitPageNav = "{{$SplitPageNav}}";
		
		public static final String version = "{{$Version}}";
		public static final String pagename = "{{$Pagename}}"; // TODO Doc {{$Pagename}}
		public static final String wiki2xhtml = "{{$wiki2xhtml}}";

		public static final class Title {

			public static final String titleTag = "%s";
			public static final String pageTag = "%p";
			
			/** @since wiki2xhtml 3.4 */
			public static final String imgCaption = "%caption";
			public static final String imgName = "%name";
			public static final String imgPath = "%path";

		}

	}

	public static final class ProgramSettings {

		public static final String autoUpdate = "AutoUpdateCheck";
		public static final String lastArgs = "LastArguments";
		public static final String locale = "Locale";
		public static final String ignoreNewVersion = "IgnoreNewVersion";

	}

	/** Tags used in the updater file */
	public static enum Updater {
		currentVersion("CurrentVersion"), currentVersionDate("CurrentVersionDate"),		
		/** @since wiki2xhtml 3.4 */
		increaseSizeBy("IncreaseSizeBy"),
		/** @since wiki2xhtml 3.4 */
		currentVersionFullname("CurrentVersionFullname"),
		/** @since wiki2xhtml 3.4 */
		htmlVersionNotes("HtmlVersionNotes");
		
		private final String keyword;
		private Updater(String keyword) {
			this.keyword = keyword;
		}
		public final String keyword() {
			return keyword;
		}
	}
	
}
