/*
 *   Copyright (C) 2010-2011 Simon A. Eugster <simon.eu@gmail.com>

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
 */


package src.tasks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Statistics;
import src.project.file.WikiFile;
import src.resources.RegExpressions;
import src.tasks.Tasks.Task;

/**
 * Handles ''italic'', '''bold''' text.
 */
public class WikiFormattings extends WikiTask {


	public WikiTask nextTask() {
		return new WikiParagraphs();
	}

	public Task desc() {
		return Task.Formattings;
	}

	public void parse(WikiFile file) {
		file.setContent(makeCode(makeItalicType(makeBoldType(file.getContent()))));
	}
	
	

	/**
	 * @return Input where '''text marked as bold''' is replaced with <strong>text marked as bold</strong>
	 */
	public static final StringBuffer makeBoldType(final StringBuffer in) {

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
	 * @return  Input where ''text marked as italic'' is replaced with <em>text marked as italic</em>
	 */
	public static final StringBuffer makeItalicType(final StringBuffer in) {

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
	
	//TODO Doc removed functions
	public static final StringBuffer makeCode(final StringBuffer input) {

		// TODO use template
		Statistics.getInstance().sw.timeFormattingCode.continueTime();

		StringBuffer in = input;
		
		StringBuffer out = new StringBuffer();
		String args;

		Pattern p;
		Matcher m;
		int first, last;
		
		p = RegExpressions.textCode;
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

		p = RegExpressions.textCodeBlock;
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


		Statistics.getInstance().sw.timeFormattingCode.stop();

		return out;
	}

	/**
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
	

}
