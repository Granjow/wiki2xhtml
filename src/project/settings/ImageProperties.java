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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Constants;
import src.Container_Resources;
import src.Resources;
import src.argumentHandler.ArgumentItem;
import src.argumentHandler.ArgumentReader;
import src.project.WikiProject.FallbackFile;
import src.project.WikiProject.FallbackFile.NoFileFoundException;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.ptm.PTM.PTMObjects;
import src.ptm.PTMArgumentNameNode;
import src.ptm.PTMArgumentNode;
import src.ptm.PTMRootNode;
import src.ptm.PTMState;
import src.resources.ResProjectSettings.EImageContext;
import src.resources.ResProjectSettings.EImageProperties;
import src.resources.ResProjectSettings.SettingsE;
import src.utilities.XMLTools;


/**
 * Contains properties about a single image
 */
public class ImageProperties extends StringSettings<EImageProperties> {
	
	private static final char SEP = '=';
	
	public boolean imagepageCreated;
	
	/** The previous image on the page */
	public ImageProperties previousIP = null;
	/** The next image on the page */
	public ImageProperties nextIP = null;
	
	/** All name/value bindings (properties) for this image */
	public PTMState argumentBindings = null;
	
	private EImageContext context = EImageContext.thumb;
	public void setContext(EImageContext context) { this.context = context; }
	
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
	
	public String getValue(String name) {
		return argumentBindings.resolve(name);
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
		for (String[] es : Resources.entities) {
			s = s.replaceAll(es[1], es[0]);
		}
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
	
	public FallbackFile getTemplate() throws NoFileFoundException {
		switch (context) {
		case gallery:
			return parentFile.project.locate(Container_Resources.sTplGallery);
		case thumb:
		default:
			return parentFile.project.locate(Container_Resources.sTplImage);
		}
	}
	






	/**
	 * Reads arguments from an image tag ([[image:args]]).
	 * @param args All image arguments, like &ldquo;<code>Image:venus.jpg|244px|The Venus</code>&rdquo;
	 */
	public void readArguments(final String args) {
		
		ArrayList<PTMObjects> allowedChildNodes = new ArrayList<PTMObjects>();
		allowedChildNodes.add(PTMObjects.Argument);
//		PTMRootNode root = new PTMRootNode(new StringBuffer(args + '|'), null, allowedChildNodes);
//		root.printTree(System.out, " ");
//		PTMState sigma = new PTMState(root);
//		sigma.printValues();
//		System.out.println("End of properties tree. Was: " + root.getRawContent());
//		System.out.println("End of state.");
		
//		StringBuffer props = new StringBuffer();
		HashMap<String, String> props = new HashMap<String, String>();

		ArrayList<ArgumentItem> a = ArgumentReader.getArguments(args);

		Pattern pSize = Pattern.compile("^(\\d+)?(?:x(\\d+))?(?<=\\d)(px|%)$");
		Matcher mSize;
		Pattern pImage = Pattern.compile("(?i)(?:image|bild|file):(.+)");
		Matcher mPImage;
		Pattern pUserVar = Pattern.compile("(?i)(var[a-z0-9_-]+)=(.+)");
		Matcher mUserVar;
		Pattern pCaption = Pattern.compile("(?i)(?:c(?:aption)?=)(.+)");
		Matcher mCaption;

		for (ArgumentItem ai : a) {
			
			mPImage = pImage.matcher(ai.fullArg);
			if (mPImage.matches() && props.get(Constants.Images.path) == null) {
				// Image:<path>
				props.put(Constants.Images.path, mPImage.group(1));
				continue;
			}

			mSize = pSize.matcher(ai.fullArg);
			if (mSize.find()) {
				// 200x300px
				if (mSize.group(1) != null) {
					props.put(Constants.Images.width, mSize.group(1) + mSize.group(3));
				}
				if (mSize.group(2) != null) {
					props.put(Constants.Images.height, mSize.group(2) + mSize.group(3));
				}
				continue;
			}

			if (Constants.Images.thumb.equals(ai.fullArg)) {
				// thumb
				props.put(Constants.Images.type, "thumb");
				continue;
			}
			
			if (Constants.Images.direct.equals(ai.fullArg)) {
				// direct
				props.put(Constants.Images.type, "direct");
				continue;
			}

			if (ai.fullArg.startsWith(Constants.Images.thumb + SEP)) {
				// thumb=<path>
				props.put(Constants.Images.thumb, ai.fullArg.substring(Constants.Images.thumb.length()+1));
				props.put(Constants.Images.type, "thumb");
				continue;
			}

			if (Constants.Position.left.equals(ai.fullArg)
					|| Constants.Position.right.equals(ai.fullArg)
					|| Constants.Position.center.equals(ai.fullArg)) {
				// left/center/right
				props.put(Constants.Images.location, ai.fullArg);
				continue;
			}
			
			mUserVar = pUserVar.matcher(ai.fullArg);
			if (mUserVar.find()) {
				// varSomething=myVar
				// User defined parameter. The parameter name has to start with «var» and may only contain
				// valid characters; See pUserVar.
				props.put(mUserVar.group(1), mUserVar.group(2));
				continue;
			}
			
			if (ai.fullArg.startsWith(Constants.Images.link + SEP)) {
				// link=<custom link>
				props.put(Constants.Images.link, ai.fullArg.substring(Constants.Images.link.length()+1));
				continue;
			}
			
			if (ai.fullArg.startsWith(Constants.Images.alt + SEP)) {
				// alt=<alternative text>
				props.put(Constants.Images.alt, ai.fullArg.substring(Constants.Images.alt.length()+1));
				continue;
			}
			
			mCaption = pCaption.matcher(ai.fullArg);
			if (mCaption.matches()) {
				// caption=<image caption>
				props.put(Constants.Images.caption, mCaption.group(1));
				continue;
			}


			if (Constants.Images.noscale.equals(ai.fullArg)) {
				// noscale
				// Indicates that the image should not be scaled on the image page
				props.put(Constants.Images.noscale, "true");
				continue;
			}
			
			if (Constants.Images.clear.equals(ai.fullArg)) {
				// clear
				props.put(Constants.Images.clear, "both");
				continue;
			}
			if (ai.fullArg.startsWith(Constants.Images.clear + SEP)) {
				// clear=<left/right>
				props.put(Constants.Images.clear, ai.fullArg.substring(Constants.Images.clear.length() + 1));
				continue;
			}
			

			if (ai.fullArg.startsWith("ld" + SEP)) {
				// ld=<long description>
				props.put(Constants.Images.longDesc, ai.fullArg.substring(3));
				continue;
			}

			/* Description */
			if (ai.fullArg.length() > 0) {
				if (isSet(EImageProperties.text)) {
					// &#124; is the vertical bar |
					append_(EImageProperties.text, "&#124;");
				}
				append_(EImageProperties.text, ai.fullArg);
				
				String s = props.get(Constants.Images.text);
				if (s == null) { s = ""; }
				else { s += "&#124;"; }
				props.put(Constants.Images.text, s + ai.fullArg);
			}
		}
		
		StringBuffer arguments = new StringBuffer();
		for (Entry<String, String> e : props.entrySet()) {
			arguments.append(e.getKey() + PTMArgumentNameNode.separator + e.getValue() + PTMArgumentNode.identifier);
		}
		
		PTMRootNode root = new PTMRootNode(arguments, null, allowedChildNodes);
		argumentBindings = new PTMState(root);
		
//		System.out.println("Arguments, reconstructed: \n");
//		root.printTree(System.out, " ");
		

		// Check for non-ASCII characters
//		Matcher m = Pattern.compile(Resources.pNotAscii).matcher(get_(EImageProperties.path));
//		if (m.find()) {
////			ca.o("Image contains non-ASCII characters! They should be removed.", CALevel.ERRORS);
//		}
	}
	
	
	public static void main(String[] args) {
		WikiFile wf = new VirtualWikiFile(null, "name", false, false, new StringBuffer());
		ImageProperties prop = new ImageProperties(wf);
		prop.readArguments("Image:nowhere.jpg|thumb|right|this is a description");
		prop.print(": ");
	}
	
}
