package src.project.file;

import java.io.File;
import java.io.IOException;

import src.utilities.IORead_Stats;
import src.utilities.IOWrite_Stats;

public class WikiFile {
	
	private StringBuffer content = null;
	
	public final boolean sitemap;
	public final boolean parse;
	public final String name;
	
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
	
	public WikiFile(File fSrc, File fDest, String name, boolean sitemap, boolean parse) {
		this.fSrc = fSrc;
		this.name = name;
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
		try {
			IOWrite_Stats.writeString(fDest, content.toString(), false);
		} catch (IOException e) {}
	}
	

}
