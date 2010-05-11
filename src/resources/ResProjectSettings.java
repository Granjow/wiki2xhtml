package src.resources;

import java.util.regex.Pattern;

import src.Constants.Checkers;
import src.project.settings.Settings.Checker;

public final class ResProjectSettings {

	public static enum ImagepageCaptionAlternatives {
		description ("Desc"),
		filename ("Filename");
		private final String key;
		private ImagepageCaptionAlternatives(String key) { this.key = key; }
		public final String key() { return key; }
	}
	
	/**
	 * This settings can be applied to either a page or the whole project.
	 * Can be read automatically. 
	 * TODO doc ..asCaption
	 */
	public static enum SettingsE {
		/** Meta data: Author */			author ("Author"),
		/** Default title for all pages */	defaultTitle ("DefaultTitle"),
		/** Meta data: Page description */	desc ("Desc(?:ription)?"),
		/** Footer */						footer ("Footer"),
		/**
		 * Number of images per line in the gallery
		 */									galleryImagesPerLine ("GalleryImagesPerLine", Checkers.positiveByteChecker),
		/** Width of thumbnails in galleries 
		 */									galleryThumbWidth ("GalleryThumbWidth", Checkers.sizeChecker),
		/** Page heading */					h1 ("H1"),
		/** Link to the index page */		homelink ("Homelink"),
		/** Meta data: Page favicon */		icon ("Icon"),
		/** Width for images on image pages 
		 */									imagepageImgWidth ("ImageWidthImagepage", Checkers.sizeChecker),
			/** What to use as imagepage title alternative
			 * @since wiki2xhtml 3.5 (multiple arguments before)
			 */ 								imagepageTitle ("ImagepageTitle"),
		/** Directory for image pages */	imagepagesDir ("DirImagepages"),
		/** Directory for images */			imagesDir ("DirImages"),
		/** Meta data: page keywords */		keywords ("Keywords", true, ","),
		/** Meta data: Page language */		lang ("Lang"),
		/** Custom meta data */				meta ("Meta", true, "\n"),
		/** 
		 * Namespace for links (Like w=http://de.wikipedia.org/wiki/%s) 
		 * @since wiki2xhtml 3.4 
		 */									namespace("Namespace", true, "\n"),
		/**
		 * Alternative reck file to use 
		 * @since 3.3.2 
		 */									reckAlternative ("ReckAlternative"),
		/** Text title/header */			textHeader ("TextHeader"),
		/** Thumbnails directory */			thumbsDir ("Thumbnails"),
		/** Width of thumbnails */			thumbWidth ("ThumbWidthImages", Checkers.sizeChecker),
		/**
		 * Page title; replacement for %s 
		 */									title ("Title"),
		/** Rule for image page titles */	titleRule ("TitleRule");
	
		private final String property;
		private final String separator;
		private final boolean loop;
		private final Pattern regex;
		private final Checker<String> checker;
	
		SettingsE(String property) { this(property, false, null, null); }
		SettingsE(String property, Checker<String> checker) { this(property, false, null, checker); }
		SettingsE(String property, boolean loop, String separator) { this(property, loop, separator, null); }
		
		SettingsE(String property, boolean loop, String separator, Checker<String> checker) {
			this.property = property;
			this.loop = loop;
			this.separator = separator;
			this.checker = checker;
			regex = Pattern.compile("(?m)\\{\\{" + keyword() + ":((?:(?!\\}\\}).)+)\\}\\}"); // basically {{Key:*}}
		}
	
		public String keyword() { return property; }
		public Pattern regex() { return regex; }
		
		/**
		 * @return true, if keyword may occur multiple times. 
		 * @since wiki2xhtml 3.4
		 */
		public boolean loop() {
			return loop;
		}
		/**
		 * @return The separator used to separate content if found multiple times. 
		 * @since wiki2xhtml 3.4
		 */
		public String separator() {
			return separator;
		}
		/**
		 * @return Checker validating values for this property 
		 * @since wiki2xhtml 3.5
		 */
		public Checker<String> checker() {
			return checker;
		}
		
	}

	/** 
	 * Each of these settings have to be read out in a different way.
	 * Cannot be done automatically as in SettingsE.
	 */
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
	
			public static final class Args {
				// Standard arguments
				public static final String argFalse = ":false";
			}
	
		}

}
