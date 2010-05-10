package src.utilities;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Container_Files;
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
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 *
 * Removes content between e.g. two XHTML tags and inserts it again later
 *
 * @author Simon Eugster
 */
public class Placeholder {

	/** Start tag/whatever */
	private String patternStart = "";
	/** End tag/whatever */
	private String patternEnd = "";

	/** Temporarily removed content */
	public ArrayList<String> content = new ArrayList<String>();

	/** Generates a new Placeholder which will temporarily remove the content between {@link #patternStart} and part2. */
	public Placeholder(String patternStart, String patternEnd) {
		this.patternStart = patternStart;
		this.patternEnd = patternEnd;
	}

	/**
	 * Removes the content between the specified place holders.
	 * Considers nesting!
	 * @param input
	 * @return The input with removed contents
	 */
	public StringBuffer removeContent(StringBuffer input) {
		StringBuffer out = new StringBuffer();
		content.clear();

		Pattern po = Pattern.compile(patternStart);
		Pattern pc = Pattern.compile(patternEnd);

		Matcher mo = po.matcher(input);
		Matcher mc = pc.matcher(input);

		boolean okay = true;
		int deep = 0;
		int start = 0;
		int contentStart = 0;
		/** Open */
		int o = 0;
		/** Close */
		int c = 0;


		if (okay = mo.find()) {

			out.append(input.substring(start, mo.end()));
			contentStart = mo.end();
			deep++;
			try {
				CommentAtor.getInstance().ol("o>>>" + input.substring(mo.start(), mo.start()+50).replace("\n", "\\ ") + "<<<, " + mo.start(), CALevel.DEBUG);
			} catch (IndexOutOfBoundsException e) {}

		} else {

			return input;

		}

		while (okay) {

			if (okay = mc.find()) {

				c = mc.start();
				try {
					CommentAtor.getInstance().ol(">c>>" + input.substring(c, c+50).replace("\n", "\\ ") + "<<<, " + c, CALevel.DEBUG);
				} catch (IndexOutOfBoundsException e) {}
				deep--;

				if (okay = mo.find()) {

					o = mo.start();
					try {
						CommentAtor.getInstance().ol("o>>>" + input.substring(o, o+50).replace("\n", "\\ ") + "<<<, " + o, CALevel.DEBUG);
					} catch (IndexOutOfBoundsException e) {}

					if (deep == 0 && o > c) {

						out.append(input.substring(c, mo.end()));
						content.add(input.substring(contentStart, mc.start()));

						contentStart = mo.end();
						start = c;

					}

					deep++;

				} else {

					if (deep == 0 && o < c) {

						start = mc.start();

					}

				}

			} else {
				CommentAtor.getInstance().ol(
					String.format("\noooops. Closing element \"%s\" (Regex-Pattern) is missing! " +
								  "Please check whether it was wiki2xhtml's fault and report a bug if it is so. File: %s.",
								  patternEnd,
								  Container_Files.getInstance().currentFilename
								 ), CALevel.ERRORS);
				start = contentStart;
			}

		}
		try {
			content.add(input.substring(contentStart, mc.start()));
		} catch (IllegalStateException e) {

		}
		out.append(input.substring(start, input.length()));

		return out;
//		 err.println("copied! " + fs[i].getPath() + " to "
//		 + dest);
	}

	/**
	 * Re-inserts the removed content
	 * @param input Input
	 * @return The input with inserted contents
	 */
	public StringBuffer insertContent(StringBuffer input) {
		StringBuffer out = new StringBuffer();

		Pattern p = Pattern.compile("(?si)(" + patternStart + ")(" + patternEnd + ")");
		Matcher m = p.matcher(input);
		int last = 0;
		int counter = 0;

		while (m.find()) {
			out.append(input.substring(last, m.start()));
			out.append(m.group(1));

			try {
				out.append(content.get(counter));
			} catch (IndexOutOfBoundsException e) { }

			out.append(m.group(2));
			counter++;
			last = m.end();
		}
		out.append(input.substring(last, input.length()));

		return out;
	}

	/**
	 * Sets the parts between which content will be removed.
	 * @param patternStart
	 * @param patternEnd
	 */
	public void setParts(String patternStart, String patternEnd) {
		if (patternStart != null)
			this.patternStart = patternStart;
		if (patternEnd != null)
			this.patternEnd = patternEnd;
	}

}
