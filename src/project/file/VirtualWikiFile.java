package src.project.file;

import java.io.File;
import java.io.IOException;

import src.project.WikiProject;

public class VirtualWikiFile extends WikiFile {

	protected VirtualWikiFile(WikiProject project, String name, boolean sitemap, boolean parse) {
		super(project, name, sitemap, parse);
		content = new StringBuffer();
		assert project != null;
	}

	/** Creates a virtual wiki file.
	 * @param project Must not be <code>null</code>. {@link #createTempProject()} can be used.
	 */
	public VirtualWikiFile(WikiProject project, String name, boolean sitemap, boolean parse, StringBuffer content) {
		super(project, name, sitemap, parse);
		assert this.project != null;
		this.content = content;
	}
	
	public boolean validLocation() {
		return true;
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
	
	public void write() { }
	
	public static WikiProject createTempProject() throws IOException {
		WikiProject project;
		File f = File.createTempFile("test", "txt");
		f.delete();
		f.mkdir();
		f.deleteOnExit();
		project = new WikiProject(f.getParent());
		return project;
	}
	
}
