package src.parserFunctions;

import src.Constants;

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
 * Factory for a suitable function parser (for if, ifeq, switch and other functions).
 *
 * @author Simon Eugster
 *
 * @since wiki2xhtml 3.4
 */
public class ParserFactory {

	/**
	 * @param s String to parse (starting with <code>if:</code>, <code>switch:</code> etc.)
	 * @return A suitable function parser
	 */
	public static ParserFunction createFunctionParser(String s) {
		if (s == null || s.length() == 0)
			return null;
		else if (s.startsWith(Constants.ParserFunctions.pIf)) {
			return new ParserFunctionIf();
		} else if (s.startsWith(Constants.ParserFunctions.pIfeq)) {
			return new ParserFunctionIfeq();
		} else if (s.startsWith(Constants.ParserFunctions.pSwitch)) {
			return new ParserFunctionSwitch();
		}
		return null;
	}

}
