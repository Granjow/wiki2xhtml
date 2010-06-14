package src.resources;

import java.util.regex.Pattern;

import src.Constants.Checkers;
import src.Constants.Preparsers;
import src.project.settings.Settings.Checker;
import src.project.settings.Settings.ValuePreparser;

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
//		/** Default title for all pages */	defaultTitle ("DefaultTitle"), // use title for project instead
		/** Meta data: Page description */	desc ("Desc(?:ription)?"),
		/** Footer */						footer ("Footer"),
		/**
		 * Number of images per line in the gallery
		 */									galleryImagesPerLine ("GalleryImagesPerLine", Checkers.positiveNonzeroByteChecker),
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
		/** Directory for image pages */	imagepagesDir ("DirImagepages", Preparsers.directoryTrailingSlashPreparser),
		/** Directory for images */			imagesDir ("DirImages", Preparsers.directoryTrailingSlashPreparser),
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
		public final Checker<String> checker;
		public final ValuePreparser<String> adjuster;
	
		private SettingsE(String property) { this(property, false, null, null); }
		private SettingsE(String property, Checker<String> checker) { this(property, false, null, checker); }
		private SettingsE(String property, ValuePreparser<String> adjuster) { this(property, false, null, null, adjuster); }
		private SettingsE(String property, boolean loop, String separator) { this(property, loop, separator, null); }
		private SettingsE(String property, boolean loop, String separator, Checker<String> checker) { this(property, loop, separator, checker, null); }
		
		private SettingsE(String property, boolean loop, String separator, Checker<String> checker, ValuePreparser<String> adjuster) {
			this.property = property;
			this.loop = loop;
			this.separator = separator;
			this.checker = checker;
			this.adjuster = adjuster;
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
	
	public static enum EImageProperties {
		argsLink ("argsLink", "Additional link arguments, e.g. when working with LightBox"),
		caption ("caption", "Image caption"),
		clear ("clear", "css:clear, may be before, after and both"),
		context ("context", "Image context: gallery or normal"),
		direct ("direct", "Link directly to the file"),
		file ("file", "Filename alone"),
		id ("id", "Automatically generated image ID"),
		link ("link", "Path to the image page or the image, depending on " +
				"whether the image page has been created or not"),
		meta ("meta", "Meta data"), // TODO fill in
		next ("next", "Link to next image on the page"), // TODO fill in
		number ("number", "The number of the image on the current page"),
		longdesc ("ld", "Long image description"),
		pageCreated ("pageCreated", "Image page created?"),
		pageWidth ("pageWidth", "Width of the image on the image page"),
		path ("path", "Image path"),
		pos ("pos", "Desired thumbnail position: left, center, right"),
		prev ("prev", "Link to previous image on the page"),  // TODO fill in
		small ("small", "Small image, to inform imagepage about not enlarging it"),
		text ("text", "Text provided"),
		title ("title", "What to use as title: caption, file, text"),
		thumb ("thumb", "Thumbnail path"),
		thumbEnabled ("thumbEnabled", "A thumbnail should be used"),
		thumbWidth ("thumbWidth", "Thumbnail width"),
		up ("up", "Link back to the page");	 // TODO fill in
		
		public final String property;
		public final String desc;
		private EImageProperties(String property, String desc) {
			this.property = property;
			this.desc = desc;
		}
	}

	/**
	 * Properties belonging to a specific image, like description, custom thumbnail, etc.
	 * @deprecated
	 */
	public static enum SettingsImgE {
		/** Additional arguments */				args,
		/** Additional link arguments */		argsLink,
		/** Insert clear=both before */			clearAfter,
		/** Insert clear=both after */			clearBefore,
		/** Gallery counter */					galleryCounter, //init: 0
		/** Image caption */					imageCaption,
		/** Image context: gallery, thumb etc. See {@link ImageContextE}*/
												imageContext,
		/** Long image description */			imageLongdesc,
		/** Image path */						imagePath,
		/** Has the image page been created? */	imagePageWasCreated,
		/** Small image? (No width has to be set for the image page) 
		 */										imageSmall, //make width=small?
		/** Direct link without image page? */	linkDirect,
		/** Link to previous image */			linkPrev,
		/** Link to next image */				linkNext,
		/** Image width on the image page */	pageWidth,
		/** Text, like image description */		text,
												textLong,
		/** Thumbnail position */				thumbPosition,
		/** Thumbnail width */					thumbWidth,
		/** Thumbnail source */					thumbSrc;
	}
	
	/**
	 * The context in which an image appears (gallery or standard thumbnail)
	 */
	public static enum EImageContext {
		/** In a gallery */						gallery ("gallery"),
		/** Ordinary thumbnail */				thumb ("thumb");
		public final String property;
		private EImageContext(String property) { this.property = property; }
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
