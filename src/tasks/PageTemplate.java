package src.tasks;

import src.project.file.WikiFile;
import src.tasks.Tasks.Task;

public class PageTemplate extends WikiTask {

	public WikiTask nextTask() {
		return new WikiCleanup();
	}

	public Task desc() {
		return Task.PageTemplate;
	}

	public void parse(WikiFile file) {
	}

}
