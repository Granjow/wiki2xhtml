package src;

import src.utilities.IORead_Stats;


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
 * Resources from the jar file itself
 *
 * @author Simon Eugster
 */
public class Container_Resources {

	private Container_Resources() { }
	private static Container_Resources cr = new Container_Resources();

	/** Singleton */
	public static Container_Resources getInstance() {
		return cr;
	}
	
	/** Location of some fallback resources delivered by wiki2xhtml */
	public static final String resdir = "/resources/";

	// TODO Doc: update used templates
	public static final String sTplImage = "tplImage.txt";
	
	public static final String sTplGallery = "tplGallery.txt";
	public static final String sTplGalleryContainer = "tplGalleryContainer.txt";
	public static final String sTplImagepage = "tplImagepage.txt";
	
	/** Template for the in-text link to the reference note
	 * @see Constants.Template_References */
	public static final String sTplCiteRef = "tplCiteRef.txt";
	/** Template for the reference note at the bottom of the page
	 * @see Constants.Template_References */
	public static final String sTplCiteNote = "tplCiteNote.txt";
	
	/** Template for $$code blocks$$ 
	 * @see Constants.Template_Blocks */ 
	public static final String sTplCode = "tplCode.txt";
	
	/** Template for the whole page
	 * @see Constants.Template_Page */
	public static final String sTplPage = "tplPage.txt";

	/** Template for the Table of Contents */
	public static final String sTplTOC = "tplTOC.txt";
	
	/** Contains a list of files to copy */
	public static final String sStyleResources = "resources.txt";
	
	/** Template for recursion warnings */
	public static final String srecursionTemplateName = "tplRecursion.txt";

	public static StringBuffer readResource(String s) {
		return IORead_Stats.readFromJar(s);
	}

}
