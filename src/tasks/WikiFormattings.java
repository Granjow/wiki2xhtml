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

import src.Constants.Blocks;
import src.Container_Resources;
import src.Statistics;
import src.project.WikiProject.FallbackFile;
import src.project.file.WikiFile;
import src.ptm.PTMObject.RecursionException;
import src.ptm.PTMRootNode;
import src.ptm.PTMState;
import src.resources.RegExpressions;
import src.tasks.Tasks.Task;
import src.utilities.Tuple;

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
		file.setContent(makeCode(file, makeItalicType(makeBoldType(file.getContent()))));
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
	
	//TODO Doc removed functions, arguments
	public static final StringBuffer makeCode(final WikiFile file, final StringBuffer input) {

		Statistics.getInstance().sw.timeFormattingCode.continueTime();
		
		StringBuffer template;
		try {
			template = new FallbackFile(Container_Resources.sTplCode, file.project).getContent();
		} catch (Exception e) {
			e.printStackTrace();
			template = new StringBuffer(e.getMessage());
		}
		assert template.length() > 0;
		
		StringBuffer in = input;
		StringBuffer out = new StringBuffer();
		StringTuple tuple;
		String s;
		String args;
		String argClass;
		String argStyle;

		PTMState sigma;
		
		Pattern p;
		Matcher m;
		int first;
		
		p = RegExpressions.textCode;
		m = p.matcher(in);

		first = 0;
		while (m.find()) {
			args = (m.group(2) != null ? " " + m.group(2).trim() : "");
			if (args.length() > 0) {
				tuple = extractClassArgs(args);
				argClass = tuple.k();
				args = tuple.v();
				tuple = extractStyleArgs(args);
				argStyle = tuple.k();
				args = tuple.v().trim();
				args = " " + args;
			} else {
				argClass = "";
				argStyle = "";
			}
			
			sigma = new PTMState()
				.b(Blocks.args, args)
				.b(Blocks.classes, argClass)
				.b(Blocks.style, argStyle)
				.b(Blocks.text, m.group(3));
			
			out.append(m.group(1));
			
			try {
				s = new PTMRootNode(template, sigma).evaluate();
			} catch (RecursionException e) {
				e.printStackTrace();
				s = e.getMessage();
			}
			out.append(s);

			first = m.end();
		}
		out.append(in.subSequence(first, in.length()));

		
		
		in = out;
		out = new StringBuffer();

		p = RegExpressions.textCodeBlock;
		m = p.matcher(in);

		first = 0;
		while (m.find()) {
			args = (m.group(1) != null ? " " + m.group(1).trim() : "");
			if (args.length() > 0) {
				tuple = extractClassArgs(args);
				argClass = tuple.k();
				args = tuple.v();
				tuple = extractStyleArgs(args);
				argStyle = tuple.k();
				args = tuple.v().trim();
				args = " " + args;
			} else {
				argClass = "";
				argStyle = "";
			}
			
			sigma = new PTMState()
				.b(Blocks.args, args)
				.b(Blocks.classes, argClass)
				.b(Blocks.style, argStyle)
				.b(Blocks.text, m.group(2))
				.b(Blocks.isBlock, "true");
			
			try {
				s = new PTMRootNode(template, sigma).evaluate();
			} catch (RecursionException e) {
				e.printStackTrace();
				s = e.getMessage();
			}
			out.append(s);
			
			first = m.end();
		}
		out.append(in.subSequence(first, in.length()));


		Statistics.getInstance().sw.timeFormattingCode.stop();

		return out;
	}
	
	private static final Pattern pClassArgs = Pattern.compile("class=\"([^\"]+)\"");
	/**
	 * @return <ClassArgs, RemainingArgs>
	 */
	private static final StringTuple extractClassArgs(String args) {
		Matcher m = pClassArgs.matcher(args);
		if (m.find()) {
			String classArgs = m.group(1);
			String rest = args.substring(0, m.start()) + args.substring(m.end());
			return new StringTuple(classArgs, rest);
		} else {
			return new StringTuple("", args); 
		}
	}
	
	private static final Pattern pStyleArgs = Pattern.compile("style=\"([^\"]+)\"");
	/**
	 * @return <ClassArgs, RemainingArgs>
	 */
	private static final StringTuple extractStyleArgs(String args) {
		Matcher m = pStyleArgs.matcher(args);
		if (m.find()) {
			String classArgs = m.group(1);
			String rest = args.substring(0, m.start()) + args.substring(m.end());
			return new StringTuple(classArgs, rest);
		} else {
			return new StringTuple("", args); 
		}
	}
	
	private static final class StringTuple extends Tuple<String, String> {
		public StringTuple(String classArgs, String rest) {
			super(classArgs, rest);
		}
	}

}
