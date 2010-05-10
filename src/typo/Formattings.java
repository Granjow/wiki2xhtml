package src.typo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.*;

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
 * Does a lot of formatting like bold type.
 *
 * @since wiki2xhtml 3.3: $$code$$, ...
 *
 * @author Simon Eugster
 */
public class Formattings {

	static I18n i18n = I18nFactory.getI18n(Formattings.class, "bin.l10n.Messages", src.Globals.getLocale());

	public static final int BOLD = 1 << 0;
	public static final int ITALIC = 1 << 1;
	public static final int LINKS = 1 << 2;
	public static final int MANY = 1 << 3;

	/**
	 * @param in
	 * @param comment
	 * @return Input where '''text marked as bold''' is replaced with <strong>text marked as bold</strong>
	 */
	public static StringBuffer makeBoldType(StringBuffer in, boolean comment) {

		StringBuffer out = new StringBuffer();
		Matcher m = Pattern.compile("'''(.*?)'''").matcher(in.toString());;

		if (m.find()) {

			Statistics.getInstance().sw.timeFormattingCode.continueTime();
			int first, last = 0;
			short counter = 0;

			do {
				first = m.start();
				out.append(in.subSequence(last, first));
				out.append("<strong>");
				last = m.end();
				out.append(m.group(1));
				out.append("</strong>");
				counter++;
			} while (m.find());
			out.append(in.subSequence(last, in.length()));


			/* Statistics */
			Statistics.getInstance().counter.boldType.increase(counter);
			Statistics.getInstance().sw.timeFormattingCode.stop();

		} else
			return in;

		return out;
	}

	/**
	 *
	 * @param in
	 * @param comment
	 * @return  Input where ''text marked as italic'' is replaced with <em>text marked as italic</em>
	 */
	public static StringBuffer makeItalicType(StringBuffer in, boolean comment) {

		StringBuffer out = new StringBuffer();
		Matcher m = Pattern.compile("''(.*?)''").matcher(in.toString());

		if (m.find()) {
			Statistics.getInstance().sw.timeFormattingCode.continueTime();
			int first, last = 0;
			short counter = 0;

			do {
				first = m.start();
				out.append(in.subSequence(last, first));
				out.append("<em>");
				last = m.end();
				out.append(m.group(1));
				out.append("</em>");
				counter++;
			} while (m.find());
			out.append(in.subSequence(last, in.length()));

			/* Statistics */
			Statistics.getInstance().sw.timeFormattingCode.stop();
			Statistics.getInstance().counter.italicType.increase(counter);

		} else
			return in;

		return out;
	}

	public static StringBuffer makeManyThings(StringBuffer in, boolean comment) {

		Statistics.getInstance().sw.timeFormattingCode.continueTime();

		StringBuffer out = new StringBuffer();
		String args;

		Pattern p;
		Matcher m;
		int first, last;

		/* DEL tags */
		p = Resources.Regex.textDel;
		m = p.matcher(in);

//		Statistics.getInstance().sw.temp.continueTime();

		first = 0;
		while (m.find()) {
			last = m.start();

			args = (m.group(2) != null ?
					" " + m.group(2).trim()
					: "");

			out.append(in.subSequence(first, last) + m.group(1) + "<del" + args + ">" + m.group(3) + "</del>");

			first = m.end();
		}
		out.append(in.subSequence(first, in.length()));


//		Statistics.getInstance().sw.temp.stop();



		/* INS tags */
		in = out;
		out = new StringBuffer();

		p = Resources.Regex.textIns;
		m = p.matcher(in);

		first = 0;
		while (m.find()) {
			last = m.start();

			args = (m.group(2) != null ? " " + m.group(2).trim() : "");

			out.append(in.subSequence(first, last) + m.group(1) + "<ins" + args + ">" + m.group(3) + "</ins>");

			first = m.end();
		}
		out.append(in.subSequence(first, in.length()));


		/* CODE tags */
		in = out;
		out = new StringBuffer();

		p = Resources.Regex.textCode;
		m = p.matcher(in);

		first = 0;
		while (m.find()) {
			last = m.start();
			args = (m.group(2) != null ? " " + m.group(2).trim() : "");

			out.append(in.subSequence(first, last) + m.group(1) + "<code" + args + ">" + m.group(3) + "</code>");

			first = m.end();
		}
		out.append(in.subSequence(first, in.length()));

		in = out;
		out = new StringBuffer();

		p = Resources.Regex.textCodeBlock;
		m = p.matcher(in);

		first = 0;
		while (m.find()) {
			last = m.start();
			args = (m.group(1) != null ? " " + m.group(1).trim() : "");

			out.append(
				in.subSequence(first, last) +
				"\n<code" +
				(
					(args = combineArgs("class=\"block\"", args)).length() > 0
					? " " + args
					: "") +
				">" +
				m.group(2) +
				"</code>\n"
			);

			first = m.end();
		}
		out.append(in.subSequence(first, in.length()));


		/* SAMP tags */
		in = out;
		out = new StringBuffer();

		p = Resources.Regex.textSample;
		m = p.matcher(in);

		last = 0;
		while (m.find()) {
			args = (m.group(2) != null ? " " +m.group(2).trim() : "");

			out.append(in.subSequence(last, m.start()) + m.group(1) + "<samp" + args + ">" + m.group(3) + "</samp>");

			last = m.end();
		}
		out.append(in.subSequence(last, in.length()));

		in = out;
		out = new StringBuffer();

		p = Resources.Regex.textSampleBlock;
		m = p.matcher(in);

		first = 0;
		while (m.find()) {
			last = m.start();
			args = (m.group(1) != null ? " " + m.group(1).trim() : "");

			out.append(
				in.subSequence(first, last) +
				"\n<samp" +
				(
					(args = combineArgs("class=\"block\"", args)).length() > 0
					? " " + args
					: "") +
				">" +
				m.group(2) +
				"</samp>\n"
			);

			first = m.end();
		}
		out.append(in.subSequence(first, in.length()));


		/* CITE tags */
		in = out;
		out = new StringBuffer();

		p = Resources.Regex.textCite;
		m = p.matcher(in);

		first = 0;
		while (m.find()) {
			last = m.start();
			args = (m.group(2) != null ? " " + m.group(2).trim() : "");

			out.append(in.subSequence(first, last) + m.group(1) + "<cite" + args + ">" + m.group(3) + "</cite>");

			first = m.end();
		}
		out.append(in.subSequence(first, in.length()));

		in = out;
		out = new StringBuffer();

		p = Resources.Regex.textCiteBlock;
		m = p.matcher(in);

		first = 0;
		while (m.find()) {
			last = m.start();
			args = (m.group(1) != null ? " " + m.group(1).trim() : "");

			out.append(
				in.subSequence(first, last) +
				"\n<cite" +
				(
					(args = combineArgs("class=\"block\"", args)).length() > 0
					? " " + args
					: "") +
				">" +
				m.group(2) +
				"</cite>\n"
			);

			first = m.end();
		}
		out.append(in.subSequence(first, in.length()));


		/* KBD tags */
		in = out;
		out = new StringBuffer();

		p = Resources.Regex.textKbd;
		m = p.matcher(in);

		first = 0;
		while (m.find()) {
			last = m.start();
			args = (m.group(2) != null ? " " + m.group(2).trim() : "");

			out.append(in.subSequence(first, last) + m.group(1) + "<kbd" + args + ">" + m.group(3) + "</kbd>");

			first = m.end();
		}
		out.append(in.subSequence(first, in.length()));

		in = out;
		out = new StringBuffer();

		p = Resources.Regex.textKbdBlock;
		m = p.matcher(in);

		first = 0;
		while (m.find()) {
			last = m.start();
			args = (m.group(1) != null ? " " + m.group(1).trim() : "");

			out.append(
				in.subSequence(first, last) +
				"\n<kbd" +
				(
					(args = combineArgs("class=\"block\"", args)).length() > 0
					? " " + args
					: "") +
				">" +
				m.group(2) +
				"</kbd>\n"
			);

			first = m.end();
		}
		out.append(in.subSequence(first, in.length()));

		Statistics.getInstance().sw.timeFormattingCode.stop();

		return out;
	}

	/**
	 * @param arg
	 * @param argUser
	 * @return Both strings together with classes and style arguments united
	 */
	public static String combineArgs(String arg, String argUser) {
		Pattern p;
		Matcher mU;
		Matcher m;
		boolean processed = false;

		p = Pattern.compile("(class=\")([^\"]+)(\")");
		mU = p.matcher(argUser);
		m = p.matcher(arg);

		if (m.find() && mU.find()) {
			argUser = mU.replaceFirst((mU.group(1) + mU.group(2)+ " " + m.group(2) + mU.group(3)).trim());
			processed = true;
		}

		p = Pattern.compile("(style=\")([^\"]+)(\")");
		mU = p.matcher(argUser);
		m = p.matcher(arg);

		if (m.find() && mU.find()) {
			argUser = mU.replaceFirst((mU.group(1) + mU.group(2)+ " " + m.group(2) + mU.group(3)).trim());
			processed = true;
		}

		if (!processed)
			argUser += " " + arg;

		return argUser.trim();
	}

	/**
	 * @param in
	 * @param currentFilename Used for disabling links to current page
	 * @param flags
	 * @param comment
	 * @return Input formatted with the desired options
	 */
	public static StringBuffer format0r(StringBuffer in, String currentFilename, int flags, boolean comment) {

		if ((flags & BOLD) > 0)
			in = makeBoldType(in, comment);
		if ((flags & ITALIC) > 0)
			in = makeItalicType(in, comment);
		if ((flags & MANY) > 0)
			in = makeManyThings(in, comment);

		if ((flags & LINKS) > 0)
			in = WikiLinks.makeLinks(in, currentFilename);
		return in;
	}

}
