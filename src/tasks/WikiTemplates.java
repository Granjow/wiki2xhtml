package src.tasks;

import java.io.FileNotFoundException;

import src.project.file.WikiFile;
import src.tasks.Tasks.Task;
import src.templateHandler.TemplateManager;

public class WikiTemplates extends WikiTask {
	
	public Task desc() {
		return Task.Templates;
	}
	public WikiTask nextTask() {
		return new WikiParserFunctions();
	}
	public void parse(WikiFile file) {
		try {
			file.setContent(TemplateManager.applyTemplates(file.getContent()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
