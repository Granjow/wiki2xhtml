package src.tasks;

import src.project.file.WikiFile;
import src.project.settings.PageSettingsReader;
import src.tasks.Tasks.Task;

public class WikiSettings extends WikiTask {

	public Task desc() {
		return Task.Settings;
	}
	
	public WikiTask nextTask() {
		return new WikiLinks();
	}
	
	public void parse(WikiFile file) {
		PageSettingsReader psReader = new PageSettingsReader(file.getContent(), file.getPageSettings());
		psReader.readSettings(true);
		// TODO read local settings
	}
	
}
