package src.project.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Constants;
import src.Container_Resources;
import src.Resources;
import src.argumentHandler.ArgumentItem;
import src.argumentHandler.ArgumentReader;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.resources.ResProjectSettings.EImageProperties;
import src.resources.ResProjectSettings.EImageContext;
import src.resources.ResProjectSettings.SettingsE;
import src.templateHandler.Template;
import src.utilities.XMLTools;

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
public class ImageProperties extends StringSettings<EImageProperties> {
	
	public boolean imagepageCreated;
	
	/** The previous image on the page */
	public ImageProperties previousIP = null;
	/** The next image on the page */
	public ImageProperties nextIP = null;
	
	public final WikiFile parentFile;

	public ImageProperties(WikiFile parent) {
		parentFile = parent;
		imagepageCreated = false;
		
		// Add the ID generator
		addPreparser(new ValuePreparser<String>() {
			public String adjust(String value) {
				String number = "unknown";
				String path = "";
				if (isSet(EImageProperties.number)) { number = get_(EImageProperties.number); }
				if (isSet(EImageProperties.path)) { path = get_(EImageProperties.path); }
				return XMLTools.getXmlNameChar(String.format("img_%s_%s", number, path));
			};
		}, EImageProperties.id);
		// Set a fake ID so that the property is not null
		set_(EImageProperties.id, "fakeID");
	}
	
	/**
	 * @return A placeholder for an image. Used for replacing image tags and insert the code
	 * afterwards after the images have been linked.
	 */
	public String getPlaceholder() {
		return String.format(">>>image-%s-placeholder<<<", get_(EImageProperties.number));
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
		String path;
		if (isSet(EImageProperties.path)) {
			path = get_(EImageProperties.path);
		} else {
			path = "";
		}
		if (path.startsWith("/") || path.startsWith("./") || path.startsWith("../") || !parentFile.isPropertySet(SettingsE.imagesDir, true)) {
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
		if (get_(EImageProperties.context) == null || get_(EImageProperties.context).equals(EImageContext.thumb.property)) {
			return new Template(Container_Resources.sTplImage, parentFile.project);
		} else {
			return new Template(Container_Resources.sTplGallery, parentFile.project);
		}
	}
	






	/**
	 * Reads arguments from an image tag ([[image:args]]).
	 * @param args All image arguments, like &ldquo;<code>Image:venus.jpg|244px|The Venus</code>&rdquo;
	 */
	public void readArguments(final String args) {

		ArrayList<ArgumentItem> a = ArgumentReader.getArguments(args);

		Pattern pWidth = Pattern.compile("^\\d+(?:px|%)$");
		Matcher mWidth;
		Pattern pPWidth = Pattern.compile("^pw(?:idth)?=(\\d+(?:px|%))");
		Matcher mPWidth;
		Pattern pImage = Pattern.compile("(?i)(?:image|bild|file):(.+)");
		Matcher mPImage;

		for (ArgumentItem ai : a) {
			
			mPImage = pImage.matcher(ai.fullArg);
			if (mPImage.matches()) {
				set_(EImageProperties.path, mPImage.group(1));
				continue;
			}

			/* Display thumbnail? */
			if (Constants.Images.thumb.equals(ai.fullArg)) {
				set_(EImageProperties.thumbEnabled, "true");
				continue;
			}

			/* Position */
			if (Constants.Position.left.equals(ai.fullArg)
					|| Constants.Position.right.equals(ai.fullArg)
					|| Constants.Position.center.equals(ai.fullArg)) {
				set_(EImageProperties.pos, ai.fullArg);
				continue;
			}

			/* Direct? */
			if (Constants.Images.direct.equals(ai.fullArg)) {
				set_(EImageProperties.direct, "true");
				continue;
			}

			/* Small image? */
			if (Constants.Images.small.equals(ai.fullArg)) {
				set_(EImageProperties.small, "true");
				continue;
			}
			
			/* Clear? */
			if (Constants.Images.clear.equals(ai.fullArg)) {
				set_(EImageProperties.clear, "both");
				continue;
			}
			if (ai.fullArg.startsWith(Constants.Images.clear + "=")) {
				set_(EImageProperties.clear, ai.fullArg.substring(Constants.Images.clear.length() + 1));
				continue;
			}
			
			/* Additional arguments */
			if (ai.fullArg.startsWith(Constants.Images.argsLink)) {
				set_(EImageProperties.argsLink, ai.fullArg.substring(Constants.Images.argsLink.length()));
				continue;
			}


			/* Width */
			mWidth = pWidth.matcher(ai.fullArg);
			if (mWidth.find()) {
				set_(EImageProperties.thumbWidth, mWidth.group());
				continue;
			}

			/* Thumbnail source */
			if (ai.fullArg.startsWith(Constants.Images.pathThumb)) {
				set_(EImageProperties.thumb, ai.fullArg.substring(Constants.Images.pathThumb.length()));
				continue;
			}

			/* Caption */
			if (ai.fullArg.startsWith(Constants.Images.captionShort)) {
				set_(EImageProperties.caption, ai.fullArg.substring(2));
				continue;
			}
			if (ai.fullArg.startsWith(Constants.Images.caption)) {
				set_(EImageProperties.caption, ai.fullArg.substring(8));
				continue;
			}

			/* Long description */
			if (ai.fullArg.startsWith(Constants.Images.longDesc)) {
				set_(EImageProperties.longdesc, ai.fullArg.substring(3));
				continue;
			}

			/* Image page width */
			mPWidth = pPWidth.matcher(ai.fullArg);
			if (mPWidth.find()) {
				set_(EImageProperties.pageWidth, mPWidth.group(1));
				continue;
			}

			/* Description */
			if (ai.fullArg.length() > 0) {
				if (isSet(EImageProperties.text)) {
					append_(EImageProperties.text, "|");
				}
				append_(EImageProperties.text, ai.fullArg);
			}
		}

		// Check for non-ASCII characters
		Matcher m = Pattern.compile(Resources.pNotAscii).matcher(get_(EImageProperties.path));
		if (m.find()) {
//			ca.o("Image contains non-ASCII characters! They should be removed.", CALevel.ERRORS);
		}
	}
	
	
	public static void main(String[] args) {
		WikiFile wf = new VirtualWikiFile(null, "name", false, false, new StringBuffer());
		ImageProperties prop = new ImageProperties(wf);
		prop.readArguments("Image:nowhere.jpg|thumb|right|this is a description");
		prop.print(": ");
	}
	
}
