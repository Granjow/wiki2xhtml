package src.project.file;

import java.io.File;
import java.io.IOException;

import src.project.WikiProject;
import src.utilities.IORead_Stats;
import src.utilities.IOWrite_Stats;

public class LocalWikiFile extends WikiFile {

	protected LocalWikiFile(WikiProject project, String name, boolean sitemap, boolean parse) {
		super(project, name, sitemap, parse);
		File f1 = null, f2 = null;
		try {
			f1 = File.createTempFile("wiki2xhtml", "temp");
			f2 = File.createTempFile("wiki2xhtml", "temp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		fSrc = f1;
		fDest = f2;
		fSrc.deleteOnExit();
		fDest.deleteOnExit();
	}
	
	public LocalWikiFile(WikiProject project, String name, boolean sitemap, boolean parse, File in, File out) {
		super(project, name, sitemap, parse);
		fSrc = in;
		fDest = out;
	}

	private final File fSrc;
	private final File fDest;

	/** Needs to be overridden for comparing. */
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj instanceof LocalWikiFile) {
			LocalWikiFile wf = (LocalWikiFile) obj;
			return fSrc.equals(wf.fSrc) && fDest.equals(wf.fDest);
		}
		return false;
	}
	public StringBuffer getContent() {
		if (!alreadyRead) {
			try {
				content = IORead_Stats.readSBuffer(fSrc);
				alreadyRead = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content; 
	}
	
	public void write() {
		try {
			IOWrite_Stats.writeString(fDest, getContent().toString(), false);
		} catch (IOException e) {}
	}
}
