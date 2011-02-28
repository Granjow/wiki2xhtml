package src.tasks;

import src.Constants.Tags;
import src.Constants.Wiki2xhtml;
import src.project.file.WikiFile;
import src.tasks.Tasks.Task;

public class WikiTags extends WikiTask {

	public WikiTask nextTask() {
		return new WikiCleanup();
	}

	public Task desc() {
		return Task.Tags;
	}

	
	public void parse(WikiFile file) {
		file.setContent(new StringBuffer(
				file.getContent().toString()
				.replace(Tags.version, Wiki2xhtml.versionNumber)
				.replace(Tags.wiki2xhtml, String.format(
						"<a href=\"http://wiki2xhtml.sourceforge.net\">wiki2xhtml</a> %s (%s)", 
						Wiki2xhtml.versionNumber, Wiki2xhtml.versionDate
						))
				.replace(Tags.pagename, file.internalName())
				.replaceAll("\\\\\\\\ \\n?", "<br/>\n")
				.replaceAll("(^|\n)----($|\n)", "\n<hr/>\n")
				));
	}
	

}
