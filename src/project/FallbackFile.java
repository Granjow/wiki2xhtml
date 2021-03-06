package src.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import src.Constants;
import src.utilities.IORead_Stats;


/**
 *  Tries to locate a file by searching for it in several locations like:
 *  <ul><li>.jar</li>
 *  <li>project directory</li>
 *  <li>style directory</li></ul>
 */
public class FallbackFile {
	/** The original filename */
	public final String filename;
	private File file;
	private URL url;
	private FallbackLocation location = FallbackLocation.none;

	public static final Vector<FallbackLocation> bottomUpLocations;
	public static final Vector<FallbackLocation> projectLocationOnly;
	
	static {
		bottomUpLocations = new Vector<FallbackLocation>();
//			bottomUpLocations.add(FallbackLocation.system);
		bottomUpLocations.add(FallbackLocation.project);
		bottomUpLocations.add(FallbackLocation.style);
		bottomUpLocations.add(FallbackLocation.jar);
		projectLocationOnly = new Vector<FallbackFile.FallbackLocation>();
		projectLocationOnly.add(FallbackLocation.project);
	}
	
	public static enum FallbackLocation {
		/** Random: Should not be used. */
		system,
		/** Project directory */
		project, 
		/** Style directory */
		style, 
		/** Jar file */
		jar, 
		/** Nowhere */
		none;
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
				if (file == null) {
					file = new File(filename);
//					System.err.printf("Checking %s ... exists? %s\n", file.getAbsolutePath(), file.exists());
				}
				// Fall through
			case project:
				if (file == null) {
					file = new File(project.projectDirectory().getAbsolutePath() + File.separatorChar + filename);
//					System.err.printf("Checking %s ... exists? %s\n", file.getAbsolutePath(), file.exists());
				}
				
			case style:
				if (file == null) {
					file = new File(project.styleDirectory().getAbsolutePath() + File.separatorChar + filename); 
//					System.err.printf("Checking %s ... exists? %s\n", file.getAbsolutePath(), file.exists());
				}
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
					url = this.getClass().getResource(Constants.Templates.resdir + filename);
				}
				if (url != null) {
					this.url = url;
					this.file = null;
					this.location = location;
//					System.err.printf("Found %s in the .jar file.", filename);
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
