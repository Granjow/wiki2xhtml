package src.tasks;

import src.project.file.WikiFile;
import src.tasks.Tasks.Task;

public class WikiPreparser extends WikiTask {

	public Task desc() {
		return Task.Preparse;
	}
	
	public WikiTask nextTask() {
		return new WikiSettings();
	}
	
	public void parse(WikiFile file) {
		file.setContent(new StringBuffer(file.getContent().toString().replace("\r", "")));
	}
	
}
