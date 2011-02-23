package src.tasks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.project.file.WikiFile;
import src.tasks.Tasks.Task;


/**
 * Removes remaining spaces and newlines at the beginning of the file.
 * These may cause troubles in some cases (XML e.g.).
 */
public class WikiCleanup extends WikiTask {
	
	private static final Pattern pSpacesAndNewlines = Pattern.compile("^\\s*");

	public WikiTask nextTask() {
		return null;
	}

	public Task desc() {
		return Task.Cleanup;
	}

	public void parse(WikiFile file) {
		Matcher m = pSpacesAndNewlines.matcher(file.getContent());
		if (m.find()) {
			file.setContent(new StringBuffer(file.getContent().substring(m.group().length())));
		}
		
	}

}
