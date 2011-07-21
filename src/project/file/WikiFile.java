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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Constants.Template_Images;
import src.Constants.Template_References;
import src.project.WikiProject;
import src.project.settings.GalleryProperties;
import src.project.settings.ImageProperties;
import src.project.settings.LocalSettings;
import src.project.settings.PageSettings;
import src.ptm.PTMState;
import src.resources.ResProjectSettings.SettingsE;
import src.resources.ResProjectSettings.SettingsLocalE;
import src.tasks.WikiLinks;
import src.tasks.WikiPreparser;
import src.tasks.WikiTask;
import src.tasks.Tasks.Task;
import src.tasks.WikiLinks.NamespaceObject;


/**
 * A single wiki file belonging to a WikiProject
 */
public abstract class WikiFile {
	
	protected StringBuffer content = null;
	
	public final boolean sitemap;
	public long timeParsingMillis = 0;
	
	/** <p>Filename of this file.<br/>
	 * Usually denotes the file name relative to the project directory. Some examples:</p>
	 * <ul>
	 * <li><code>project/filename1</code> with <code>filename1 = index.txt</code><li>
	 * <li><code>project/filename2</code> with <code>filename2 = pics/summer2010.txt</code></li>
	 * </ul> 
	 * @see #internalName()
	 * @deprecated until correct usage of internalName is checked.
	 */
	public final String name;
	private String internalName;
	/**
	 * <p>An internal representation of the output file name, which always uses / as file separator
	 * and nothing else. To be used in Wiki-Links, for OS-wide consistency.</p>
	 * @see #name
	 */
	public String internalName() { return internalName; }
	public final Generators generators;
	
	public final WikiProject project;
	
	protected boolean alreadyRead = false;
	/** List of all WikiTasks to be executed. Can be adjusted if necessary. */
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private PageSettings pageSettings = new PageSettings();
	private LocalSettings localSettings = new LocalSettings();
	
	public ArrayList<String> nowiki = new ArrayList<String>();
	/** @see #addImageProperties(ImageProperties) for inserting new properties! */
	public ArrayList<ImageProperties> imagePropertiesList = new ArrayList<ImageProperties>();
	/** @see #addGalleryProperties(GalleryProperties) for inserting new properties! */
	public ArrayList<GalleryProperties> galleryPropertiesList = new ArrayList<GalleryProperties>();
	/** <p>List of reference bindings.</p>
	 * <p>See {@link Template_References} for the bound names.</p> */
	public HashMap<String, PTMState> references = null;
	/** List of available link namespaces */
	private ArrayList<NamespaceObject> linkNamespaces;
	
	
	
	protected WikiFile(WikiProject project, String name, boolean sitemap) {
		this.name = name;
		this.project = project;
		this.sitemap = sitemap;
		this.generators = new Generators(this);
		this.linkNamespaces = new ArrayList<NamespaceObject>();
		for (Task t : Task.values()) {
			tasks.add(t);
		}
		if (File.separator.equals("/")) {
			internalName = name;
		} else {
			internalName = name.replace(File.separator, "/");
		}
	}
	
	abstract public boolean equals(Object obj);
	/** Verifies the location of the file. E.g. it might need to be located in the project directory or a subdirectory. */
	abstract public boolean validLocation();
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
	/** @param fallback Allows to fall back to a less prioritised value (file -> project -> default)
	 * @return The property as String, or <strong>{@code null}</strong>, if the property was never set. */
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
	

	/** Adds an image property and directly returns its number in the list.
	 * Additionally, sets {@link Template_Images#number} for {@code prop}. */
	public int addImageProperties(ImageProperties prop) {
		imagePropertiesList.add(prop);
		int nr = imagePropertiesList.indexOf(prop);
		prop.argumentBindings.b(Template_Images.number, Integer.toString(nr));
		return nr;
	}
	/** Adds an gallery property and directly returns its number in the list 
	 * Additionally, sets {@link Template_Images#number} for {@code prop}. */
	public int addGalleryProperties(GalleryProperties prop) {
		galleryPropertiesList.add(prop);
		int nr = galleryPropertiesList.indexOf(prop);
		prop.sigma.b(Template_Images.number, Integer.toString(nr));
		return nr;
	}
	
	/**
	 * Returns a list of namespaces (default ones from {@link NamespaceObject#defaultNamespaces()}
	 * plus custom ones set with {@link SettingsE#namespace}). For the usage see {@link WikiLinks}.
	 */
	public final ArrayList<NamespaceObject> getNamespaces() {
		
		// Put all namespaces into linkNamespaces if it hasn't been initialized yet
		if (linkNamespaces.size() == 0) {
			// Add some default namespaces
			linkNamespaces.addAll(NamespaceObject.defaultNamespaces());
			
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
	
	public Locale getLocale() {
		// TODO read locale
		return Locale.ENGLISH;
	}
	
	
	
	
	public void parse() {
		long start = System.currentTimeMillis();
		WikiTask task = new WikiPreparser();
		do {
			if (tasks.contains(task.desc())) {
				task.parse(this);
			} else {
//				System.err.printf("Omitted: %s (%s)\n", task.desc().name, task.desc().description);
			}
		} while ((task = task.nextTask()) != null);
		timeParsingMillis += System.currentTimeMillis() - start;
	}
	
	
	
	
	


	
	
	
	public static void main(String[] args) {
		Pattern p = Pattern.compile("(?m)^(.+?)=(.+)$");
		Matcher m = p.matcher("a=b\nab=cd|cut\na\nb");
		while (m.find()) {
			System.out.println(m.group());
		}
	}

}
