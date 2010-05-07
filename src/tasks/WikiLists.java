package src.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Resources;
import src.Statistics;
import src.XHTML;
import src.project.file.WikiFile;
import src.resources.RegExpressions;
import src.tasks.Tasks.Task;
import src.utilities.HandlerLists;

public class WikiLists extends WikiTask {

	public Task desc() {
		return Task.Lists;
	}
	
	public WikiTask nextTask() {
		return null;
	}
	

	/**
	 * Build the Lists with a *, # at the beginning.
	 *
	 * @param in - input
	 * @return input with lists
	 */
	public void parse(WikiFile file) {
		Statistics.getInstance().sw.timeCreatingLists.continueTime();

		StringBuffer out = new StringBuffer();
		BufferedReader b = new BufferedReader(new StringReader(file.getContent().toString()));
		short counter = 0;
		String allowedChars = "*#;:";

		HandlerLists newList = new HandlerLists(), oldList = new HandlerLists();

		int posArgument;
		boolean fileStart = true;

		/** The argument for li, dd, ... */
		StringBuffer argument = new StringBuffer();
		/** The argument for ul, ol, ... */
		String firstArgument = new String();
		/** The list entry itself */
		StringBuffer content = new StringBuffer();

		try {

			int i;

			for (String line = b.readLine(); line != null; line = b.readLine()) {
				i = 0; // free variable

				oldList = newList.clone();
				newList.clear();

				// Set up the handler
				newList.addSameBase(":;");
				newList.allowChars(allowedChars);

				// Get list structure
				newList.parse(line);

				// Handles closing dd/dt tags
				if ((i = newList.equalEntries(oldList)) >= 1) {
					String replace = oldList.levels().substring(0, i);
					if ((i = newList.size() - 1) < replace.length())
						replace = replace.substring(0, i);
					newList.replace(replace);
				}

				/*
				 * Check for an argument for the ul tag
				 */
				if (newList.size() > 0) {
					Matcher m = RegExpressions.listGroupArguments.matcher(line);

					if (m.find()) {
						firstArgument =  m.group(1).trim();
						if (firstArgument.length() > 0)
							firstArgument = " " + firstArgument;
						line = line.substring(m.end(), line.length());
					} else
						firstArgument = "";
				}

				/*
				 * Check whether the line contains an argument for the <li>-tag
				 */
				posArgument = line.indexOf("|");
				if (posArgument > 0 && newList.size() > 0) {

					// Get the arguments and the content
					argument = new StringBuffer(line.substring(newList.size(), posArgument).trim());
					content = new StringBuffer(line.subSequence(posArgument + 1, line.length()));

					if (argument.length() != 0)
						if (argument.charAt(0) != ' ')
							argument.insert(0, ' ');

				} else { // has no argument
					argument.setLength(0);
					content = new StringBuffer(line.subSequence(newList.size(), line.length()));
				}

				/*
				 * Create HTML Tags
				 */

				if (newList.size() == 0 && oldList.size() == 0) {
					// No need to do anything, no list
					if (!fileStart) out.append(XHTML.lineSep);

				} else {

					// TODO ul > ul?

					// TODO use firstArgument

					/*
					 * If there is p.e. first «**:##» and afterwards that «**:*:» then only
					 * the two «##» have to be closed and the «*:» opened.
					 */

					/*
					 * Prepare
					 */

					newList.getDiffBetween(oldList);

					/*
					 * Close
					 */

					/* Close all old elements */
					for (i = 0; i < oldList.difference(); i++) {
						if (oldList.size() > 0) {
							out.append(Resources.closeItem(oldList.type(oldList.size() - i),  oldList.size() - i));
						}
						if (oldList.size() > newList.size() - newList.difference()) {
							out.append(Resources.closeList(oldList.type(oldList.size() - i), oldList.size() - i + 1));
						}
					}

					if (oldList.size() > newList.size() && newList.size() > 0)
						out.append(Resources.closeItem(oldList.type(newList.size()), newList.size() + 1));

					/* Same length, same structure */
					if (oldList.size() > 0 && newList.size() > 0 && oldList.sameStructureAs(newList)) {
						out.append(Resources.closeItem(oldList.last(), oldList.size()));
						out.append(Resources.openItem(newList.last(), newList.size(), argument.toString()));
						Statistics.getInstance().counter.listItems.increase();
					}

					/*
					 * Open
					 */

					for (i = newList.difference() - 1; i >= 0; i--) {
						if (newList.size() > (oldList.size() - oldList.difference())) {
							out.append(Resources.openList(
										   newList.type(newList.size() - i),
										   newList.size() - i,
										   (i == 0 ? firstArgument.toString() : "")
									   ));
							Statistics.getInstance().counter.lists.increase();
						}
						if (newList.size() > 0) {
							out.append(Resources.openItem(newList.type(newList.size() - i), newList.size() - i, argument.toString()));
							Statistics.getInstance().counter.listItems.increase();
						}
					}

					/*
					 * In special cases li- or dd-tags have to be closed and
					 * reopened to add a new entry or they only have to be
					 * opened.
					 *
					 * If the first element was a ** and the second a *,
					 * there has to be inserted a li tag. In HTML the first
					 * is ul/ul/li, the second ul/_li_.
					 */

					if (oldList.size() > newList.size() && newList.size() > 0
							&& oldList.nearlyEqual(newList.size(), newList)) {
						out.append(Resources.openItem(newList.last(), newList.size(), argument.toString()));
						Statistics.getInstance().counter.listItems.increase();
					}

				} // end else

				if (!fileStart) out.append(content);
				else if (content.length() > 0) {
					fileStart = false;
					out.append(content);
				}
			} // end for

			/*
			 * If file ends with a list, it has to be closed. newline is null,
			 * so the upper for is aborted
			 */
			if (newList.size() > 0) {

				for (i = 0; i < newList.size(); i++) {
					out.append(Resources.closeItem(newList.type(newList.size() - i),  newList.size() - i));
					out.append(Resources.closeList(newList.type(newList.size() - i), newList.size() - i + 1));

				}

				//TODO 4 not closing everything yet

				/*
				for (i = 0; i < oldList.difference(); i++) {
					if (oldList.size() > 0) {
						out.append(Resources.closeItem(oldList.type(oldList.size() - i),  oldList.size() - i));
					}
					if (oldList.size() > newList.size() - newList.difference()) {
						out.append(Resources.closeList(oldList.type(oldList.size() - i), oldList.size() - i + 1));
					}
				}
				*/

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		file.setContent(out);
		Matcher m = Pattern.compile("\\s*<\\/dl>\\s<dl>\\s*").matcher(file.getContent().toString());
		if (m.find()) {
			out = new StringBuffer();
			int first, last = 0;
			do {
				counter++;
				first = m.start();
				out.append(file.getContent().subSequence(last, first));
				out.append(XHTML.lineSep);
				last = m.end();
			} while (m.find());
			out.append(file.getContent().subSequence(last, file.getContent().length()));
		}

		Statistics.getInstance().sw.timeCreatingLists.stop();
	}
	

}
