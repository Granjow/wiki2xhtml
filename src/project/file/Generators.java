package src.project.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Statistics;
import src.tasks.WikiHeadings;

/*
 *   Copyright (C) 2007-2010 Simon Eugster <granjow@users.sf.net>

 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * Retrieve information from a WikiFile
 */
public class Generators {
	
	private final WikiFile file;
	
	public Generators(WikiFile file) {
		this.file = file;
	}

	/**
	 * Creates a TOC from an input file. The <hx> have to be at the beginning of
	 * the line!
	 */	
	public StringBuffer getTOC() {
		StringBuffer toc = new StringBuffer();

		BufferedReader b = new BufferedReader(new StringReader(file.getContent().toString()));
		byte counter = 0;
		byte level = 0;

		try {
			Pattern pat = Pattern.compile("^<h[2-6]");

			for (String line = b.readLine(); line != null; line = b.readLine()) {
				if (line.length() > 0 && pat.matcher(line).find()) {
					try {
						level = Byte.parseByte(line.charAt(2) + "");
						level--;	// Headings start with level 2
					} catch (NumberFormatException e) {
						e.getStackTrace();
					}
					toc.append(getTOCItem(counter, level, 
							line.subSequence(line.indexOf(">") + 1, line.lastIndexOf("<")).toString(), 
							line));
					counter++;
				}
			}

		} catch (IOException e)  {
			e.printStackTrace();
		}

		/* Statistics */
		Statistics.getInstance().counter.TOC.increase(counter);
		return toc;
	}


	/**
	 * Creates an item (ordered list entry, can finally be converted with 
	 * WikiLists) for the TOC with the generated ID
	 *
	 * @param n The current number of the heading
	 * @param level The heading's level
	 * @param name The content of the heading. Tags will be removed.
	 * @param line Line containing the heading entry, to search for an ID if existing 
	 * @return Item with the generated ID
	 */
	public static final StringBuffer getTOCItem(int n, int level, String name, String line) {
		StringBuffer item = new StringBuffer();
		Pattern pat = Pattern.compile("(?<=id=\").*?(?=\")");
		Matcher m = pat.matcher(line);

		name = name.replaceAll("<.*>", "");

		item.append("\n");
		for (byte i = 0; i < level; i++)
			item.append("#");

		item.append("<a href=\"#");

		if (m.find())
			item.append(m.group());
		else
			item.append(WikiHeadings.getIDtoHeading(name, null));

		item.append("\">" + name + "</a>");

		return item;
	}

}
