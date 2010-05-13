package src;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;


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
 *
 * Generates XHTML code for headings h1, h2, ...
 *
 * @author Simon Eugster
 */
public class WikiHeadings {

	private static final CommentAtor ca = CommentAtor.getInstance();

	/**
	 * Replaces the headings marked with p.e. == Heading == with the according
	 * hX-Tags from HTML, here
	 * &lt;h2&gt;Heading&lt;/h2&gt;.
	 *
	 * @param in - the input
	 * @return the input with replaced headings
	 */
	public static StringBuffer makeHeadings(StringBuffer in) {
		Statistics.getInstance().sw.timeMakingHeadings.continueTime();
		XHTML.clearHeadings();

		StringBuffer out = new StringBuffer();
		int last;

		for (byte i = 6; i >= 2; i--) {

			Matcher mHeading = Resources.Regex.wikiHeading(i).matcher(in);
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
				in = out;
				out = new StringBuffer();
			}
		}

		/* Statistics */
		Statistics.getInstance().sw.timeMakingHeadings.stop();

		return out.length() == 0 ? in : out;
	}

	/**
	 * Creates an entry for a heading: Heading text surrounded with tags which have an ID.
	 * @param heading Heading text
	 * @param level Level of the heading (usually 2-6)
	 * @return The heading entry
	 */
	public static StringBuffer getHeadingEntry(String heading, int level) {
		StringBuffer out = new StringBuffer();

		// Replace leading and final spaces
		heading = heading.trim();

		ca.ol("(" + heading + ")", CALevel.DEBUG);

		out.append("<h" + level + " id=\"" + getIDtoHeading(heading) + "\">");
		out.append(heading);
		out.append("</h" + level + ">");

		return out;
	}

	/**
	 * Creates an ID as an XML Name for a heading.
	 * @param heading
	 * @return The ID for the heading
	 */
	public static String getIDtoHeading(String heading) {

		StringBuilder id = new StringBuilder("h_");

		Matcher mNameChar = Resources.XmlNames.patternNameChar.matcher(heading);

		while (mNameChar.find()) {
			id.append(mNameChar.group());
		}

		while (XHTML.headingIDs.contains(id))
			id.append('_');

		XHTML.headingIDs.add(id.toString());

		return id.toString();
	}

	/**
	 * Creates a TOC from an input file. The <hx> have to be at the beginning of
	 * the line!
	 *
	 * @param in The input file
	 * @param xhs Here the TOC is written in
	 */
	public static StringBuffer getTOC(final StringBuffer in) {
		StringBuffer toc = new StringBuffer();

		BufferedReader b = new BufferedReader(new StringReader(in.toString()));
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
					toc.append(addItem(counter, level, line.subSequence(
										   line.indexOf(">") + 1, line.lastIndexOf("<"))
									   .toString(), line));
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
	 * Creates an item for the TOC with the generated ID
	 *
	 * @param n The current number of the heading
	 * @param level The heading's level
	 * @param name The content of the heading. Tags will be removed.
	 * @return Item with the generated ID
	 */
	public static StringBuffer addItem(int n, int level, String name, String line) {
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
			item.append(getIDtoHeading(name));

		item.append("\">" + name + "</a>");

		return item;
	}

}
