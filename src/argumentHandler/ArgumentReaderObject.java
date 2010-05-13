package src.argumentHandler;

import java.util.ArrayList;

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
 * <p>Used for reading arguments which are separated with a pipe symbol.
 * Arguments may have a name (name=arg).</p>
 * <p>To avoid storing the ArrayList etc locally ...</p>
 *
 * @author Simon Eugster
 */
public class ArgumentReaderObject {

	/** Contains the plain string of the given arguments. */
	protected String argsPlain = "";
	public ArrayList<ArgumentItem> argsList = new ArrayList<ArgumentItem>();

	/** Can read arguments from a string */
	public ArgumentReaderObject() { }
	/** Can read arguments from a string
	 *  @param args String with arguments, separated by pipe symbols */
	public ArgumentReaderObject(String args) {
		setArguments(args);
	}

	/**
	 * @return The arguments of the previously set argument string, as a list.
	 */
	public ArrayList<ArgumentItem> getArguments() {
		argsList = ArgumentReader.getArguments(argsPlain);
		return argsList;
	}
	/**
	 * Sets arguments which are separated by a pipe symbol.
	 * Arguments may contain CDATA sections.
	 * @param args
	 */
	public void setArguments(String args) {
		if (args != null) {
			if (args.length() > 0 && !args.startsWith("|"))
				argsPlain = '|' + args;
			else
				argsPlain = args;
			argsList = ArgumentReader.getArguments(argsPlain);
		}
	}

	/** Returns the number of arguments */
	public int size() {
		return argsList.size();
	}

}
