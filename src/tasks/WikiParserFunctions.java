package src.tasks;

import src.project.file.WikiFile;
import src.ptm.PTM;
import src.ptm.PTMObject.RecursionException;
import src.tasks.Tasks.Task;

public class WikiParserFunctions extends WikiTask {
	
	public Task desc() {
		return Task.ParserFunctions;
	}
	public WikiTask nextTask() {
		return new WikiTables();
	}
	public void parse(WikiFile file) {
		try {
			file.setContent(new StringBuffer(PTM.parse(file.getContent(), file.project.projectDirectory())));
		} catch (RecursionException e) {
			e.printStackTrace();
			System.err.println("ERROR: Recursion could not be removed. In: " + file.name);
		}
	}

}
