package src.project.file;

import src.project.WikiProject;
import src.project.settings.PageSettings;

public class VirtualWikiFile extends WikiFile {

	protected VirtualWikiFile(WikiProject project, String name, boolean sitemap, boolean parse) {
		super(project, name, sitemap, parse);
		content = new StringBuffer();
	}

	public VirtualWikiFile(WikiProject project, String name, boolean sitemap, boolean parse, StringBuffer content) {
		super(project, name, sitemap, parse);
		this.content = content;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj instanceof WikiFile) {
			WikiFile wf = (WikiFile) obj;
			
			return wf.content.equals(content);
		}
		return false;
	}
	
	public StringBuffer getContent() {
		return content;
	}
	
	public void setPageSettings(PageSettings p) {
		pageSettings = p;
	}
	
	public void write() { }
}
