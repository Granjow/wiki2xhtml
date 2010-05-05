package src.project.file;

import java.io.File;
import java.io.IOException;

import src.utilities.IORead_Stats;

public class WikiFile {
	
	private StringBuffer content = null;
	
	public final int ID;
	public final boolean sitemap;
	public final boolean parse;
	
	private final File fSrc;
	private final File fDest;
	
	private boolean alreadyRead = false;
	
	
	/** Needs to be overridden for comparing. */
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj instanceof WikiFile) {
			WikiFile wf = (WikiFile) obj;
			return fSrc.equals(wf.fSrc) && fDest.equals(wf.fDest);
		}
		return false;
	}
	
	public WikiFile(File fSrc, File fDest, int id, boolean sitemap, boolean parse) {
		this.ID = id;
		this.fSrc = fSrc;
		this.fDest = fDest;
		this.sitemap = sitemap;
		this.parse = parse;
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
	
	public void setContent(StringBuffer newContent) {
		content = newContent;
	}
	
	public void write() {
		
	}
	

}
