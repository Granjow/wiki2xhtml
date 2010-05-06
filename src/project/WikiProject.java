package src.project;

import src.project.file.WikiFile;
import src.project.settings.Settings.SettingContext;
import src.tasks.*;

import java.io.File;
import java.util.HashMap;

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
		p.addFile(new WikiFile(new File("/tmp/a"), new File("/tmp/out"),"myname", false,false));
		p.make();
	}

}
