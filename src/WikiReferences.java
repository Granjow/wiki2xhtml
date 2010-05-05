package src;

import java.util.ArrayList;
import java.util.regex.*;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.utilities.StringTools;


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
 * Creates references in a text. References may be given with <ref>content</ref>
 * and inserted with <references />.
 *
 * @author Simon Eugster
 * ,		hb9eia
 *
 * TODO 5 Footnotes
 */
public class WikiReferences {

	I18n i18n = I18nFactory.getI18n(WikiReferences.class, "bin.l10n.Messages", src.Globals.getLocale());

	StringBuffer content = new StringBuffer(),
	referencesList = new StringBuffer();

	private ArrayList<StringBuffer> references = new ArrayList<StringBuffer>();

	boolean refPlaceFound = false;

	public void makeReferences() {
		short counter = 0;

		if (content.length() == 0)
			;
		else {
			short refNr = 0;
			// (?s) makes the dot matching also new lines
			Matcher m = Pattern.compile("(?s)<ref>(.*?)</ref>").matcher(content.toString());
			StringBuffer out = new StringBuffer();
			int last = 0, first;
			if (m.find()) {
				do {
					refNr++;
					counter++;

					first = m.start();
					out.append(content.subSequence(last, first));
					last = m.end();
					out = StringTools.removeChars(out, ' ', false, true);
					references.add(new StringBuffer(m.group(1)));
					out
					.append("<sup><a href=\"#refnote_" + refNr +
							"\" style=\"border-bottom: none; text-decoration: none;\"" +
							" id=\"refmark_" + refNr + "\"" +
							" class=\"refmark\"" +
							">[" + refNr + "]</a></sup>");
				} while (m.find());
				out.append(content.subSequence(last, content.length()));
				content = new StringBuffer(out);
				generateReferencesList();
			}
		}

		/* Statistics */
		Statistics.getInstance().counter.references.increase(counter);
	}

	public void generateReferencesList() {
		referencesList.setLength(0);
		if (references.size() > 0) {
			short i = 0;
			for (StringBuffer s : references) {
				i++;
				referencesList.append("\n#" +
									  (i == 1 ? " ((class=\"references\"))" : "") +
									  " class=\"reference\" id=\"refnote_" + i + "\" | <a href=\"#refmark_" + i +
									  "\">[↑]</a> " + s);
			}

			referencesList = WikiLists.makeList(referencesList);
			insertReferenceList();
		}
	}

	public void insertReferenceList() {
		int pos;
		String s = "<references />";
		if ((pos = content.indexOf(s)) >= 0) {
			refPlaceFound = true;
			content.replace(pos, pos + s.length(), referencesList.toString());
		} else {
			s = "<references/>";
			if ((pos = content.indexOf(s)) >= 0) {
				refPlaceFound = true;
				content.replace(pos, pos + s.length(), referencesList.toString());
			}
		}
	}

}
