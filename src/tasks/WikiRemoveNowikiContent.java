package src.tasks;

import java.util.regex.Matcher;

import src.Statistics;
import src.project.file.WikiFile;
import src.resources.RegExpressions;
import src.tasks.Tasks.Task;

public class WikiRemoveNowikiContent extends WikiTask {

	public Task desc() {
		return Task.RemoveNowiki;
	}
	public WikiTask nextTask() {
		return new WikiParserFunctions();
	}

	/**
	 * Temporarily removes &lt;nowiki&gt; content.
	 */
	public void parse(WikiFile file) {
		StringBuffer in = file.getContent();
		Matcher m = RegExpressions.nowikiContent.matcher(in);

		if (!m.find()) {
			return;
		} else {
			m.reset();

			StringBuffer out = new StringBuffer();
			int last = 0;
			while (m.find()) {
				Statistics.getInstance().counter.nowikiRemoved.increase();
				out.append(in.substring(last, m.start()));

				file.nowiki.add(m.group(1));
				/*
				 * Insert the nowiki-tags again that the
				 * expression can be inserted afterwards at the right place
				 */
				out.append("<nowiki></nowiki>");

				last = m.end();
			}
			out.append(in.substring(last, in.length()));

			in.setLength(0);
			in.append(out);
		}
	}
	
}
