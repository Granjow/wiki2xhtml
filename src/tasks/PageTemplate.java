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

package src.tasks;


import src.Constants.Template_Page;
import src.project.file.WikiFile;
import src.ptm.PTMObject.RecursionException;
import src.ptm.PTMRootNode;
import src.ptm.PTMState;
import src.resources.ResProjectSettings.SettingsE;
import src.tasks.Tasks.Task;

public class PageTemplate extends WikiTask {

	public WikiTask nextTask() {
		return new WikiCleanup();
	}

	public Task desc() {
		return Task.PageTemplate;
	}

	public void parse(WikiFile file) {
		StringBuffer content;
		try {
			content = file.project.wikiStyle.pageTemplate().getContent();
		} catch (Exception e) {
			content = new StringBuffer(e.getMessage());
			e.printStackTrace();
		}
		
		PTMState sigma = new PTMState()
			.b(Template_Page.text, file.getContent().toString())
			.b(Template_Page.title, file.getProperty(SettingsE.title, true))
			.b(Template_Page.h1, file.getProperty(SettingsE.h1, true))
			.b(Template_Page.homelink, file.getProperty(SettingsE.homelink, true))
			.b(Template_Page.icon, file.getProperty(SettingsE.icon, true))
			.b(Template_Page.author, file.getProperty(SettingsE.author, true))
			.b(Template_Page.lang, file.getProperty(SettingsE.lang, true))
			.b(Template_Page.keywords, file.getProperty(SettingsE.keywords, true));
		
		PTMRootNode root = new PTMRootNode(content, sigma);
		root.setTemplateDirectory(file.project.projectDirectory());
		
		try {
			file.setContent(new StringBuffer(root.evaluate()));
		} catch (RecursionException e) {
			e.printStackTrace();
		}
		
	}

}
