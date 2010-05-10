package src.argumentHandler;

import java.util.regex.Matcher;

import src.resources.RegExpressions;

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
 * An &#xab;Argument Item&#xbb; providing the argument itself and its name (given with name=argument) or index.
 *
 * @author Simon Eugster
 */
public class ArgumentItem {

	/** Argument&#x2019;s name or index */
	public String name = "";
	/** Argument itself or {@link #fullArg} if the argument has no name. */
	public String argument = "";
	/** Full argument */
	public String fullArg = "";

	/** See {@link ArgumentItem} */
	public ArgumentItem() { }

	/** See {@link ArgumentItem} */
	public ArgumentItem(String s, int index) {
		fullArg = s;

		Matcher m = RegExpressions.templateParameter.matcher(s);
		if (m.find()) {
			name = m.group(1);
			argument = m.group(2);
		} else {
			name = "" + index;
			argument = s;
		}
	}

	public static void main(String[] args) {
		ArgumentItem ai;
		String[] s = new String[] {"test", "1=asdf", "arg2=bbbb", "hello world = not an argument"};

		for (int i = 0; i < s.length; i++) {
			ai = new ArgumentItem(s[i], i);
			System.out.println(ai.name + " -- " + ai.argument);
		}

	}



}
