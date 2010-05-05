package src;

import java.util.regex.Matcher;

import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;


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
 * Handles nowiki-Tags
 *
 * @author Simon Eugster
 */
public class WikiNoWiki {

	private static final CommentAtor ca = CommentAtor.getInstance();

	/**
	 * Temporarily removes &lt;nowiki&gt; content.
	 * @param in
	 * @return
	 */
	public static StringBuffer removeNowikiContent(StringBuffer in) {
		Matcher m = Resources.Regex.nowikiContent.matcher(in);

		if (!m.find()) {
			return in;
		} else {
			m.reset();

			StringBuffer out = new StringBuffer();
			int last = 0;
			while (m.find()) {
				Statistics.getInstance().counter.nowikiRemoved.increase();
				out.append(in.substring(last, m.start()));

				XHTML.nowiki.add(m.group(1));
				/*
				 * Insert the nowiki-tags again that the
				 * expression can be inserted afterwards at the right place
				 */
				out.append("<nowiki></nowiki>");

				last = m.end();
			}
			out.append(in.substring(last, in.length()));

			return out;
		}
	}

	/**
	 * Re-inserts removed &lt;nowiki&gt; content.
	 * @param in
	 * @return
	 */
	public static StringBuffer insertNowikiContent(StringBuffer in) {

		Matcher m = Resources.Regex.nowikiContent.matcher(in);

		if (!m.find()) {
			return in;
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
					out.append(XHTML.nowiki.get(counter));
				} catch (IndexOutOfBoundsException e) {
					ca.ol(String.format("Error while trying to insert nowiki-content Nr. %d!", counter), CALevel.ERRORS);
					e.printStackTrace();
				}

				counter++;
				last = m.end();
			}
			out.append(in.substring(last, in.length()));

			return out;
		}
	}
}
