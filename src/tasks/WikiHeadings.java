package src.tasks;

import java.util.ArrayList;
import java.util.regex.Matcher;

import src.Resources;
import src.Statistics;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.resources.RegExpressions;
import src.tasks.Tasks.Task;


public class WikiHeadings extends WikiTask {
	
	private ArrayList<String> headingIDs;
	
	public Task desc() {
		return Task.Headings;
	}
	public WikiTask nextTask() {
		return new WikiLinks();
	}
	/**
	 * Replaces the headings marked with p.e. == Heading == with the according
	 * hX-Tags from HTML, here
	 * &lt;h2&gt;Heading&lt;/h2&gt;.
	 */
	public void parse(WikiFile file) {
		headingIDs = new ArrayList<String>();//TODO 0 position of initialization matters?
		Statistics.getInstance().sw.timeMakingHeadings.continueTime();
		
		StringBuffer in = file.getContent();
		StringBuffer out = new StringBuffer();
		int last;

		for (byte i = 6; i >= 2; i--) {

			Matcher mHeading = RegExpressions.wikiHeading(i).matcher(in);
			last = 0;

			if (!mHeading.find())
				continue;
			mHeading.reset();

			while (mHeading.find()) {
				out.append(in.substring(last, mHeading.start()));
				Statistics.getInstance().counter.headings.increase();

				out.append(getHeadingEntry(mHeading.group(1), i));

				last = mHeading.end();
			}
			out.append(in.substring(last, in.length()));

			if (i > 2) {
				file.setContent(out);
				out = new StringBuffer();
			}
		}

		/* Statistics */
		Statistics.getInstance().sw.timeMakingHeadings.stop();
		
		if (out.length() > 0) file.setContent(out);
	}

	/**
	 * Creates an entry for a heading: Heading text surrounded with tags which have an ID.
	 * @param heading Heading text
	 * @param level Level of the heading (usually 2-6)
	 * @return The heading entry
	 */
	private StringBuffer getHeadingEntry(String heading, int level) {
		StringBuffer out = new StringBuffer();

		// Replace leading and final spaces
		heading = heading.trim();

//		ca.ol("(" + heading + ")", CALevel.DEBUG);

		out.append("<h" + level + " id=\"" + getIDtoHeading(heading, headingIDs) + "\">");
		out.append(heading);
		out.append("</h" + level + ">");

		return out;
	}

	/**
	 * Creates an ID as XML Name for a heading.
	 * @param heading The heading itself, used for generating the ID
	 * @param usedHeadingIDs IDs already used in this document, to avoid duplicates
	 * @return A (unique) ID for the heading
	 */
	public static final String getIDtoHeading(String heading, ArrayList<String> usedHeadingIDs) {

		StringBuilder id = new StringBuilder("h_");

		Matcher mNameChar = Resources.XmlNames.patternNameChar.matcher(heading);

		while (mNameChar.find()) {
			id.append(mNameChar.group());
		}

		while (usedHeadingIDs != null && usedHeadingIDs.contains(id.toString())) {
			id.append('_');
		}

		if (usedHeadingIDs != null) {
			usedHeadingIDs.add(id.toString());
		}

		return id.toString();
	}
	
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("== Titel ==");
		VirtualWikiFile vf = new VirtualWikiFile(null, "me.html", false, true, sb);
		vf.removeAllTasks();
		vf.addTask(Task.Headings);
		vf.parse();
		System.out.println(vf.getContent());
	}
	

}
