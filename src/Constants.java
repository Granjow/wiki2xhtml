package src;

import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import src.project.settings.Settings.Checker;
import src.project.settings.Settings.ValuePreparser;
import src.resources.ResProjectSettings;
import src.resources.RegExpressions;
import src.resources.ResProjectSettings.ImagepageCaptionAlternatives;


/*
 *   Copyright (C) 2007-2010 Simon Eugster <granjow@users.sf.net>

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

		// TODO 0 check for git
		public static final String revision = "$Revision: 1894 $".replace("$", "");
		public static final String revisionDate = "$Date$".replace("$", "");
		public static final String progName = "wiki2xhtml";
		public static final String versionNumber = "3.4b9";
		public static final String versionDate = "Jan 28, 2010";
		/** Old versions which will be ignored by the update checker. */
		//TODO 0 update version numbers
		public static final String[] versionsTillNow = new String[] { "3.4b8", "3.3.2", "3.3.1", "3.3", "3.2.1", "3.1.0", "3.0.4" };
		public static final String version = versionNumber + " (" + versionDate + ")";
		public static final String webpage = "http://wiki2xhtml.sf.net";
		public static final URL[] getUpdateURLs() {
			try {
				return new URL[] { new URL("http://granjow.net/wiki2xhtml") };
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		}
		public static final String revisionNumber() {
			Matcher m = RegExpressions.num.matcher(Constants.Wiki2xhtml.revision);
			if (m.find()) { return m.group(0); }
			return "(unknown)";
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

		//TODO remove
//		public static final Args stdArgs = new Args("-m menu.txt -f footer.txt -c common.txt ");
//
//		public static final Args stdArgsHelpDe = new Args("-m menu-de.txt -f footer-de.txt -c common-de.txt " +
//				"-s hd --source-dir=doc/txt --target-dir=help err404.php help-de.txt doc-de.txt index-de.txt download-de.txt quickstart-de.txt " +
//				"news-de.txt changelog-de.txt faq-de.txt examples-de.txt ex-php-reload-de.php " +
//				"php-de.txt doc-design-de.txt quickref-de.txt about-de.txt links-de.txt usage-de.txt");
//
//		public static final Args stdArgsHelpEn = new Args(stdArgs.getArgs(GetPolicyE.AllArgs) +
//				" -s hd --source-dir=doc/txt --target-dir=help err404.php index.txt help.txt doc.txt download.txt quickstart.txt quickstart-ru.txt quickstart-it.txt news.txt faq.txt php.txt " +
//				"examples.txt ex-php-reload.php doc-design.txt quickref.txt about.txt changelog.txt testpage.txt links.txt usage.txt");

		/** Special command line arguments */
		public static final class Special {

			public static final String menufileUpdater = "menu";
			public static final String templateUpdater = "template";

		}

		/** Command line arguments (like -f footer.txt) for wiki2xhtml
		 * @deprecated */
		public static final class Arg {

			public static final String menu = "-m";
			public static final String style = "-s";
			public static final String common = "-c";
			public static final String sourceDir = "--sd";
			public static final String targetDir = "--td";
			public static final String footer = "-f";

		}

		/** Combined command line arguments (like --footer=f.txt) for wiki2xhtml
		 * @deprecated */
		public static final class CombinedArg {

			public static final String common = "--common=";
			public static final String footer = "--footer=";
			public static final String lang = "--lang=";
			public static final String menu = "--menu-file=";
			public static final String sitemap = "--sitemap=";
			public static final String style = "--style=";
			public static final String sourceDir = "--source-dir=";
			public static final String targetDir = "--target-dir=";
		}

		/** Command line flags (like --dead) for wiki2xhtml with no further arguments
		 * @deprecated */
		public static final class Flags {

			public static final String dead = "--dead";
			public static final String debug = "--debug";
			public static final String help = "-h";
			public static final String helpLong = "--help";
			public static final String helpfilesDe = "--helpfiles-de";
			public static final String helpfiles = "--helpfiles";
			public static final String noupdatecheck = "--noupdatecheck";
			public static final String incremental = "--incremental";
			public static final String standard = "--standard";
			public static final String silent = "--silent";
			public static final String verbose = "--verbose";
			public static final String updateCheck = "--www";

			public static final String onlyCode = "--only-code";
			public static final String stdout = "--stdout";
			public static final String removeLinebreaks = "--remove-linebreaks";
			public static final String typos = "--typos";

			public static final String gui = "--gui";

		}
		
		public static final class FileArgs {
			
			public static final String noSitemap = "nositemap";
			public static final String noParse = "noparse";
			
		}

		public static final String[] obsoleteFlags = new String[] {"-pn", "-pns", "--header" };
		public static final String[] obsoleteArgs = new String[] {"-i", "-t", "--index-file", "--title="};
		public static final String[] obsoleteCombined = new String[] {"--header=",  };

	}
	
	public static final class Blocks {
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

	/** Arguments for images */
	public static final class Images {

		/** Additional HTML arguments for the image
		public static final String args = "args=";
		/** Additional HTML arguments for the image link, e.g. for use with plug-ins
		 * @since wiki2xhtml 3.4 *
		 * TODO doc: removed
		public static final String argsLink = "argsLink=";
		/** Clear before and after (insert clear=both) to put it on a new line
		 * @since wiki2xhtml 3.4 */
		public static final String clear = "clear";
		// TODO doc: clear=arg
//		/** Clear before an image
//		 * @since wiki2xhtml 3.4 */
//		public static final String clearBefore = "clear:before";
//		/** Clear after an image
//		 * @since wiki2xhtml 3.4 */
//		public static final String clearAfter = "clear:after";
		/** Direct link, no image page */
		public static final String direct = "direct";
		/** Do not scale the image on the image page. @since wiki2xhtml 4.0 TODO Doc renamed from «small» */
		public static final String noscale = "noscale";
		/** Path to the thumbnail */
		public static final String thumb = "thumb";
		/** Type: thumb (with image page) or direct (links directly). @since wiki2xhtml 4.0 */
		public static final String type = "type";
		/** Image path. @since wiki2xhtml 4.0 */
		public static final String path = "path";
		/** Image width. @since wiki2xhtml 4.0 */
		public static final String width = "width";
		/** Image height. @since wiki2xhtml 4.0 */
		public static final String height = "height";
		/** Image description. @since wiki2xhtml 4.0 */
		public static final String text = "text";
		/** Horizontal position. @since wiki2xhtml 4.0 */
		public static final String location = "location";
		/** Alternative link. @since wiki2xhtml 4.0 */
		public static final String link = "link";
		/** Image caption */
		public static final String caption = "caption";
		/** Alternative description (if the image cannot be displayed). @since wiki2xhtml 4.0 */
		public static final String alt = "alt";

		/** Long description of the image */
		public static final String longDesc = "longdesc";
		
		/** To comment out a gallery entry */
		public static final String galleryComment = "//";

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
		
		/** @deprecated */
		public static final String disable = "|disable";
		
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
	
	public static final class References {
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

	/** Tags used in templates */
	@Deprecated public static final class TemplateTags {

		/** @since wiki2xhtml 3.4 */
		public static final String args = "[args]";
		/** @since wiki2xhtml 3.4 */
		public static final String argsLink = "[argsLink]";
		public static final String caption = "[caption]";
		/** Direct link to image */
		public static final String imagePath = "[imagepath]";
		public static final String imageName = "[imagename]";
		public static final String imageCaption = "[imagecaption]";
		public static final String pre = "[pre]";
		public static final String back = "[back]";
		public static final String nextImage = "[nextimage]";
		public static final String prevImage = "[previmage]";
		public static final String width = "[width]";
		public static final String alt = "[alt]";

		public static final String pos = "[pos]";
		/** Link to image or image page */
		public static final String link = "[link]";
		/** Thumbnail source */
		public static final String thumb = "[thumb]";
		public static final String desc = "[desc]";
		/** Description, as XML name (without special characters).
		 * @since wiki2xhtml 3.3 */
		public static final String descXmlName = "[descxmlname]";
		public static final String id = "[id]";

		public static final String meta = "[meta]";
		public static final String title = "[title]";
		public static final String head = "[head]";
		public static final Pattern regexHead = RegExpressions.reckHead;
		public static final String textheader = "[textheader]";
		public static final String menu = "[menu]";
		public static final String footer = "[footer]";
		public static final String h1 = "[h1]";
		public static final String homelink = "[homelink]";
		public static final String content = "[content]";
		
		/** @since wiki2xhtml 3.4 */
		public static final String toc = "[toc]";

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

		public static final String anchor = "~~(.+?)~~";
		public static final String splitPageNav = "{{$SplitPageNav}}";
		public static final String version = "{{$Version}}";
		public static final String top = "{{Top}}";
		public static final Pattern regexToc = RegExpressions.toc;
		public static final String pagename = "{{$Pagename}}";
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

	public static final class Placeholder {

		public static final String toc = "__TOC__";

	}

	public static final class ProgramSettings {

		public static final String autoUpdate = "AutoUpdateCheck";
		public static final String lastArgs = "LastArguments";
		public static final String locale = "Locale";
		public static final String ignoreNewVersion = "IgnoreNewVersion";

	}

	public static final class Keys {

		public static final KeyStroke quit = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK);
		public static final KeyStroke close = KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK);
		public static final KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		public static final KeyStroke ctrlNext = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK);
		public static final KeyStroke ctrlPrev = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK);
		public static final KeyStroke ctrlPgDown = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, KeyEvent.CTRL_DOWN_MASK);
		public static final KeyStroke ctrlPgUp = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, KeyEvent.CTRL_DOWN_MASK);

		public static final KeyStroke toggleCodeWindow = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_DOWN_MASK);

	}

	public static final class Filters {

		/**
		 * Filter filtering unwanted files in the style directory.
		 */
		public static final java.io.FileFilter designFiles = new java.io.FileFilter() {
			@Override
			public boolean accept(File f) {
				String name = f.getName().toLowerCase();
				if (name.endsWith(".tmp") || name.endsWith("~") || name.endsWith(".bak") || name.endsWith(".xcf")
						|| name.equals(".svn") || name.equals(".cvs")) {
					return false;
				}
				return true;
			}
		};

		/**
		 * Files which will be copied only and not processed by wiki2xhtml.
		 */
		public static final java.io.FileFilter copyOnly = new java.io.FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				String name = f.getName();
				return
					name.endsWith(".html") || name.endsWith(".htm");
			}
		};

		public static final FileFilter scriptFile = new FileFilter() {
			@Override
			public boolean accept(File f) {
				Matcher m = RegExpressions.scriptModeFile.matcher(f.getName());
				return m.find();
			}
			@Override
			public String getDescription() {
				return "Script file, e.g. php or asp";
			}
		};

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
