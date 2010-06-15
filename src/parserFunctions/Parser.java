package src.parserFunctions;

import java.util.ArrayList;

import src.Statistics;
import src.argumentHandler.ArgumentItem;
import src.argumentHandler.ArgumentReader;

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
 * <del>Will, once big and mighty, evaluate any parser function (if, ifeq, switch etc).</del>
 * <p><ins>Handles already if, ifeq, and switch functions.</ins></p>
 *
 * TODO 0 replace functions in the head too
 *
 * @author Simon Eugster
 *
 * @since wiki2xhtml 3.4
 */
public class Parser {

	private static final String startExpr = "{{", endExpr = "}}";

	/**
	 * Parses (supported) functions like {@link #ParserFunctionIf}
	 * @param in
	 * @return
	 */
	public static StringBuffer parse(StringBuffer in) {

		Statistics.getInstance().sw.timeFunctionParser.continueTime();

		Pos p = evaluate(in);

		Statistics.getInstance().sw.timeFunctionParser.stop();

		return p.sb;
	}
	public static String parse(String in) {
		return parse(new StringBuffer(in)).toString();
	}

	/**
	 * Evaluates all appearances of {{foo}} recursively and, if foo is a (supported) function, tries to parse it.
	 * @param sb Input
	 * @return Input with parsed functions
	 */
	private static Pos evaluate(StringBuffer sb) {

		boolean parsed = false;
		int pO = 0, pC = 0;
		int last = 0;
		Pos pos = new Pos();
		Pos temp = new Pos();


//		System.out.println("%%%START%%%");
		do {
			pO = sb.indexOf(startExpr, pos.pos);
			pC = sb.indexOf(endExpr, pos.pos);

			if (pO > pC || pO < 0 || pC < 0)
				break;
			else {

				pos.sb.append(sb.substring(last, pO));

				temp = evaluate(new StringBuffer(sb.subSequence(pO + startExpr.length(), sb.length())));
				if (temp.pos > 0) {
					sb.replace(pO + startExpr.length(), sb.length(), temp.sb.toString());
					pC = sb.indexOf(endExpr, pO + startExpr.length());
				}

				// Stays the same if no expression was found above
				pC = (sb.indexOf(endExpr, pC) > 0 ? sb.indexOf(endExpr, pC) : pC);

				pos.sb.append(parseExpression(sb.substring(pO, pC + endExpr.length())));
				
				// To decide if the parsing output is an empty string: 
				// Parsed (take empty string) or not (take source string)?
				parsed = true;

			}

			pos.pos = pC + endExpr.length();

			last = pos.pos;

		} while (true);

		pos.setLengthWithoutTail();

		if (pos.sb.length() == 0 && !parsed) pos.sb = sb;
		else pos.sb.append(sb.substring(last));

//		System.out.println("%%%END%%%");

		return pos;
	}

	/**
	 * Parses an expression
	 * @param s Expression to parse
	 * @return Original value, if not a known/supported expression, or (hopefully) the desired value.
	 */
	private static String parseExpression(String s) {

		/* s without brackets */
		String expr = s.substring(startExpr.length(), s.length() - endExpr.length());

		/* Read the arguments of s, leaving opening and closing brackets away */
		ArrayList<ArgumentItem> al = ArgumentReader.getArguments(expr);
		ParserFunction pf = null;

		if (al.size() > 0) {
			pf = ParserFactory.createFunctionParser(expr);
		}

		/* Full argument with brackets needs to be returned @param s was not a function;
		 * otherwise the brackets will be left away. */
		String st = (pf == null) ? s : pf.parseIt(al);
//		System.out.println(">>" + st + "<<");
		return st;
	}

	private static class Pos {

		/** Just a temporary position variable */
		int pos = 0;
		/** Storing the length of the content after the last function was parsed;
		 * rest will probably need to be cut away later during recursion */
		private int lengthWithoutTail = 0;

		/** Store current length of the content; see {@link #lengthWithoutTail} */
		public void setLengthWithoutTail() {
			lengthWithoutTail = sb.length();
		}
		/** Get the length of the modified content (without unmodified tail); see {@link #lengthWithoutTail} */
		@SuppressWarnings("unused")
		public int lengthWithoutTail() {
			return lengthWithoutTail;
		}

		/** Content */
		StringBuffer sb = new StringBuffer();

	}

	/**
	 * Test function
	 * @param args
	 */
	public static void main(String[] args) {

		StringBuffer sb = new StringBuffer("{{#switch: {{#if:||a}} | a = y{{e}}{{#ifeq:{{#if:a|a|b}}|a|s|t}} | b=false }} {{#switch: dfdf|a=ö|k=ö|{{#ifeq:a|b|c|d}}fdf=yes }} {{#switch: abc | df=bb|d=bbb|#default=ok}}");
//		StringBuffer sb = new StringBuffer("{{#if:||a}} {{#if:d|{{#if:||b}} }}");
		sb = new StringBuffer("{{#if:\t||a}} variable: {{{1}}}");
		StringBuffer out = Parser.parse(sb);

		System.err.println(out);

	}

}
