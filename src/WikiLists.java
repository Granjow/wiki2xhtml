package src;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.utilities.HandlerLists;



/*
 *   Copyright (C) 2007-2009 Simon Eugster <granjow@users.sf.net>

 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 *
 * Edits block levels (<li> etc)
 * TODO 1 ; a :: b
 *
 * @author Simon Eugster
 */
public class WikiLists {

	public static final PrintStream o = System.out;

	/**
	 * Build the Lists with a *, # at the beginning.
	 *
	 * @param in - input
	 * @return input with lists
	 */
	public static StringBuffer makeList(StringBuffer in) {
		@SuppressWarnings("unused")
		StringBuffer funcName = new StringBuffer("BlockLevels.makeList");
		Statistics.getInstance().sw.timeCreatingLists.continueTime();

		StringBuffer out = new StringBuffer();
		BufferedReader b = new BufferedReader(new StringReader(in.toString()));
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
					Matcher m = Resources.Regex.listGroupArguments.matcher(line);

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

		in = out;
		Matcher m = Pattern.compile("\\s*<\\/dl>\\s<dl>\\s*").matcher(in.toString());
		if (m.find()) {
			out = new StringBuffer();
			int first, last = 0;
			do {
				counter++;
				first = m.start();
				out.append(in.subSequence(last, first));
				out.append(XHTML.lineSep);
				last = m.end();
			} while (m.find());
			out.append(in.subSequence(last, in.length()));
		}

		Statistics.getInstance().sw.timeCreatingLists.stop();

		return out;
	}

}
