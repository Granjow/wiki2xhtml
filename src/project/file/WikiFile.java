package src.project.file;

import java.util.ArrayList;

import src.project.WikiProject;
import src.project.settings.PageSettings;
import src.project.settings.Settings;
import src.resources.ResProjectSettings.SettingsE;
import src.resources.ResProjectSettings.SettingsLocalE;
import src.tasks.WikiPreparser;
import src.tasks.WikiTask;
import src.tasks.Tasks.Task;

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
 *   along with this http://www.gnu.org/licenses/.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

public abstract class WikiFile {
	
	protected StringBuffer content = null;
	
	public final boolean sitemap;
	public final boolean parse;
	public final String name;
	public final Generators generators;
	
	private final WikiProject project;
	
	protected boolean alreadyRead = false;
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private Settings<SettingsE, String> pageSettings = new PageSettings();
	private Settings<SettingsLocalE, String> localSettings = new Settings<SettingsLocalE, String>() {
		public String nullValue() {return null;};
		protected String concatenate(String left, String right) { return left+right; }
	};
	
	
	protected WikiFile(WikiProject project, String name, boolean sitemap, boolean parse) {
		this.name = name;
		this.project = project;
		this.sitemap = sitemap;
		this.parse = parse;
		this.generators = new Generators(this);
		if (parse) {
			for (Task t : Task.values()) {
				tasks.add(t);
			}
		}
	}
	
	abstract public boolean equals(Object obj);
	abstract public void write();
	abstract public StringBuffer getContent();
	
	
	public void setContent(StringBuffer newContent) {
		content = newContent;
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
	
	public boolean addTask(Task t) {
		return tasks.add(t);
	}
	public boolean removeTask(Task t) {
		return tasks.remove(t);
	}
	public void removeAllTasks() {
		tasks.clear();
	}
	
	public void parse() {
		if (!parse) return;
		WikiTask task = new WikiPreparser();
		do {
			if (tasks.contains(task.desc())) {
				task.parse(this);
			} else {
				System.err.println("Omitted " + task.desc().name);
			}
		} while ((task = task.nextTask()) != null);
	}
	

}
