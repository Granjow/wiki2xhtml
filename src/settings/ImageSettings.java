package src.settings;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Constants;
import src.Container_Files;
import src.Container_Resources;
import src.GenerateID;
import src.Resources;
import src.Template;
import src.Constants.SettingsE;
import src.Constants.SettingsImgE;
import src.Constants.SettingsImgPageE;
import src.argumentHandler.ArgumentItem;
import src.argumentHandler.ArgumentReader;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.utilities.IOUtils;
import src.utilities.StringTools;

import static src.Constants.SettingsImgE;
import static src.Constants.SettingsImgPageE;

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
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * Reads and handles image settings
 *
 * @since wiki2xhtml 3.3, 2&#x2a2f faster than the old one
 *
 * @author Simon Eugster
 *
 */
public class ImageSettings {

	private static final CommentAtor ca = CommentAtor.getInstance();

	/** Settings for current page */
	public Page page = new Page();

	/** Settings for one image */
	public Image image = new Image();

	private ImageSettings() { }
	private static ImageSettings is = new ImageSettings();
	/** Singleton */
	public static ImageSettings getInstance() {
		return is;
	}

	public Template tDesc, tBlank, pDesc, pBlank;


	/**
	 * Replaces characters like "&amp;" and " " by "&amp;amp;" and "%20"
	 * @param s
	 * @return W3C conform URI String
	 */
	public static String htmlURI(String s) {
		for (String[] es : Resources.entities)
			s = s.replaceAll(es[1], es[0]);
		return s;
	}

	/**
	 * Clears page specific settings
	 */
	public void nextPage(String pagename) {
		image = new Image();
		page.set_(SettingsImgPageE.pageName, pagename);
	}

	/**
	 * Clears the settings for the specific item
	 */
	public void nextItem() {
		image = new Image();
	}

	/**
	 * Contains the settings for images on one page.
	 * Overrides {@link src.settings.ImageSettings.All}
	 */
	public static class Page extends src.settings.Settings<SettingsImgPageE, String> {
		
		public int counterGalleries = 0;

		public Page() {
		}
		
		@Override
		public String nullValue() {
			return "null";
		}
		@Override
		boolean setCheck(SettingsImgPageE property, String value) {
			return true;
		}

		/** Returns the gallery ID in the form of gallery001 */
		public String getIdGallery() {
			StringBuilder sb = new StringBuilder();
			
			String counter = "" + counterGalleries;
			for (byte i = 0; i < 2 - Math.round(Math.log10(Integer.parseInt(counter))); i++)
				sb.append("0");
			sb.append(counter);
			
			return "gallery" + sb;
		}


		/** @return The page specific image page directory */
		public String getImagepagesDir() {
			XhtmlSettings xhs = XhtmlSettings.getInstance();
			String dir = xhs.local.getLocalOrGlobal_(SettingsE.imagepagesDir);

			dir.replaceAll("\\/\\/+", "/");

			return dir.endsWith("/") ? dir : dir + "/";
		}

		public String getBacklinkDir() {
			String dirImagepages = getImagepagesDir();

			int n = StringTools.countString(dirImagepages, "/");

			if (dirImagepages.startsWith("./")) n--;
			if (!dirImagepages.endsWith("/")) n++;

			StringBuffer backlink = new StringBuffer();

			for (int i = 0; i < n; i++) {
				backlink.append("../");
			}

			return backlink.toString();
		}
	}

	/**
	 * Contains the settings for one image.
	 * Overrides {@link src.settings.ImageSettings.All}.
	 */
	public static class Image extends src.settings.Settings<Constants.SettingsImgE, String> {
		
		public Image() {
			set_(SettingsImgE.galleryCounter, "0");
		}
		
		public void append_(final SettingsImgE property, final String value) {
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
		boolean setCheck(SettingsImgE property, String value) {
			boolean ok = true;
			
			switch(property) {
			case galleryCounter:
				try {
					int i = Integer.parseInt(value);
					if (i < 0) throw new Error("Negative value! " + value);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					ok = false;
				}
				break;
			}

			return ok;
		}

		

		/** Image ID */
		public String getID() {
			String path = get_(SettingsImgE.imagePath);
			if (path == null) path = "";
			return GenerateID.getHexMD5id(path, "", true);
		}





		/**
		 * Reads arguments from an image tag ([[image:args]]).
		 * @param args Arguments (without image: and square brackets)
		 */
		public void readArguments(final String args, final boolean textOnly) {

			if (textOnly) {
				set_(SettingsImgE.galleryText, args);
				return;
			}

			ArrayList<ArgumentItem> a = ArgumentReader.getArguments(args);

			if (a.size() > 0) {
				set_(SettingsImgE.imagePath, a.get(0).fullArg);
				a.remove(0);
			}

			Pattern pWidth = Pattern.compile("^\\d+(?:px|%)$");
			Matcher mWidth;
			Pattern pPWidth = Pattern.compile("^pw(?:idth)?=(\\d+(?:px|%))");
			Matcher mPWidth;

			for (ArgumentItem ai : a) {

				/* Display thumbnail? */
				if (Constants.Images.thumb.equals(ai.fullArg)) {
					set_(SettingsImgE.thumbEnabled, "true");
					continue;
				}

				/* Position */
				if (Constants.Position.left.equals(ai.fullArg)
						|| Constants.Position.right.equals(ai.fullArg)
						|| Constants.Position.center.equals(ai.fullArg)) {
					set_(SettingsImgE.thumbPosition, ai.fullArg);
					continue;
				}

				/* Direct? */
				if (Constants.Images.direct.equals(ai.fullArg)) {
					set_(SettingsImgE.linkDirect, "true");
					continue;
				}

				/* Small image? */
				if (Constants.Images.small.equals(ai.fullArg)) {
					set_(SettingsImgE.imageSmall, "true");
					continue;
				}
				
				/* Clear? */
				if (Constants.Images.clear.equals(ai.fullArg)) {
					set_(SettingsImgE.clearAfter, "true");
					set_(SettingsImgE.clearBefore, "true");
					continue;
				}
				if (Constants.Images.clearBefore.equals(ai.fullArg)) {
					set_(SettingsImgE.clearBefore, "true");
					continue;
				}
				if (Constants.Images.clearAfter.equals(ai.fullArg)) {
					set_(SettingsImgE.clearAfter, "true");
					continue;
				}

				
				/* Additional arguments */
				if (ai.fullArg.startsWith(Constants.Images.argsLink)) {
					set_(SettingsImgE.argsLink, ai.fullArg.substring(Constants.Images.argsLink.length()));
					continue;
				}


				/* Width */
				mWidth = pWidth.matcher(ai.fullArg);
				if (mWidth.find()) {
					set_(SettingsImgE.thumbWidth, mWidth.group());
					continue;
				}

				/* Thumbnail source */
				if (ai.fullArg.startsWith(Constants.Images.pathThumb)) {
					set_(SettingsImgE.thumbSrc, ai.fullArg.substring(Constants.Images.pathThumb.length()));
					set_(SettingsImgE.thumbEnabled, "true");
					continue;
				}

				/* Caption */
				if (ai.fullArg.startsWith(Constants.Images.captionShort)) {
					set_(SettingsImgE.imageCaption, ai.fullArg.substring(2));
					continue;
				}
				if (ai.fullArg.startsWith(Constants.Images.caption)) {
					set_(SettingsImgE.imageCaption, ai.fullArg.substring(8));
					continue;
				}

				/* Long description */
				if (ai.fullArg.startsWith(Constants.Images.longDesc)) {
					set_(SettingsImgE.imageLongdesc, ai.fullArg.substring(3));
					continue;
				}

				/* Image page width */
				mPWidth = pPWidth.matcher(ai.fullArg);
				if (mPWidth.find()) {
					set_(SettingsImgE.pageWidth, mPWidth.group(1));
					continue;
				}

				/* Description */
				if (contains(SettingsImgE.imageDesc)) {
					append_(SettingsImgE.imageDesc, "|");
					continue;
				}
				if (ai.fullArg.length() > 0) append_(SettingsImgE.imageDesc, ai.fullArg);
			}

			// Check for non-ASCII characters
			Matcher m = Pattern.compile(Resources.pNotAscii).matcher(get_(SettingsImgE.imagePath));
			if (m.find()) {
				ca.o("Image contains non-ASCII characters! They should be removed.", CALevel.ERRORS);
			}
		}


		/** Returns the specific width for the image on the image page */
		public String getWidthPage() {
			XhtmlSettings xhs = XhtmlSettings.getInstance();
			return contains(SettingsImgE.pageWidth) ? get_(SettingsImgE.pageWidth) : xhs.local.getLocalOrGlobal_(SettingsE.imagepageImgWidth);
		}

		/** @return The attribute for the width of the image on the image page */
		public String getWidthPageAttrib() {
			return getWidthPage().length() > 0 ? "width=\"" + getWidthPage() + "\"" : "";
		}

		/** Returns the specific thumbnail width */
		public String getWidthThumb() {
			XhtmlSettings xhs = XhtmlSettings.getInstance();
			if (contains(SettingsImgE.thumbEnabled)) {
				return contains(SettingsImgE.thumbWidth) ? get_(SettingsImgE.thumbWidth) : xhs.local.getLocalOrGlobal_(SettingsE.thumbWidth);
			} else {
				return contains(SettingsImgE.thumbWidth) ? get_(SettingsImgE.thumbWidth) : "";
			}
		}

		/** @return The attribute for the thumbnail width */
		public String getWidthThumbAttrib() {
			return getWidthThumb().length() > 0 ? "width=\"" + getWidthThumb() + "\"" : "";
		}


		/**
		 * Gets the filename of the image.
		 * @return Image filename without the path
		 */
		public String getImageFilenameHtml() {
			String path = get_(SettingsImgE.imagePath);
			return htmlURI(path.substring(path.lastIndexOf('/') + 1));
		}

		/** @return Image path with escaped spaces and &amp;&#x2019;s */
		public String getImagePathHtml() {
			return htmlURI(getImagePath());
		}

		/**
		 * Builds the filename and path for the image page.
		 * @return Image page path
		 */
		public String getImagepagePath(boolean filesystemPath, boolean filenameOnly) {
			if (!contains(SettingsImgE.imagePath)) {
				ca.ol("No image path available! Unable to getImagepagePath().", CALevel.ERRORS);
				return null;
			}
			StringBuffer name = new StringBuffer();

			if (!filenameOnly) {
				if (filesystemPath) {
					name.append(Container_Files.getInstance().cont.targetDir().getAbsolutePath() + File.separatorChar);
				}
				name.append(is.page.getImagepagesDir());

				if (filesystemPath) {
					// Path for the file is required, likely to create it, so create the sub-directory if necessary
					File f = new File(name.toString());
					if (!f.exists())
						f.mkdirs();
				}
			}

			name.append(is.page.get_(SettingsImgPageE.pageName).replace(".", "-"));
			name.append("_to_");
			name.append(get_(SettingsImgE.imagePath).replace(".", "-").replace("/", "+").replace(" ", "_"));
			name.append(".html");

			return name.toString();
		}


		/** @return The link back to the calling image or thumbnail */
		public String getBacklinkPage() {
			ImageSettings is = ImageSettings.getInstance();
			return is.page.getBacklinkDir() + is.page.get_(SettingsImgPageE.pageName) + "#" + getID();
		}


		/** @return The appropriate template */
		public Template getTemplate() {
			String text = get_(SettingsImgE.galleryText);
			String desc = get_(SettingsImgE.imageDesc);
			if (contains(SettingsImgE.galleryEnabled)) {
				if (text != null) {		// Text only
					return new Template(
							   new File(Container_Files.getInstance().cont.styleDirAbsolutePath() + File.separatorChar + "galleryText.html"),
							   Container_Resources.readResource(Container_Resources.sgalleryText)
						   );
				} else {
					if (desc != null) {	// Thumbnail with description
						return new Template(
								   new File(Container_Files.getInstance().cont.styleDirAbsolutePath() + File.separatorChar + "galleryImage.html"),
								   Container_Resources.readResource(Container_Resources.sgalleryImage)
							   );
					} else {					// Thumbnail without description
						return new Template(
								   new File(Container_Files.getInstance().cont.styleDirAbsolutePath() + File.separatorChar + "galleryImageNodesc.html"),
								   Container_Resources.readResource(Container_Resources.sgalleryImageNodesc)
							   );
					}

				}
			}
			if (contains(SettingsImgE.thumbEnabled)) {
				if (desc != null)		// Thumbnail with description
					return new Template(
							   new File(Container_Files.getInstance().styleDirAbsolutePath() + File.separatorChar + "thumbPictureT.html"),
							   Container_Resources.readResource(Container_Resources.sthumbPictureT)
						   );
				else						// Thumbnail without description
					return new Template(
							   new File(Container_Files.getInstance().styleDirAbsolutePath() + File.separatorChar + "thumbPicture.html"),
							   Container_Resources.readResource(Container_Resources.sthumbPicture)
						   );
			} else {
				if (desc != null)		// Image with description
					return new Template(
							   new File(Container_Files.getInstance().styleDirAbsolutePath() + File.separatorChar + "pictureT.html"),
							   Container_Resources.readResource(Container_Resources.spictureT)
						   );
				else						// Image without description
					return new Template(
							   new File(Container_Files.getInstance().styleDirAbsolutePath() + File.separatorChar + "picture.html"),
							   Container_Resources.readResource(Container_Resources.spicture)
						   );
			}
		}

		/**
		 *
		 * @return The path to the image, including a standard image path
		 */
		public String getImagePath() {
			String path = get_(SettingsImgE.imagePath);
			if (path == null) path = "";
			if (path.startsWith("./") || path.startsWith("../")) {
				return path;
			} else {
				return XhtmlSettings.getInstance().local.dirImages() + path;
			}
		}

		/**
		 * Creates the path to the thumbnail.
		 * @return Relative thumbnail path
		 * @throws IOException
		 */
		public String getThumbSrc() throws IOException {
			String thumbSrc = "";
			if (contains(SettingsImgE.thumbEnabled) || contains(SettingsImgE.galleryEnabled)) {
				if (contains(SettingsImgE.thumbSrc)) {
					return get_(SettingsImgE.thumbSrc);
				} else {
					
					String path = get_(SettingsImgE.imagePath);
					if (path == null) path = "";

					if (path.endsWith(".pdf")) {

						/*
						 * Use the pdf image for PDF files, if the user
						 * didn't already give one
						 */
						thumbSrc = setThumb(Container_Resources.getInstance().rPDFCrystal, Container_Resources.getInstance().iPDFCrystal);

					} else if (path.endsWith(".odt") || path.endsWith(".ods") || path.endsWith(".odp") || path.endsWith(".odg")) {

						thumbSrc = setThumb(Container_Resources.getInstance().rOOo, Container_Resources.getInstance().iOOo);

					} else if (path.endsWith(".tex")) {

						thumbSrc = setThumb(Container_Resources.getInstance().rTEX, Container_Resources.getInstance().iTEX);

					} else if (path.endsWith(".xcf")) {

						thumbSrc = setThumb(Container_Resources.getInstance().rXCF, Container_Resources.getInstance().iXCF);

					} else if (path.endsWith(".tt")) {

						thumbSrc = setThumb(Container_Resources.getInstance().rTT, Container_Resources.getInstance().iTT);

					} else if (path.endsWith(".deb")) {

						thumbSrc = setThumb(Container_Resources.getInstance().rDEB, Container_Resources.getInstance().iDEB);

					} else if (path.endsWith(".rpm")) {

						thumbSrc = setThumb(Container_Resources.getInstance().rRPM, Container_Resources.getInstance().iRPM);

					} else if (path.endsWith(".zip") || path.endsWith(".tbz") || path.endsWith(".bz2")
							   || path.endsWith(".tar") || path.endsWith(".gz") || path.endsWith(".ace")
							   || path.endsWith(".rar") || path.endsWith(".7z") || path.endsWith(".cab")
							  ) {

						thumbSrc = setThumb(Container_Resources.getInstance().rXCF, Container_Resources.getInstance().iXCF);

					} else {
						thumbSrc = getThumbnailSource(path);
					}
					return thumbSrc;
				}
			} else { // no thumbnail desired
				return get_(SettingsImgE.imagePath);
			}
		}

		private String setThumb(File fIcon, java.io.InputStream is) throws IOException {
			String src = fIcon.getName();

			File t = new File(Container_Files.getInstance().cont.targetDir().getAbsolutePath()
							  + File.separatorChar + XhtmlSettings.getInstance().local.getLocalOrGlobal_(SettingsE.thumbsDir)
							  + File.separatorChar + src);

			if (!t.exists()) {
				IOUtils.copyFile(is, new FileOutputStream(t));
			}

			if (!contains(SettingsImgE.thumbWidth)) {
				set_(SettingsImgE.thumbWidth, "100px");
			}
			return src;
		}

	}


	/**
	 * Replaces: (example: <code>dir/image.jpg</code>)
	 * <ul>
	 * <li>%n &#x2013; full name: <code>dir/image.jpg</code></li>
	 * <li>%f &#x2013; file name: <code>image.jpg</code><li>
	 * <li>%d &#x2013; directory: <code>dir</code></li>
	 * <li>%w &#x2013; without extension: <code>image</code></li>
	 * <li>%e &#x2013; file extension: <code>.jpg</code></li>
	 * </ul>
	 * @param nameThumb The name of the thumbnail
	 * @return The location of a thumbnail
	 */
	public static String getThumbnailSource(String nameThumb) {
		String out = XhtmlSettings.getInstance().local.getLocalOrGlobal_(SettingsE.thumbsDir);
		if (out == null) out = "";
		if (out.length() > 0) {

			// Start replacing
			String n = nameThumb, f = "", d = "", w = "", e = "";
			if (nameThumb.indexOf("/") >= 0) {
				f = nameThumb.substring(nameThumb.lastIndexOf("/") + 1, nameThumb.length());
				d = nameThumb.substring(0, nameThumb.lastIndexOf("/"));
			} else
				f = nameThumb;

			if (nameThumb.indexOf(".") >= 0) {
				w = nameThumb.substring(0, nameThumb.lastIndexOf("."));
				e = nameThumb.substring(nameThumb.lastIndexOf("."), nameThumb.length());
			} else
				w = nameThumb;
			out = out.replace("%n", n);
			out = out.replace("%f", f);
			out = out.replace("%d", d);
			out = out.replace("%w", w);
			out = out.replace("%e", e);

		} else
			out = nameThumb;

		return out;
	}

}
