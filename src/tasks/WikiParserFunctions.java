package src.tasks;

import src.parserFunctions.Parser;
import src.project.file.WikiFile;
import src.tasks.Tasks.Task;

public class WikiParserFunctions extends WikiTask {
	
	public Task desc() {
		return Task.ParserFunctions;
	}
	public WikiTask nextTask() {
		return new WikiLinks();
	}
	public void parse(WikiFile file) {
		file.setContent(Parser.parse(file.getContent()));
	}

}
