package src.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import src.utilities.IOWrite_Stats;

/**
 * Sitemap builder. Creates text files (not XML). 
 * See <a href="http://sitemaps.org/">sitemaps.org</a> for details.
 * @since wiki2xhtml 3.4
 * TODO 0 doc Sitemaps
 */
public class Sitemap {

	/** List of all files to include in the sitemap */
	private ArrayList<String> sitemapList = new ArrayList<String>();		
	private final File relativeTo;
	private final File mapFile;
	private final String baseURI;
	
	Sitemap() {
		this(new File("."), new File("wx-sitemap.txt"), "");
	}
	Sitemap(File relativeTo, File mapFile, String baseURI) {
		this.relativeTo = relativeTo;
		this.mapFile = mapFile;
		this.baseURI = baseURI.replaceFirst("/+$", ""); // Remove trailing slashes
	}
	
	/** Add a file to the sitemap. */
	public void add(File f) {
		String entry = f.getAbsolutePath();
		String rel = relativeTo.getAbsolutePath();
		
		if (entry.startsWith(rel)) {
			entry = entry.substring(rel.length());
			entry = entry.replace(File.separatorChar, '/');
			if (entry.startsWith("/")) {
				entry = entry.substring(1);
			}
			sitemapList.add(baseURI + "/" + entry);
		} else {
//			ca.ol("Don't know what to do with %s in the sitemap (not in %s). Not adding.", CALevel.MSG, entry, rel);
		}
	}
	
	/**
	 * Writes the sitemap.
	 * @return true, if writing was successful 
	 */
	public boolean write() {
		StringBuffer sb = new StringBuffer();
		for (String s : sitemapList) {
			sb.append(s + "\n");
		}
		try {
			IOWrite_Stats.writeString(mapFile, sb.toString(), false);
			return true;
			
		} catch (IOException e) {
//			ca.ol("Could not write sitemap to file %s.", CALevel.ERRORS, mapFile.getAbsolutePath());
			e.printStackTrace();
			return false;
		}
	}
}
