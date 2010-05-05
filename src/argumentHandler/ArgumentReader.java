package src.argumentHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

import src.Resources;

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
 * Reads arguments parted with a | and containing CDATA sections.
 *
 * @author Simon Eugster
 */
public class ArgumentReader {

	/**
	 * Reads arguments parted by a pipe symbol from a string.
	 * The string may contain CDATA sections.
	 * @param args
	 * @return The arguments as a list.
	 */
	public static ArrayList<ArgumentItem> getArguments(String args) {
		ArrayList<ArgumentItem> arguments = new ArrayList<ArgumentItem>();

		if (args == null || args.length() == 0)
			return arguments;

		if (!args.startsWith("|"))
			args = "|" + args;

		Matcher m = Resources.Regex.argument.matcher(args);
		int i = 0;
		while (m.find()) {
			arguments.add(new ArgumentItem(m.group(1), i));
			i++;
		}

		return arguments;
	}

	/**
	 * See {@link #getArguments(String)}.
	 * @param args
	 * @return The arguments as a (searchable) HashMap.
	 */
	public static HashMap<String, ArgumentItem> getArgumentsMap(String args) {
		HashMap<String, ArgumentItem> map = new HashMap<String, ArgumentItem>();

		ArrayList<ArgumentItem> argsList = getArguments(args);

		for (ArgumentItem ai : argsList) {
			map.put(ai.name, ai);
		}

		return map;
	}

	public static void main(String[] args) {
		String s = "test|b||dd";

		ArrayList<ArgumentItem> a = getArguments(s);
		System.out.print("Arguments in " + s + ":" +
						 "\n             ");
		for (int i = 0; i < s.length() + 1; i++) {
			System.out.print(i%10);
		}
		System.out.println();
		int i = 0;
		for (ArgumentItem g : a) {
			System.out.println("Argument " + i + ": " + g);
			i++;
		}
	}

}
