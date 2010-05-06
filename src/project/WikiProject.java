package src.project;

import src.project.file.WikiFile;
import src.project.settings.Settings.SettingContext;
import src.tasks.*;

import java.io.File;
import java.util.HashMap;

/*
 *   Copyright (C) 2007-2009 Simon Eugster <granjow@users.sf.net>

 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

public class WikiProject {
	
	// Files
	// With ID, StringBuffer, etc. for each file
	
	// Settings
	
	private int id = 0;
	private int nextID() {
		return id++;
	}
	
	private HashMap<Integer, WikiFile> fileMap;
	
	
	public WikiProject() {
		fileMap = new HashMap<Integer, WikiFile>();
	}
	
	
	public WikiFile getFile(int id) {
		return fileMap.get(id);
	}
	
	public void addFile(WikiFile f) {
		fileMap.put(nextID(), f);
	}
	
	public String getSetting(int fileID, String what, SettingContext context) {
		
	}
	
	
	
	public void make() {
		// Make the project
		WikiTask task = new WikiLinks();
		for (int id : fileMap.keySet()) {
			task.parse(this, id);
			getFile(id).write();
		}
	}
	
	
	public static void main(String[] args) {
		WikiProject p = new WikiProject();
		p.addFile(new WikiFile(new File("/tmp/a"), new File("/tmp/out"),p,"myname", false,false));
		p.make();
	}

}
