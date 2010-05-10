package src.project;

import src.Constants;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.project.settings.PageSettings;
import src.project.settings.Settings;
import src.resources.ResProjectSettings.SettingsE;

import java.util.HashMap;

/*
 *   Copyright (C) 2007-2010 Simon Eugster <granjow@users.sf.net>

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

public class WikiProject {
	
	// Files
	// With ID, StringBuffer, etc. for each file
	
	// Settings
	private Settings<SettingsE, String> projectSettings = new PageSettings();
	
	private int id = 0;
	private int nextID() {
		return id++;
	}
	
	/** All files in the project */
	private HashMap<Integer, WikiFile> fileMap = new HashMap<Integer, WikiFile>();
	
	
	public WikiProject() {
		/** Initialize global settings with default values */
		projectSettings.set_(SettingsE.imagepagesDir, Constants.Directories.imagePages);
		projectSettings.set_(SettingsE.imagepageImgWidth, Constants.Standards.widthImgImagepages);
		projectSettings.set_(SettingsE.thumbWidth, Constants.Standards.widthThumbs);
		projectSettings.set_(SettingsE.imagepageTitle, Constants.Standards.imagepageCaption);
		projectSettings.set_(SettingsE.galleryImagesPerLine, Constants.Standards.galleryImagesPerLine);
	}
	
	
	public WikiFile getFile(int id) {
		return fileMap.get(id);
	}
	
	public void addFile(WikiFile f) {
		fileMap.put(nextID(), f);
	}
	
	public String getProperty(SettingsE property) {
		return projectSettings.get_(property);
	}
	
	
	
	public void make() {
		// Make the project
		for (WikiFile f : fileMap.values()) {
			f.parse();
		}
	}
	
	
	public static void main(String[] args) {
		WikiProject p = new WikiProject();
		StringBuffer sb = new StringBuffer();
		sb.append("Hallo. [[link.html]]\n*bla");
		VirtualWikiFile vf = new VirtualWikiFile(p,"myname", false,true,sb);
		p.addFile(vf);
		p.make();
		System.out.println(vf.getContent());
	}

}
