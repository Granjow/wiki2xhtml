package src.tasks;

import src.project.file.WikiFile;
import src.ptm.PTM;
import src.tasks.Tasks.Task;

public class WikiParserFunctions extends WikiTask {
	
	public Task desc() {
		return Task.ParserFunctions;
	}
	public WikiTask nextTask() {
		return new WikiTables();
	}
	public void parse(WikiFile file) {
		file.setContent(new StringBuffer(PTM.parse(file.getContent())));
	}

}
