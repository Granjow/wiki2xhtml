package src.project.file;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.project.WikiProject;
import src.project.settings.LocalSettings;
import src.project.settings.PageSettings;
import src.resources.ResProjectSettings.SettingsE;
import src.resources.ResProjectSettings.SettingsLocalE;
import src.tasks.WikiPreparser;
import src.tasks.WikiTask;
import src.tasks.Tasks.Task;
import src.tasks.WikiLinks.NamespaceObject;

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
	/** List of all WikiTasks to be executed. Can be adjusted if necessary. */
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private PageSettings pageSettings = new PageSettings();
	private LocalSettings localSettings = new LocalSettings();

	private ArrayList<NamespaceObject> linkNamespaces;
	
	
	protected WikiFile(WikiProject project, String name, boolean sitemap, boolean parse) {
		this.name = name;
		this.project = project;
		this.sitemap = sitemap;
		this.parse = parse;
		this.generators = new Generators(this);
		this.linkNamespaces = new ArrayList<NamespaceObject>();
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
	
	
	
	public boolean isPropertySet(SettingsE property, boolean fallback) {
		if (fallback) {
			return pageSettings.isSet(property) || project.isPropertySet(property);
		} else { return pageSettings.isSet(property); }
	}
	public String getProperty(SettingsE property, boolean fallback) {
		if (pageSettings.isSet(property)) { return pageSettings.get_(property); }
		else {
			if (fallback) { return project.getProperty(property); }
			else { return null; }
		}
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
	
	public LocalSettings getLocalSettings() { return localSettings; }
	public PageSettings getPageSettings() { return pageSettings; }
	public void setPageSettings(PageSettings settings) { pageSettings = settings; }
	public void setLocalSettings(LocalSettings settings) { localSettings = settings; }
	
	public boolean addTask(Task t) {
		return tasks.add(t);
	}
	public boolean removeTask(Task t) {
		return tasks.remove(t);
	}
	public void removeAllTasks() {
		tasks.clear();
	}
	
	public final ArrayList<NamespaceObject> getNamespaces() {
		
		// Put all namespaces into linkNamespaces if it hasn't been initialized yet
		if (linkNamespaces.size() == 0) {
			StringBuilder allNamespaces = new StringBuilder();
			
			if (project != null && project.isPropertySet(SettingsE.namespace)) {
				allNamespaces.append(project.getProperty(SettingsE.namespace));
			}
			if (isPropertySet(SettingsE.namespace, false)) {
				allNamespaces.append(SettingsE.namespace.separator());
				allNamespaces.append(getProperty(SettingsE.namespace, false));
			}
			Matcher m = src.resources.RegExpressions.namespace.matcher(allNamespaces.toString());
			while (m.find()) {
				linkNamespaces.add(new NamespaceObject(m.group(1), m.group(2)));
			}
		}
		return linkNamespaces;
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
	
	public static void main(String[] args) {
		Pattern p = Pattern.compile("(?m)^(.+?)=(.+)$");
		Matcher m = p.matcher("a=b\nab=cd|cut\na\nb");
		while (m.find()) {
			System.out.println(m.group());
		}
	}

}
