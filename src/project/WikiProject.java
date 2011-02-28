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

package src.project;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import src.Constants;
import src.Statistics;
import src.Wiki2xhtmlArgsParser;
import src.project.FallbackFile.FallbackLocation;
import src.project.FallbackFile.NoFileFoundException;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.project.settings.PageSettings;
import src.project.settings.PageSettingsReader;
import src.project.settings.Settings;
import src.resources.ResProjectSettings.SettingsE;
import src.utilities.IOUtils;

/**
 * Bundles multiple WikiFiles and project settings.
 */
public class WikiProject {
	
	private File projectDirectory;
	private File outputDirectory;
	private File styleDirectory;
	private String styleOutputDirectory;

	public Sitemap sitemap = new Sitemap();
	public WikiStyle wikiStyle = new WikiStyle(this);
	public Wiki2xhtmlArgsParser argsParser = null;
	
	// Settings
	private Settings<SettingsE, String> projectSettings = new PageSettings();
	private WikiMenu wikiMenu = null;
	
	private int id = 0;
	private int nextID() {
		return id++;
	}
	
	/** All files in the project */
	private HashMap<Integer, WikiFile> fileMap = new HashMap<Integer, WikiFile>();
	
	
	public WikiProject(String projectDirectory) {
		setProjectDirectory(new File(projectDirectory));
		this.styleDirectory = new File(this.projectDirectory.getAbsolutePath() + File.separator + "style");
		this.outputDirectory = new File(this.projectDirectory.getAbsolutePath() + File.separator + Constants.Directories.target);
		styleOutputDirectory = "style";
		
		/** Initialize global settings with default values */
		projectSettings.set_(SettingsE.imagepagesDir, Constants.Directories.imagePages);
		projectSettings.set_(SettingsE.imagepageImgWidth, Constants.Standards.widthImgImagepages);
		projectSettings.set_(SettingsE.thumbWidth, Constants.Standards.widthThumbs);
		projectSettings.set_(SettingsE.imagepageTitle, Constants.Standards.imagepageCaption);
		projectSettings.set_(SettingsE.galleryImagesPerLine, Constants.Standards.galleryImagesPerLine);
	}
	
	public File projectDirectory() { return projectDirectory; }
	/** Setting the project directory is only allowed if no files have been added yet. */
	public boolean setProjectDirectory(File f) {
		if (fileCount() == 0) {
			try {
				projectDirectory = f.getCanonicalFile();
			} catch (IOException e) {
				projectDirectory = f;
			}
			return true;
		}
		return false;
	}
	public File outputDirectory() { return outputDirectory; }
	/** @see #setProjectDirectory(File) */
	public boolean setOutputDirectory(File f) {
		if (fileCount() == 0) {
			try {
				checkOutputDirectoryLocation();
				try {
					outputDirectory = f.getCanonicalFile();
				} catch (IOException e) {
					outputDirectory = f;
				}
				return true;
			} catch (InvalidOutputDirectoryLocationException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	public File styleDirectory() { return styleDirectory; }
	public boolean setStyleDirectory(File f) {
		if (fileCount() == 0) {
			try {
				styleDirectory = f.getCanonicalFile();
			} catch (IOException e) {
				styleDirectory = f;
			}
			return true;
		}
		return false;
	}
	public String styleOutputDirectoryName() { return styleOutputDirectory; }
	public File styleOutputDirectory() { return new File(outputDirectory.getAbsolutePath() + File.separator + styleOutputDirectory); }
	public void setStyleOutputDirectory(String dirName) {
		styleOutputDirectory = dirName;
	}
	
	/** Returns the WikiMenu object or <strong><code>null</code></strong>, if no menu is set. */
	public WikiMenu wikiMenu() {
		if (wikiMenu == null && argsParser != null) {
			String menuLocation = (String)argsParser.getOptionValue(argsParser.menuFile, false);
			if (menuLocation != null) {
				try {
					FallbackFile fMenu = locate(menuLocation);
					String content = fMenu.getContent().toString();
					wikiMenu = new WikiMenu();
					wikiMenu.readNewMenu(content);
					
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
		return wikiMenu;
	}
	
	
	public WikiFile getFile(int id) {
		return fileMap.get(id);
	}
	
	public void addFile(WikiFile f) throws InvalidLocationException {
		if (f.validLocation()) {
			fileMap.put(nextID(), f);
		} else {
			throw new InvalidLocationException(String.format("File %s is not at a valid location.", f.name));
		}
	}
	
	public int fileCount() { return fileMap.size(); }
	
	public String getProperty(SettingsE property) {
		return projectSettings.get_(property);
	}
	public boolean isPropertySet(SettingsE property) {
		return projectSettings.isSet(property);
	}
	
	public void checkOutputDirectoryLocation() throws InvalidOutputDirectoryLocationException {
		if (projectDirectory.equals(outputDirectory)) {
			throw new InvalidOutputDirectoryLocationException("Source directory must not be equal to the output directory.");
		}
		try {
			if (projectDirectory.getCanonicalPath().startsWith(outputDirectory.getCanonicalPath())) {
				throw new InvalidOutputDirectoryLocationException("Project directory must not be a subdirectory of the output directory.");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidOutputDirectoryLocationException(e.getMessage());
		}
	}
	
	
	////////// PARSING ///////////
	public void make() throws IOException, InvalidOutputDirectoryLocationException {
		Statistics.getInstance().sw.timeOverall.start();
		
		checkOutputDirectoryLocation();
		
		// Read the common file
		String commonFile = (String)argsParser.getOptionValue(argsParser.commonFile, false);
		if (commonFile != null) {
			FallbackFile ff = locate(commonFile);
			PageSettingsReader psr = new PageSettingsReader(ff.getContent(), (PageSettings) projectSettings);
			psr.readSettings(false);
		}
		
		wikiStyle.copyFiles();
		copyFiles();
		
		// Make the project
		for (WikiFile f : fileMap.values()) {
			f.parse();
			f.write();
		}
		
		Statistics.getInstance().sw.timeOverall.stop();
		System.out.printf("Total time taken: %s\n", Statistics.getInstance().sw.timeOverall.getStoppedTimeString());
	}
	
	
	
	/** See {@link FallbackFile} */
	public final FallbackFile locate(String filename) throws NoFileFoundException {
		return new FallbackFile(filename, this);
	}
	/** See {@link FallbackFile} */
	public final FallbackFile locate(String filename, Vector<FallbackLocation> fallback) throws NoFileFoundException {
		return new FallbackFile(filename, this, fallback);
	}
	
	
	
	
	///////////// VARIOUS FUNCTIONS /////////////
	
	
	/**
	 * @param map file;baseUri. Example: sitemap.txt;http://wiki2xhtml.sf.net
	 */
	public boolean setSitemap(String map) {
		String[] parts = map.split(";", 2);
		boolean worked = false;
		
		if (parts.length < 2) {
//			ca.ol("Wrong sitemap definition! Use --sitemap=filename;base URI.", CALevel.ERRORS);
		} else {
			sitemap = new Sitemap(outputDirectory, new File(outputDirectory.getAbsolutePath() + File.separatorChar + parts[0]), parts[1]);
			worked = true;
		}
		return worked;
	}
	
	public void copyFiles() {
		IOUtils.copyWithRsync(projectDirectory, outputDirectory);
	}
	
	
	
	
	public static class InvalidLocationException extends Exception {
		private static final long serialVersionUID = 1L;
		public InvalidLocationException(String msg) { super(msg); }
	}
	
	public static class InvalidOutputDirectoryLocationException extends Exception {
		private static final long serialVersionUID = 1L;
		public InvalidOutputDirectoryLocationException(String msg) { super(msg); }
	}
	
	public static void main(String[] args) throws InvalidLocationException, IOException, InvalidOutputDirectoryLocationException {
		WikiProject p = new WikiProject(".");
		StringBuffer sb = new StringBuffer();
		sb.append("Hallo. [[link.html]]\n*bla");
		VirtualWikiFile vf = new VirtualWikiFile(p,"myname", false,true,sb);
		p.addFile(vf);
		p.make();
		System.out.println(vf.getContent());
	}

}
