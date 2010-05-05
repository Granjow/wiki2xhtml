package src.project;

import src.project.file.WikiFile;
import java.util.HashMap;

public class WikiProject {
	
	// Files
	// With ID, StringBuffer, etc. for each file
	
	// Settings
	
	private HashMap<Integer, WikiFile> fileMap;
	
	public WikiFile getFile(int id) {
		return fileMap.get(id);
	}
	

}
