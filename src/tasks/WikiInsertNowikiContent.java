package src.tasks;

import java.util.regex.Matcher;

import src.Statistics;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.resources.RegExpressions;
import src.tasks.Tasks.Task;


public class WikiInsertNowikiContent extends WikiTask {

	public Task desc() {
		return Task.InsertNowiki;
	}
	public WikiTask nextTask() {
		return new XMLNames();
	}

	/**
	 * Re-inserts removed &lt;nowiki&gt; content.
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
			int counter = 0;

			while (m.find()) {

				Statistics.getInstance().counter.nowikiInserted.increase();
				out.append(in.substring(last, m.start()));

				/*
				 * Re-insert the expression
				 */
				try {
					out.append(file.nowiki.get(counter));
				} catch (IndexOutOfBoundsException e) {
//					ca.ol(String.format("Error while trying to insert nowiki-content Nr. %d!", counter), CALevel.ERRORS);
					e.printStackTrace();
				}

				counter++;
				last = m.end();
			}
			out.append(in.substring(last, in.length()));

			in.setLength(0);
			in.append(out);
		}
	}
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("Hallo [[link]]. <nowiki>[[link]]</nowiki>");
		VirtualWikiFile vf = new VirtualWikiFile(null, "a", false, true, sb);
		vf.parse();
		System.out.println(vf.getContent());
	}
	
}