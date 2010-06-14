package src;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

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
	
	public static final String resdir = "/resources/";

	public final Icon rAdd = new ImageIcon(getClass().getResource(resdir + "add.png"));
	public final Icon rOpen = new ImageIcon(getClass().getResource(resdir + "open.png"));
	public final Icon rMOpen = new ImageIcon(getClass().getResource(resdir + "mopen.png"));
	public final Icon rClose = new ImageIcon(getClass().getResource(resdir + "close.png"));
	public final Icon rAbout = new ImageIcon(getClass().getResource(resdir + "about.png"));
	public final Icon rHelp = new ImageIcon(getClass().getResource(resdir + "help.png"));
	public final Icon rNext = new ImageIcon(getClass().getResource(resdir + "next.png"));
	public final Icon rSwiss = new ImageIcon(getClass().getResource(resdir + "swiss.png"));
	public final Icon rGermany = new ImageIcon(getClass().getResource(resdir + "germany.png"));
	public final Icon rItaly = new ImageIcon(getClass().getResource(resdir + "italy.png"));
	public final Icon rFrance = new ImageIcon(getClass().getResource(resdir + "france.png"));
	public final Icon rDir = new ImageIcon(getClass().getResource(resdir + "dir.png"));
	public final Icon rDirOpen = new ImageIcon(getClass().getResource(resdir + "dirOpen.png"));
	public final Icon rSave = new ImageIcon(getClass().getResource(resdir + "save.png"));
	public final Icon rSaveDisk = new ImageIcon(getClass().getResource(resdir + "saveDisk.png"));
	public final Icon rExportSh = new ImageIcon(getClass().getResource(resdir + "export-sh.png"));
	public final Icon rExportBat = new ImageIcon(getClass().getResource(resdir + "export-bat.png"));

	public final File rPDFCrystal = new File(getClass().getResource(sPDF).getFile());
	public final File rOOo = new File(getClass().getResource(sOOo).getFile());
	public final File rXCF = new File(getClass().getResource(sXCF).getFile());
	public final File rTEX = new File(getClass().getResource(sTEX).getFile());
	public final File rTT = new File(getClass().getResource(sTT).getFile());
	public final File rZIP = new File(getClass().getResource(sZIP).getFile());
	public final File rRPM = new File(getClass().getResource(sRPM).getFile());
	public final File rDEB = new File(getClass().getResource(sDEB).getFile());

	public final InputStream iPDFCrystal = getClass().getResourceAsStream(sPDF);
	public final InputStream iOOo = getClass().getResourceAsStream(sOOo);
	public final InputStream iXCF = getClass().getResourceAsStream(sXCF);
	public final InputStream iTEX = getClass().getResourceAsStream(sTEX);
	public final InputStream iTT = getClass().getResourceAsStream(sTT);
	public final InputStream iZIP = getClass().getResourceAsStream(sZIP);
	public final InputStream iRPM = getClass().getResourceAsStream(sRPM);
	public final InputStream iDEB = getClass().getResourceAsStream(sDEB);

	// TODO 0 Removed, Doc!
//	public static final String spicture = "/resources/picture.html";
//	public static final String spictureT = "/resources/pictureT.html";
//	public static final String sthumbPicture = "/resources/thumbPicture.html";
//	public static final String sthumbPictureT = "/resources/thumbPictureT.html";
//	public static final String sgalleryText = "/resources/galleryText.html";
//	public static final String sgalleryImageNodesc = "/resources/galleryImageNodesc.html";
//	public static final String sgalleryImage = "/resources/galleryImage.html";
//	public static final String sgalleryContainer = "/resources/galleryContainer.html";
	
	public static final String sTplImage = "tplImage.txt";
	public static final String sTplGallery = "tplGallery.txt";
	public static final String sTplGalleryContainer = "tplGalleryContainer.txt";
	public static final String sTplImagepage = "tplImagepage.txt";
	
	public static final String sTOC = resdir + "tplTOC.txt";
	public static final String sabout = resdir + "about.html";
	public static final String smissingTemplate = resdir + "tplMissingTemplate.txt";
	public static final String srecursionTemplateName = "tplRecursion.txt";
	public static final String srecursionTemplate = resdir + "" + srecursionTemplateName;
	public static final String sreck = resdir + "reck.html";
	public static final String sPDF = resdir + "Crystal_Clear_mimetype_pdf.png";
	public static final String sOOo = resdir + "Crystal_Clear_app_openoffice.png";
	public static final String sXCF = resdir + "xcf.png";
	public static final String sTEX = resdir + "tex.png";
	public static final String sTT = resdir + "font_truetype.png";
	public static final String sZIP = resdir + "tgz.png";
	public static final String sRPM = resdir + "rpm.png";
	public static final String sDEB = resdir + "deb.png";

	public static final java.net.URL uabout = Container_Resources.class.getResource(resdir + "gui-about.html");
	public static final java.net.URL uhelp = Container_Resources.class.getResource(resdir + "gui-help.html");

	public final Image getIcon() {
		try {
			return ImageIO.read(getClass().getResource(resdir + "wx-icon.png"));
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	public static StringBuffer readResource(String s) {
		return IORead_Stats.readFromJar(s);
	}

}
