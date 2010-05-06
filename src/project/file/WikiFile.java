package src.project.file;

import java.io.File;
import java.io.IOException;

import src.Constants.SettingsE;
import src.Constants.SettingsLocalE;
import src.project.WikiProject;
import src.project.settings.PageSettings;
import src.project.settings.Settings;
import src.tasks.WikiLinks;
import src.tasks.WikiTask;
import src.utilities.IORead_Stats;
import src.utilities.IOWrite_Stats;

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
 *   along with this http://www.gnu.org/licenses/.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

public class WikiFile {
	
	private StringBuffer content = null;
	
	public final boolean sitemap;
	public final boolean parse;
	public final String name;
	public final Generators generators;
	
	private final File fSrc;
	private final File fDest;
	private final WikiProject project;
	
	private boolean alreadyRead = false;
	private Settings<SettingsE, String> pageSettings = new PageSettings();
	private Settings<SettingsLocalE, String> localSettings = new Settings<SettingsLocalE, String>() {
		public String nullValue() {return null;};
		protected String concatenate(String left, String right) { return left+right; }
	};
	
	
	/** Needs to be overridden for comparing. */
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj instanceof WikiFile) {
			WikiFile wf = (WikiFile) obj;
			return fSrc.equals(wf.fSrc) && fDest.equals(wf.fDest);
		}
		return false;
	}
	
	public WikiFile(File fSrc, File fDest, WikiProject project, String name, boolean sitemap, boolean parse) {
		this.fSrc = fSrc;
		this.fDest = fDest;
		this.name = name;
		this.project = project;
		this.sitemap = sitemap;
		this.parse = parse;
		this.generators = new Generators(this);
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
			IOWrite_Stats.writeString(fDest, getContent().toString(), false);
		} catch (IOException e) {}
	}
	
	public String getProperty(SettingsE property, boolean fallback) {
		if (pageSettings.isSet(property)) { return pageSettings.get_(property); }
		else { return project.getProperty(property); }
	}
	public String getProperty(SettingsLocalE property) {
		return localSettings.get_(property);
	}
	
	public boolean setProperty(SettingsE property, String value) {
		return pageSettings.set_(property, value);
	}
	public boolean setProperty(SettingsLocalE property, String value) {
		return localSettings.set_(property, value);
	}
	
	public void parse() {
		if (!parse) return;
		WikiTask task = new WikiLinks();
		do {
			task.parse(this);
		} while ((task = task.nextTask()) != null);
	}
	

}
