package src.project.settings;

import java.io.File;
import java.io.FileNotFoundException;

import src.Container_Resources;
import src.Resources;
import src.project.file.WikiFile;
import src.resources.ResProjectSettings.EImageProperties;
import src.resources.ResProjectSettings.EImageContext;
import src.resources.ResProjectSettings.SettingsE;
import src.templateHandler.Template;

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
 */

/**
 * Contains properties about a single image
 */
public class ImageProperties extends Settings<EImageProperties, String> {
	
	public boolean imagepageCreated;
	
	public final WikiFile parentFile;

	protected String concatenate(String left, String right) {
		return left + right;
	}
	public String nullValue() {
		return null;
	}
	public ImageProperties(WikiFile parent) {
		parentFile = parent;
		imagepageCreated = false;
	}
	
	/**
	 * Builds the filename and path for the image page.
	 * @return Image page path
	 */
	public String getImagepagePath(boolean filesystemPath, boolean filenameOnly) {
		if (!isSet(EImageProperties.path)) {
//			ca.ol("No image path available! Unable to getImagepagePath().", CALevel.ERRORS);
			return null;
		}
		StringBuffer name = new StringBuffer();

		if (!filenameOnly) {
			if (filesystemPath) {
				name.append(parentFile.project.targetDirectory.getAbsolutePath() + File.separatorChar);
			}
			name.append(parentFile.getProperty(SettingsE.imagepagesDir, true));

			if (filesystemPath) {
				// Path for the file is required, likely to create it, so create the sub-directory if necessary
				File f = new File(name.toString());
				if (!f.exists())
					f.mkdirs();
			}
		}

		name.append(parentFile.name.replace(".", "-"));
		name.append("_to_");
		name.append(get_(EImageProperties.path).replace(".", "-").replace("/", "+").replace(" ", "_"));
		name.append(".html");

		return name.toString();
	}
	
	/**
	 * Replaces characters like "&amp;" and " " by "&amp;amp;" and "%20"
	 * @param s
	 * @return W3C conform URI String
	 */
	private static String htmlURI(String s) {
		for (String[] es : Resources.entities)
			s = s.replaceAll(es[1], es[0]);
		return s;
	}
	
	/** @return Image path with escaped spaces and &amp;&#x2019;s */
	public String getImagePathHtml() {
		return htmlURI(getImagePath());
	}
	
	/**
	 * Gets the filename of the image.
	 * @return Image filename without the path
	 */
	public String getImageFilenameHtml() {
		String path = get_(EImageProperties.path);
		return htmlURI(path.substring(path.lastIndexOf('/') + 1));
	}
	
	/** 
	 * @return The path to the image. 
	 * Prefix is the Images directory if the given image path does not start with a <code>.{0,2}/</code> 
	 */
	public String getImagePath() {
		String path = get_(EImageProperties.path);
		if (path == null) path = "";
		if (path.startsWith("/") || path.startsWith("./") || path.startsWith("../")) {
			return path;
		} else {
			return parentFile.getProperty(SettingsE.imagesDir, true) + path;
		}
	}
	
	/**
	 * @return A template for the image, according to its context (gallery or non-gallery).
	 * @throws FileNotFoundException 
	 */
	public Template getTemplate() throws FileNotFoundException {
		if (get_(EImageProperties.context) == null || get_(EImageProperties.context).equals(EImageContext.gallery.property)) {
			return new Template(Container_Resources.sTplImage);
		} else {
			return new Template(Container_Resources.sTplGallery);
		}
	}
	
}
