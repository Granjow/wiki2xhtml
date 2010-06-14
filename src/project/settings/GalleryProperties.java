package src.project.settings;

import java.util.ArrayList;

import src.project.file.WikiFile;
import src.resources.ResProjectSettings.EGalleryProperties;
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
 * Contains properties about a single gallery
 */
public class GalleryProperties extends Settings<EGalleryProperties, String> {

	public final WikiFile parentFile;
	/** An ordered list of all items in this Gallery container. */
	public final ArrayList<ImageProperties> imagePropertiesList;
	
	protected String concatenate(String left, String right) { return left + right; }
	public String nullValue() { return null; }

	public GalleryProperties(WikiFile parentFile) {
		this.parentFile = parentFile;
		imagePropertiesList = new ArrayList<ImageProperties>();
		set_(EGalleryProperties.container, "true");
		
		// Add the ID generator
		addPreparser(new ValuePreparser<String>() {
			public String adjust(String value) {
				String number = "unknown";
				if (isSet(EGalleryProperties.number)) { number = get_(EGalleryProperties.number); }
				return XMLTools.getXmlNameChar(String.format("gallery_%s", number));
			};
		}, EGalleryProperties.id);
		// Set a fake ID so that the property is not null
		set_(EGalleryProperties.id, "fakeID");
	}
	
	/**
	 * @return A placeholder for an image. Used for replacing image tags and insert the code
	 * afterwards after the images have been linked.
	 */
	public String getPlaceholder() {
		return String.format(">>>gallery-%s-placeholder<<<", get_(EGalleryProperties.number));
	}
	
	
	public void readArguments(String s) {
		//TODO read caption and other arguments
	}
	
}
