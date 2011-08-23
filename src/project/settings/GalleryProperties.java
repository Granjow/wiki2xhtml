/*
 *   Copyright (C) 2007-2011 Simon A. Eugster <simon.eu@gmail.com>

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

package src.project.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Constants.Template_Gallery;
import src.Constants.Template_Images;
import src.Constants;
import src.project.FallbackFile;
import src.project.file.WikiFile;
import src.ptm.PTMObject.RecursionException;
import src.ptm.PTMRootNode;
import src.ptm.PTMState;
import src.resources.ResProjectSettings.EGalleryProperties;
import src.resources.ResProjectSettings.SettingsE;
import src.tasks.WikiImages;


/**
 * Contains properties about a single gallery
 */
public class GalleryProperties extends StringSettings<EGalleryProperties> {

	public final WikiFile parentFile;
	/** An ordered list of all items in this Gallery container. */
	public final ArrayList<ImageProperties> imagePropertiesList;
	
	
	public PTMState sigma = null;
	
	
	/** caption="<caption>" */
	private static final Pattern pCaption = Pattern.compile("(?i)\\s(caption)=\"([^\"]*)\"");
	/** widths="<n>px" */
	private static final Pattern pWidths = Pattern.compile("(?i)\\s(widths)=\"(\\d+px)\"");
	/** heights="<n>px" */
	private static final Pattern pHeights = Pattern.compile("(?i)\\s(heights)=\"(\\d+px)\"");
	/** perrow="<n>" */
	private static final Pattern pPerrow = Pattern.compile("(?i)\\s(perrow)=\"(\\d+)\"");


	public GalleryProperties(WikiFile parentFile) {
		this.parentFile = parentFile;
		imagePropertiesList = new ArrayList<ImageProperties>();
		sigma = new PTMState();
	}
	
	/**
	 * @return A placeholder for an image. Used for replacing image tags and insert the code
	 * afterwards after the images have been linked.
	 */
	public String placeholder() {
		return String.format(">>>gallery-%s-placeholder<<<", get_(EGalleryProperties.number));
	}
	
	
	public void readArguments(String s) {
		
		Matcher m;
		
		for (Pattern p : new Pattern[] {pCaption, pWidths, pHeights, pPerrow} ) {
			m = p.matcher(s);
			if (m.find()) {
				sigma.bind(m.group(1), m.group(2));
			}
		}
		
		buildID();
		
//		System.out.println("Gallery arguments:");
//		sigma.printValues();
		
	}
	
	/**
	 * Generates a gallery from the available image properties. Includes writing the image pages.  
	 */
	public String generateGallery() throws IOException {
		StringBuffer content = new StringBuffer();
		linkImages();
		buildImagePages();
		
		int imagesPerLine = Integer.parseInt(parentFile.getProperty(SettingsE.galleryImagesPerLine, true));
		try {
			imagesPerLine = Integer.parseInt(sigma.resolve(EGalleryProperties.perrow.property));
		} catch (NumberFormatException e) {}
		
		int pos = 0;
		for (ImageProperties p : imagePropertiesList) {
			
			// Layout: Check the image's current position.
			// The template can decide how to realize n images per row.
			pos++;
			if (pos == 1) {
				p.argumentBindings.bind(Template_Images.rowStart, "true");
			}
			if (pos == imagesPerLine) {
				p.argumentBindings.bind(Template_Images.rowEnd, "true");
			}
			pos = pos % imagesPerLine;
			
			try {
				content.append(WikiImages.generateThumbnailEntry(p).toString());
			} catch (IOException e) {
				System.err.println("Error in file " + parentFile.internalName());
				e.printStackTrace();
			}
		}
		
		sigma.bind(EGalleryProperties.content.property, content.toString());
		
		FallbackFile template = parentFile.project.locateDefault(Constants.Templates.sTplGalleryContainer, parentFile);
		
		PTMRootNode root = new PTMRootNode(template.getContent(), sigma);
		try {
			return root.evaluate();
		} catch (RecursionException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private void buildImagePages() {
		for (ImageProperties p : imagePropertiesList) {
			// TODO consider only generated pages in linkImages()?
			WikiImages.generateImagepage(p);
		}
	}
	
	/**
	 * Updates the predecessor and the successor of each image in the list.
	 */
	private void linkImages() {
		ImageProperties previousIP = null;
		for (ImageProperties currentIP : imagePropertiesList) {
			if (previousIP != null) {
				previousIP.nextIP = currentIP;
				currentIP.previousIP = previousIP;
			}
			previousIP = currentIP;
		}
	}
	
	private String buildID() {
		String id;
		if (!sigma.containsKey(Template_Gallery.id)) {
			String number = sigma.resolve(Template_Gallery.number);
			id = String.format("gallery%s", number);
			sigma.b(Template_Gallery.id, id);
		} else {
			id = sigma.resolve(Template_Gallery.id);
		}
		return id;
	}
	
}
