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

import src.Constants;
import src.Container_Resources;
import src.project.WikiProject.FallbackFile.FallbackLocation;
import src.project.WikiProject.FallbackFile.NoFileFoundException;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.project.settings.PageSettings;
import src.project.settings.Settings;
import src.resources.ResProjectSettings.SettingsE;
import src.utilities.IORead_Stats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

/**
 * Bundles multiple WikiFiles and project settings.
 */
public class WikiProject {
	
	private File projectDirectory;
	private File outputDirectory;
	public File styleDirectory;

	public Sitemap sitemap = new Sitemap();
	
	// Settings
	private Settings<SettingsE, String> projectSettings = new PageSettings();
	
	private int id = 0;
	private int nextID() {
		return id++;
	}
	
	/** All files in the project */
	private HashMap<Integer, WikiFile> fileMap = new HashMap<Integer, WikiFile>();
	
	
	public WikiProject(String projectDirectory) {
		this.projectDirectory = new File(projectDirectory);
		this.styleDirectory = this.projectDirectory;
		this.outputDirectory = new File(this.projectDirectory.getAbsolutePath() + File.separator + Constants.Directories.target);
		
		// TODO create directories
		
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
			projectDirectory = f;
			return true;
		}
		return false;
	}
	public File outputDirectory() { return outputDirectory; }
	/** @see #setProjectDirectory(File) */
	public boolean setoutputDirectory(File f) {
		if (fileCount() == 0) {
			try {
				checkOutputDirectoryLocation();
				outputDirectory = f;
				return true;
			} catch (InvalidTargetDirectoryLocationException e) {
				e.printStackTrace();
			}
		}
		return false;
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
	
	public void checkOutputDirectoryLocation() throws InvalidTargetDirectoryLocationException {
		if (projectDirectory.equals(outputDirectory)) {
			throw new InvalidTargetDirectoryLocationException("Source directory must not be equal to the target directory.");
		}
		try {
			if (projectDirectory.getCanonicalPath().startsWith(outputDirectory.getCanonicalPath())) {
				throw new InvalidTargetDirectoryLocationException("Project directory must not be a subdirectory of the output directory.");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidTargetDirectoryLocationException(e.getMessage());
		}
	}
	
	
	////////// PARSING ///////////
	public void make() throws IOException, InvalidTargetDirectoryLocationException {
		checkOutputDirectoryLocation();
		
		// Make the project
		for (WikiFile f : fileMap.values()) {
			f.parse();
			f.write();
		}
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
	
	/**
	 *  Tries to locate a file by searching for it in several locations like:
	 *  <ul><li>.jar</li>
	 *  <li>project directory</li>
	 *  <li>style directory</li></ul>
	 */
	public static class FallbackFile {
		/** The original filename */
		public final String filename;
		private File file;
		private URL url;
		private FallbackLocation location = FallbackLocation.none;
		
		public static final Vector<FallbackLocation> bottomUpLocations;
		
		static {
			bottomUpLocations = new Vector<FallbackLocation>();
			bottomUpLocations.add(FallbackLocation.system);
			bottomUpLocations.add(FallbackLocation.project);
			bottomUpLocations.add(FallbackLocation.style);
			bottomUpLocations.add(FallbackLocation.jar);
		}
		
		public static enum FallbackLocation {
			system, project, style, jar, none;
		}
		
		public StringBuffer getContent() throws NoFileFoundException, IOException {
			switch (location) {
			case none:
				throw new NoFileFoundException(filename);
			case jar:
				return IORead_Stats.readSBuffer(url);
			default:
				return IORead_Stats.readSBuffer(file);
			}
		}
		
		/** @return null if the file is in a .jar or not available */
		public final File file() { return file; }
		/** @return null if the file is not in the .jar */
		public final URL url() { return url; }
		/** @return null, if no path available */
		public final String pathInfo() {
			if (file != null) { return file.getAbsolutePath(); }
			if (url != null) { return url.getFile(); }
			return null;
		}
		
		/** Using default fallback priority. See {@link #bottomUpLocations}. */
		public FallbackFile(String filename, WikiProject project) throws NoFileFoundException {
			this(filename, project, bottomUpLocations);
		}
		public FallbackFile(String filename, WikiProject project, Vector<FallbackLocation> fallback) throws NoFileFoundException {
			this.filename = filename;
			File file;
			URL url;
			forLoop : for (FallbackLocation location : fallback) {
				file = null;
				switch (location) {
				case system:
					if (file == null) file = new File(filename);
					// Fall through
				case project:
					if (file == null) file = new File(project.projectDirectory.getAbsolutePath() + File.separatorChar + filename);
				case style:
					if (file == null) file = new File(project.styleDirectory.getAbsolutePath() + File.separatorChar + filename); 
					if (file.exists()) {
						this.location = location;
						this.url = null;
						this.file = file;
						break forLoop;
					}
					break;
				case jar:
					url = this.getClass().getResource(filename);
					if (url == null) {
						url = this.getClass().getResource(Container_Resources.resdir + filename);
					}
					if (url != null) {
						this.url = url;
						this.file = null;
						this.location = location;
						break forLoop;
					}
					break;
				}
			}
			if (location == FallbackLocation.none) {
				throw new NoFileFoundException(filename);
			}
		}
		
		public static class NoFileFoundException extends FileNotFoundException {
			private static final long serialVersionUID = 1L;
			public NoFileFoundException(String filename) {
				super("No file like " + filename + " found.");
			}
		}
		
	}	
	
	public static class InvalidLocationException extends Exception {
		private static final long serialVersionUID = 1L;
		public InvalidLocationException(String msg) { super(msg); }
	}
	
	public static class InvalidTargetDirectoryLocationException extends Exception {
		private static final long serialVersionUID = 1L;
		public InvalidTargetDirectoryLocationException(String msg) { super(msg); }
	}
	
	public static void main(String[] args) throws InvalidLocationException, IOException, InvalidTargetDirectoryLocationException {
		WikiProject p = new WikiProject(".");
		StringBuffer sb = new StringBuffer();
		sb.append("Hallo. [[link.html]]\n*bla");
		VirtualWikiFile vf = new VirtualWikiFile(p,"myname", false,true,sb);
		p.addFile(vf);
		p.make();
		System.out.println(vf.getContent());
	}

}
