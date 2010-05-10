package src.typo;

import java.util.regex.*;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.utilities.StringTools;

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
 * Will perhaps correct some typographical mistakes in future.
 *
 * TODO 4 Typography correction depending on doc language
 *
 * @author Simon Eugster
 */
public class Typograph {

	private static I18n i18n = I18nFactory.getI18n(Typograph.class, "bin.l10n.Messages", src.Globals.getLocale());
	private StringBuffer content = new StringBuffer();

	//Typo: dash spaces(abbr) zoll quot

	public void setContent(StringBuffer sb) {
		content = sb;
	}

	public StringBuffer getContent() {
		return content;
	}

	public void replace(String arguments) {
		content = replace(content, arguments);
	}

	public StringBuffer replace(StringBuffer in, String arguments) {
		boolean all = false;

		StringBuffer out = in;

		if (arguments.indexOf("all") >= 0)
			all = true;

		if (all || arguments.contains("s"))
			out = makeSpaces(in);

		if (all || arguments.contains("d"))
			out = makeDashes(in);

		if (all || arguments.contains("z"))
			out = makeZollChars(in);

		if (all || arguments.contains("q"))
			out = makeQuotationMarks(in);

		return out;
	}

	public StringBuffer makeSpaces(StringBuffer in) {
		StringBuffer out;
		short counter = 0;
		int first, last = 0;

		// \\p{Z}: Unicode-Whitespace
		Matcher m = Pattern.compile("[\\s\\p{Z}(](z\\.B\\.|d\\.h\\.|v\\.a\\.|u\\.a\\.|s\\.o\\.)[\\s)\\p{Z},]").matcher(in.toString());
		if (m.find()) {
			out = new StringBuffer();
			do {
				counter++;
				first = m.start();
				out.append(in.subSequence(last, first));
				out.append(StringTools.replaceOnce(new StringBuffer(m.group()), ".", ".&thinsp;"));
				// subtract 1 from the end that the space is added too
				last = m.end();
			} while (m.find());
			out.append(in.subSequence(last, in.length()));

			return out;
		}

		return in;
	}

	public static void main(String[] args) {
//		Typograph t = new Typograph();

//		t.setContent(new StringBuffer("das ist z.B. ein Beispiel,	d.h. es ist wirklich (s.o.) so. text ist u.a.,  "));
//		t.replace("s");

//		System.out.println(t.getContent());
	}

	public StringBuffer makeDashes(StringBuffer in) {
		StringBuffer out = in;
		short counter = 0;
		int first, last = 0;

		/*
		 * Replace the dashes. Dashes have a' ' before and after the '-'.
		 * Replace the - with &ndash;.
		 */

		Matcher m = Pattern.compile(" - ").matcher(in.toString());
		if (m.find()) {
			out = new StringBuffer();
			do {
				counter++;
				first = m.start();
				out.append(in.subSequence(last, first + 1));
				out.append("&ndash;");
				// subtract 1 from the end that the space is added too
				last = m.end() - 1;
			} while (m.find());
			out.append(in.subSequence(last, in.length()));
			counter = 0;
			last = 0;
			in = out;
		}

		m = Pattern.compile(" -- ").matcher(in.toString());
		if (m.find()) {
			out = new StringBuffer();
			do {
				counter++;
				first = m.start();
				out.append(in.subSequence(last, first + 1));
				out.append("&ndash;");
				// subtract 1 from the end that the space is added too
				last = m.end() - 1;
			} while (m.find());
			out.append(in.subSequence(last, in.length()));
			last = 0;
			in = out;
		}

		m = Pattern.compile(" --,").matcher(in.toString());
		if (m.find()) {
			out = new StringBuffer();
			do {
				counter++;
				first = m.start();
				out.append(in.subSequence(last, first));
				out.append("&nbsp;&ndash;");
				// subtract 1 from the end that the space is added too
				last = m.end() - 1;
			} while (m.find());
			out.append(in.subSequence(last, in.length()));
			counter = 0;
			last = 0;
			in = out;
		}

		return out;
	}

	public StringBuffer makeZollChars(StringBuffer in) {
		StringBuffer out = new StringBuffer();
		short counter = 0;
		int first, last = 0;

		/*
		 * Replace the inch char
		 */

		// finds a number followed by \" (" has to be escaped).
		Matcher m = Pattern.compile("(\\d+)\\\\\" ").matcher(in.toString());
		if (m.find()) {
			// last = 0;
			// counter = 0;
			out = new StringBuffer();
			do {
				counter++;

				first = m.start();
				out.append(in.subSequence(last, first));
				// don't count space
				last = m.end() - 1;

				out.append(m.group(1));
				out.append(i18n.tr("&Prime;"));
			} while (m.find());
			out.append(in.subSequence(last, in.length()));

			return out;
		}

		return in;
	}

	public StringBuffer makeQuotationMarks(StringBuffer in) {
		StringBuffer out = new StringBuffer();
		short counter = 0;
		int first, last = 0;

		Matcher m = Pattern.compile(" \"(.+?)\"").matcher(in.toString());
		if (m.find()) {
			// last = 0;
			out = new StringBuffer();
			do {
				counter++;

				// don't count space
				first = m.start() + 1;

				out.append(in.subSequence(last, first));

				// add specific quotation marks
				out.append(i18n.tr("&ldquo;"));

				last = m.end();
				out.append(m.group(1));

				out.append(i18n.tr("&rdquo;"));
			} while (m.find());
			out.append(in.subSequence(last, in.length()));

//			notes.append(i18n.tr("Note: if you want to make a zoll char instead of a quotation, put a backslash (\\) before the char: \\\"\n"));

			return out;
		}

		return in;
	}

}
