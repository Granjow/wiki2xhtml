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

package src.project.file;

import java.io.File;
import java.io.IOException;

import src.project.WikiProject;

public class VirtualWikiFile extends WikiFile {

	protected VirtualWikiFile(WikiProject project, String name, boolean sitemap) {
		super(project, name, sitemap);
		content = new StringBuffer();
		assert project != null;
	}

	/** <p>Creates a virtual wiki file.</p>
	 * <p>The project is automatically filled with all tasks. To remove them, call {@link #removeAllTasks()}.</p>
	 * @param project Must not be <code>null</code>. {@link #createTempProject()} or {@link #createEmptyProject()} can be used.
	 */
	public VirtualWikiFile(WikiProject project, String name, boolean sitemap, StringBuffer content) {
		super(project, name, sitemap);
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
	
	/** 
	 * Tries to create a temporary directory where files can be stored in
	 * and sets this directory to the project directory.
	 * @throws IOException if creating the temporary directory fails.
	 * @see #createEmptyProject()
	 */
	public static WikiProject createTempProject() throws IOException {
		WikiProject project;
		File f = File.createTempFile("test", "txt");
		f.delete();
		f.mkdir();
		f.deleteOnExit();
		project = new WikiProject(f.getParent());
		return project;
	}
	
	/**
	 * Creates an empty project with the project directory set to /tmp 
	 * (or the respective temporary directory on the current OS).
	 * @see #createTempProject() if files need to be written into the project directory.
	 */
	public static WikiProject createEmptyProject() {
		WikiProject project = new WikiProject(System.getProperty("java.io.tmpdir"));
		return project;
	}
	
}
