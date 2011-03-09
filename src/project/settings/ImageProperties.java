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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Constants;
import src.Resources;
import src.Constants.Template_Images;
import src.argumentHandler.ArgumentItem;
import src.argumentHandler.ArgumentReader;
import src.project.FallbackFile;
import src.project.FallbackFile.NoFileFoundException;
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
	public void setContext(EImageContext context) {
		this.context = context;
		set_(EImageProperties.context, context.property);
		if (context == EImageContext.gallery) {
			argumentBindings.b(Template_Images.type, "thumb");
		}
	}
	
	public final WikiFile parentFile;
	
	private GalleryProperties galleryProperties = null;

	public ImageProperties(WikiFile parent) {
		parentFile = parent;
		imagepageCreated = false;
		argumentBindings = new PTMState();
	}
	public ImageProperties(WikiFile parent, GalleryProperties galleryProperties) {
		this(parent);
		this.galleryProperties = galleryProperties;
	}
	
	/**
	 * @return A placeholder for an image. Used for replacing image tags and insert the code
	 * afterwards after the images have been linked.
	 */
	public String getPlaceholder() {
		return String.format(">>>image-%s-placeholder<<<", argumentBindings.resolve(Template_Images.number));
	}
	
	/**
	 * Builds the filename and path for the image page.
	 * @return Image page path, in simplified form (no ../, ./, //, etc.)
	 */
	public String getImagepagePath() {
		String path = argumentBindings.resolve(Template_Images.path);
		String dirImagepages = parentFile.getProperty(SettingsE.imagepagesDir, true);
		assert dirImagepages != null; // Default setting
		assert path.length() > 0;
		
		
		StringBuffer name = new StringBuffer();
		name.append(dirImagepages);
		name.append(parentFile.internalName().replace("/", "+"));
		name.append("_to_");
		name.append(path.replace("/", "+").replace(" ", "_"));
		name.append(".html");
		
		String finalName;
		try {
			finalName = new File(name.toString()).getCanonicalPath().substring(new File(".").getCanonicalPath().length());
			assert finalName.length() > 0;
			// Remove leading slash
			finalName = finalName.substring(1);
		} catch (IOException e) {
			finalName = name.toString().replaceAll("(?<=/|^)\\./", "").replaceAll("//+", "/");
		}
		
		return finalName;
	}
	
	/**
	 * Replaces characters like "&amp;" and " " by "&amp;amp;" and "%20"
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
			return parentFile.project.locate(Constants.Templates.sTplGallery);
		case thumb:
		default:
			return parentFile.project.locate(Constants.Templates.sTplImage);
		}
	}
	




	public void resolvePaths() {

		if (!argumentBindings.containsKey(Template_Images.link)) {
			if (imagepageCreated) {
				argumentBindings.b(Template_Images.link, getImagepagePath());
			} else {
				argumentBindings.b(Template_Images.link, argumentBindings.resolve(Template_Images.path));
			}
		}
		if (!argumentBindings.containsKey(Template_Images.thumb)) {
			if ("thumb".equals(argumentBindings.resolve(Template_Images.type))) {
				String dirPattern = parentFile.getProperty(SettingsE.thumbsDir, true);
				if (dirPattern == null) {
					dirPattern = "%n";
				}
				String thumb = getThumbnailSource(argumentBindings.resolve(Template_Images.path), dirPattern);
				argumentBindings.b(Template_Images.thumb, thumb);
			}
		}
	}
	

	/**
	 * Replaces: (example: <code>dir/image.jpg</code>)
	 * <ul>
	 * <li>%n &#x2013; full name: <code>dir/image.jpg</code></li>
	 * <li>%f &#x2013; file name: <code>image.jpg</code><li>
	 * <li>%d &#x2013; directory: <code>dir</code></li>
	 * <li>%b &#x2013; without extension: <code>image</code></li>
	 * <li>%e &#x2013; file extension: <code>.jpg</code></li>
	 * </ul>
	 * in the given {@code dirPattern}.
	 * @param imgPath The path of the thumbnail
	 */
	public static final String getThumbnailSource(String imgPath, String dirPattern) {
		assert dirPattern != null;
		
		int index;
		String n, f, d, b, e;
		
		n = imgPath;
		
		index = imgPath.lastIndexOf('/');
		if (index < 0) {
			f = imgPath;
			d = "";
		} else {
			f = imgPath.substring(index+1);
			d = imgPath.substring(0, index);
		}
		
		index = f.lastIndexOf('.');
		if (index < 0) {
			b = f;
			e = "";
		} else {
			b = f.substring(0, index);
			e = f.substring(index);
		}
		
		String path = dirPattern
			.replace("%n", n)
			.replace("%f", f)
			.replace("%d", d)
			.replace("%b", b)
			.replace("%e", e);

		return path;
	}
	


	/**
	 * Reads arguments from an image tag ([[image:args]]). Updates {@link #argumentBindings}.
	 * @param args All image arguments, like &ldquo;<code>Image:venus.jpg|244px|The Venus</code>&rdquo;
	 */
	public void readArguments(final String args) {
		
		ArrayList<PTMObjects> allowedChildNodes = new ArrayList<PTMObjects>();
		allowedChildNodes.add(PTMObjects.Argument);
		
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
		
		String pathPrefix = parentFile.getProperty(SettingsE.imagesDir, true);
		if (pathPrefix == null) {
			pathPrefix = "";
		} else if (pathPrefix.length() > 0 && !pathPrefix.endsWith("/")) {
			pathPrefix += "/";
		}

		for (ArgumentItem ai : a) {
			
			mPImage = pImage.matcher(ai.fullArg);
			if (mPImage.matches() && props.get(Constants.Template_Images.path) == null) {
				// Image:<path>
				String path = mPImage.group(1);
				if (!path.startsWith("./")) {
					path = pathPrefix + path;
				}
				props.put(Constants.Template_Images.path, path);
				continue;
			}

			mSize = pSize.matcher(ai.fullArg);
			if (mSize.find()) {
				// 200x300px
				if (mSize.group(1) != null) {
					props.put(Constants.Template_Images.width, mSize.group(1) + mSize.group(3));
				}
				if (mSize.group(2) != null) {
					props.put(Constants.Template_Images.height, mSize.group(2) + mSize.group(3));
				}
				continue;
			}

			if (Constants.Template_Images.thumb.equals(ai.fullArg)) {
				// thumb
				props.put(Constants.Template_Images.type, "thumb");
				continue;
			}

			if (ai.fullArg.startsWith(Constants.Template_Images.thumb + SEP)) {
				// thumb=<path>
				props.put(Constants.Template_Images.thumb, ai.fullArg.substring(Constants.Template_Images.thumb.length()+1));
				continue;
			}
			
			if (Constants.Template_Images.direct.equals(ai.fullArg)) {
				// direct
				props.put(Constants.Template_Images.direct, "true");
				continue;
			}

			if (Constants.Position.left.equals(ai.fullArg)
					|| Constants.Position.right.equals(ai.fullArg)
					|| Constants.Position.center.equals(ai.fullArg)) {
				// left/center/right
				props.put(Constants.Template_Images.location, ai.fullArg);
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
			
			if (ai.fullArg.startsWith(Constants.Template_Images.link + SEP)) {
				// link=<custom link>
				props.put(Constants.Template_Images.link, ai.fullArg.substring(Constants.Template_Images.link.length()+1));
				continue;
			}
			
			if (ai.fullArg.startsWith(Constants.Template_Images.alt + SEP)) {
				// alt=<alternative text>
				props.put(Constants.Template_Images.alt, ai.fullArg.substring(Constants.Template_Images.alt.length()+1));
				continue;
			}
			
			mCaption = pCaption.matcher(ai.fullArg);
			if (mCaption.matches()) {
				// caption=<image caption>
				props.put(Constants.Template_Images.caption, mCaption.group(1));
				continue;
			}


			if (Constants.Template_Images.noscale.equals(ai.fullArg)) {
				// noscale
				// Indicates that the image should not be scaled on the image page
				props.put(Constants.Template_Images.noscale, "true");
				continue;
			}
			
			if (Constants.Template_Images.clear.equals(ai.fullArg)) {
				// clear
				props.put(Constants.Template_Images.clear, "both");
				continue;
			}
			if (ai.fullArg.startsWith(Constants.Template_Images.clear + SEP)) {
				// clear=<left/right>
				props.put(Constants.Template_Images.clear, ai.fullArg.substring(Constants.Template_Images.clear.length() + 1));
				continue;
			}
			

			if (ai.fullArg.startsWith("ld" + SEP)) {
				// ld=<long description>
				props.put(Constants.Template_Images.longDesc, ai.fullArg.substring(3));
				continue;
			}

			/* Description */
			if (ai.fullArg.length() > 0) {
				if (isSet(EImageProperties.text)) {
					// &#124; is the vertical bar |
					append_(EImageProperties.text, "&#124;");
				}
				append_(EImageProperties.text, ai.fullArg);
				
				String s = props.get(Constants.Template_Images.text);
				if (s == null) { s = ""; }
				else { s += "&#124;"; }
				props.put(Constants.Template_Images.text, s + ai.fullArg);
			}
		}
		
		StringBuffer arguments = new StringBuffer();
		for (Entry<String, String> e : props.entrySet()) {
			arguments.append(e.getKey() + PTMArgumentNameNode.separator + e.getValue() + PTMArgumentNode.identifier);
		}
		
		PTMRootNode root = new PTMRootNode(arguments, null, allowedChildNodes);
		argumentBindings.readState(root);
		
		buildID();
		
//		System.out.println("Arguments, reconstructed: \n");
//		root.printTree(System.out, " ");
	}

	/**
	 * Ensures that the image ID is bound to {@link Template_Images#id}.
	 * @return The image ID.
	 */
	private String buildID() {
		String id;
		if (!argumentBindings.containsKey(Template_Images.id)) {
			String number = argumentBindings.resolve(Template_Images.number);
			String path = argumentBindings.resolve(Template_Images.path);
			String prefix = "";
			if (galleryProperties != null) {
				prefix = "g" + galleryProperties.sigma.resolve(Template_Images.number) + "_";
			}
			id = String.format("%sim%s_%s", prefix, number, path); 
			argumentBindings.b(Template_Images.id, id);
			
			assert number.length() > 0;
			
		} else {
			id = argumentBindings.resolve(Template_Images.id);
		}
		return id;
	}
	
	
	public static void main(String[] args) {
		WikiFile wf = new VirtualWikiFile(null, "name", false, new StringBuffer());
		ImageProperties prop = new ImageProperties(wf);
		prop.readArguments("Image:nowhere.jpg|thumb|right|this is a description");
		prop.print(": ");
	}
	
}
