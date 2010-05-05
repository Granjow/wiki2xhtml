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

import src.Args.GetPolicyE;
import src.settings.XhtmlSettingsReader;


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
		public static final String target = "html";
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

	}

	/** Default settings like thumbnail width */
	public static final class Standards {

		public static final Locale locale = Locale.ENGLISH;

		public static String widthImgImagepages = "700px";
		public static String widthThumbs = "200px";
		public static String galleryWidthThumbs = "125px";

		public static String galleryImagesPerLine = "3";

		public static final String useImageDescAsCaption = "true";
		public static final String useImagenameAsCaption = "false";
	}


	/** Command line arguments for wiki2xhtml */
	public static final class Arguments {

		public static final Args stdArgs = new Args("-m menu.txt -f footer.txt -c common.txt ");

		public static final Args stdArgsHelpDe = new Args("-m menu-de.txt -f footer-de.txt -c common-de.txt " +
				"-s hd --source-dir=doc/txt --target-dir=help err404.php help-de.txt doc-de.txt index-de.txt download-de.txt quickstart-de.txt " +
				"news-de.txt changelog-de.txt faq-de.txt examples-de.txt ex-php-reload-de.php " +
				"php-de.txt doc-design-de.txt quickref-de.txt about-de.txt links-de.txt usage-de.txt");

		public static final Args stdArgsHelpEn = new Args(stdArgs.getArgs(GetPolicyE.AllArgs) +
				" -s hd --source-dir=doc/txt --target-dir=help err404.php index.txt help.txt doc.txt download.txt quickstart.txt quickstart-ru.txt quickstart-it.txt news.txt faq.txt php.txt " +
				"examples.txt ex-php-reload.php doc-design.txt quickref.txt about.txt changelog.txt testpage.txt links.txt usage.txt");

		/** Special command line arguments */
		public static final class Special {

			public static final String menufileUpdater = "menu";
			public static final String templateUpdater = "template";

		}

		/** Command line arguments (like -f footer.txt) for wiki2xhtml */
		public static final class Arg {

			public static final String menu = "-m";
			public static final String style = "-s";
			public static final String common = "-c";
			public static final String sourceDir = "--sd";
			public static final String targetDir = "--td";
			public static final String footer = "-f";

		}

		/** Combined command line arguments (like --footer=f.txt) for wiki2xhtml */
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

		/** Command line flags (like --dead) for wiki2xhtml with no further arguments */
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

	/** Arguments for images */
	public static final class Images {

		/** Additional HTML arguments for the image
		public static final String args = "args=";
		/** Additional HTML arguments for the image link, e.g. for use with plug-ins
		 * @since wiki2xhtml 3.4 */
		public static final String argsLink = "argsLink=";
		/** Clear before and after (insert clear=both) to put it on a new line
		 * @since wiki2xhtml 3.4 */
		public static final String clear = "clear";
		/** Clear before an image
		 * @since wiki2xhtml 3.4 */
		public static final String clearBefore = "clear:before";
		/** Clear after an image
		 * @since wiki2xhtml 3.4 */
		public static final String clearAfter = "clear:after";
		/** Direct link, no image page */
		public static final String direct = "direct";
		/** Image is small, do not enlarge it on the image page */
		public static final String small = "small";
		/** Insert a thumbnail with border and image page*/
		public static final String thumb = "thumb";
		/** Self-defined path to the thumbnail
		 *  @since wiki2xhtml 3.4: Creates thumbnail (Argument thumb no longer necessary) */
		public static final String pathThumb = "thumb=";

		/** Image caption */
		public static final String captionShort = "c=";
		/** Image caption */
		public static final String caption = "caption=";
		/** Long description of the image */
		public static final String longDesc = "ld=";
		
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

	/** Tags used in templates */
	public static final class TemplateTags {

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
		public static final Pattern regexHead = Resources.Regex.reckHead;
		public static final String textheader = "[textheader]";
		public static final String menu = "[menu]";
		public static final String footer = "[footer]";
		public static final String h1 = "[h1]";
		public static final String homelink = "[homelink]";
		public static final String content = "[content]";
		
		/** @since wiki2xhtml 3.4 */
		public static final String toc = "[toc]";

	}

	/**
	 * @see {@link XhtmlSettingsReader#autoReadableSettings}
	 */
	public static enum SettingsE {
		/**
		 * Number of images per line in the gallery
		 */									galleryImagesPerLine ("GalleryImagesPerLine"),
		/** Width of thumbnails in galleries 
		 */									galleryThumbWidth ("GalleryThumbWidth", Settings.argSize),
		/** Width for images on image pages 
		 */									imagepageImgWidth ("ImageWidthImagepage", Settings.argSize),
		/** Width of thumbnails */			thumbWidth ("ThumbWidthImages", Settings.argSize),
		/** Meta data: Author */			author ("Author"),
		/**
		 * Use the image description as caption if no caption available
		 * @see #nameForCaption
		 * @see {@link Constants.Standards#useImageDescAsCaption}
		 */									descForCaption ("DescForCaption"),
		/** Meta data: Page description */	desc ("Desc(?:ription)?"),
		/** Default title for all pages */	defaultTitle ("DefaultTitle"),
		/** Page heading */					h1 ("H1"),
		/** Link to the index page */		homelink ("Homelink"),
		/** Meta data: Page favicon */		icon ("Icon"),
		/** Directory for iamge pages */	imagepagesDir ("DirImagepages"),
		/** Directory for images */			imagesDir ("DirImages"),
		/** Meta data: page keywords */		keywords ("Keywords", true, ","),
		/** Meta data: Page language */		lang ("Lang"),
		/** Custom meta data */				meta ("Meta", true, "\n", ":((?:(?!\\}\\})[^\\n])+)"),
		/**
		 * Don&#x2019;t use image name as caption if no caption available
		 * @see #descForCaption
		 * @see {@link Constants.Standards#useImagenameAsCaption}
		 */									nameForCaption ("NameForCaption"),
		/** 
		 * Namespace for links (Like w=http://de.wikipedia.org/wiki/%s) 
		 * @since wiki2xhtml 3.4 
		 */									namespace("Namespace", true, ","),
		/**
		 * Alternative reck file to use 
		 * @since 3.3.2 
		 */									reckAlternative ("ReckAlternative"),
		/** Text title/header */			textHeader ("TextHeader"),
		/** Thumbnails directory */			thumbsDir ("Thumbnails"),
		/**
		 * Page title; replacement for %s 
		 */									title ("Title"),
		/** Rule for image page titles */	titleRule ("TitleRule");

		private final String property;
		private boolean loop = false;
		private final String separator;
		private String regexExtension = ":((?:(?!\\}\\}).)+)";
		private Pattern regex = null;

		SettingsE(String property) {
			this(property, false, null, null);
		}
		SettingsE(String property, String regexExtension) {
			this(property, false, null, regexExtension);
		}
		SettingsE(String property, boolean loop, String separator) {
			this(property, loop, separator, null);
		}
		SettingsE(String property, boolean loop, String separator, String regexExtension) {
			this.loop = loop;
			this.property = property;
			this.separator = separator;
			if (regexExtension != null) this.regexExtension = regexExtension;
		}

		public String keyword() {
			return property;
		}
		public Pattern regex() {
			if (regex == null) regex = Pattern.compile("(?m)\\{\\{" + keyword() + regexExtension + "\\}\\}");
			return regex;
		}
		/** @return true, if keyword may occur multiple times. 
		 * @since wiki2xhtml 3.4 */
		public boolean loop() {
			return loop;
		}
		/** @return The separator used to separate content if found multiple times. 
		 * @since wiki2xhtml 3.4 */
		public String separator() {
			return separator;
		}

	}
	
	public static enum SettingsLocalE {
		/** Header content for JavaScript, CSS and so on 
		 */										head,
		/** Page menu. Generated outside! */	menu,
		/** Page number (when multipage) */		pageNumber,
		/** Total number of pages */			pagesTotal,
		/** Link to redirected page (given with #REDIRECT [[Link]]) 
		 */										redirect,
		/** Script content, e.g. PHP code */	script,
		/** "true" if the current page contains a script (PHP e.g.) 
		 */										scriptMode,
		/** Text title (aka text header) */		textTitle;
	}
	 
	public static enum SettingsImgE {
		/** Additional arguments */				args,
		/** Additional link arguments */		argsLink,
		/** Insert clear=both before */			clearAfter,
		/** Insert clear=both after */			clearBefore,
		/** Gallery counter */					galleryCounter,
		/** Gallery */							galleryEnabled,
		/** Text (gallery entries) */			galleryText,
		/** Image caption */					imageCaption,
		/** Image description */				imageDesc,
		/** Long image description */			imageLongdesc,
		/** Image path */						imagePath,
		/** Has the image page been created? */	imagePageWasCreated,
		/** Small image? (No with has to be set for the image page) 
		 */										imageSmall,
		/** Direct link without image page? */	linkDirect,
		/** Link to previous image */			linkPrev,
		/** Link to next image */				linkNext,
		/** Image width on the image page */	pageWidth,
		/** Thumbnail? */						thumbEnabled,
		/** Thumbnail position */				thumbPosition,
		/** Thumbnail width */					thumbWidth,
		/** Thumbnail source */					thumbSrc;
	}
	
	public static enum SettingsImgPageE {
		/** Page name */						pageName;
	}

	public static final class Settings {

		public static final char separator = ':';
		public static final String arg = separator + "([^}]+)";
		public static final String argSize = separator + "(\\d+(?:%|px))";
		
		/** For namespaces: If namespace w is defined, then the w: in the name of a link to w:Somewhere will be cut off */
		public static final String argCut = "|cut";

//		public static final String consistentGallery = "ConsistentGallery";	// Removed in wiki2xhtml 3.4

		/**
		 * @since 3.1.1
		 */
		public static final class Args {
			// Standard arguments
			public static final String argFalse = ":false";
		}

	}

	/** Tags which will be replaced in the document, e.g. by the current version */
	public static final class Tags {

		public static final String anchor = "~~(.+?)~~";
		public static final String splitPageNav = "{{$SplitPageNav}}";
		public static final String version = "{{$Version}}";
		public static final String top = "{{Top}}";
		public static final Pattern regexToc = Resources.Regex.toc;
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
				Matcher m = Resources.Regex.scriptModeFile.matcher(f.getName());
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
