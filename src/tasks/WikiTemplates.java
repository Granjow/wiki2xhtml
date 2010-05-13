package src.tasks;

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
		file.setContent(TemplateManager.applyTemplates(file.getContent()));
	}

}
