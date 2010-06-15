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
import src.argumentHandler.ArgumentItem;
import src.argumentHandler.ArgumentReader;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.resources.ResProjectSettings.SettingsE;
import src.utilities.IOUtils;
import src.utilities.StringTools;


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
 * Reads and handles image settings
 *
 * @since wiki2xhtml 3.3, 2&#x2a2f faster than the old one
 *
 */
public class ImageSettings {

	/**
	 * Contains the settings for one image.
	 * Overrides {@link src.settings.ImageSettings.All}.
	 */
	public static class Image extends src.settings.Settings<SettingsImgE, String> {

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









		/** @return The link back to the calling image or thumbnail */
		public String getBacklinkPage() {
			ImageSettings is = ImageSettings.getInstance();
			return is.page.getBacklinkDir() + is.page.get_(SettingsImgPageE.pageName) + "#" + getID();
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
