package src.parserFunctions;

import java.util.ArrayList;

import src.Constants;
import src.argumentHandler.ArgumentItem;

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
 * <p>Parser for the <strong><code>switch</code></strong> function.</p>
 * 
 * <p>Examples:
 * <ul>
 * 	<li><code>{{#switch: testExpression | wrong=not_this | testExpression=Insert this text. }}</code></li>
 *  <li><code>{{#switch: testExpression <br />| wrong=not_this<br />| wrong too=Don't insert| #default = Insert default value!}}</code></li>
 * </ul>
 * </p>
 *
 * @author Simon Eugster
 *
 * @since wiki2xhtml 3.4
 */

public class ParserFunctionSwitch extends ParserFunction {

	@Override
	public String parse(ArrayList<ArgumentItem> args) {

		//System.out.println("Name: " + args.get(1).name + ", Argument: " + args.get(1).argument);

		if (args.size() < 2)
			return "";

		String s = trimmed(args, 0, TrimManner.CUT_OFFSET);

		for (int i = 1; i < args.size(); i++) {
			// Find suitable argument
			if (s.equals(args.get(i).name))
				return trimmed(args, i, TrimManner.NOT_FULL_ARG);
		}

		for (int i = 1; i < args.size(); i++) {
			// Nothing suitable found: Return default value if available.
			if (Constants.ParserFunctions.defaultValue.equals(args.get(i).name))
				return trimmed(args, i, TrimManner.NOT_FULL_ARG);
		}

		return "";
	}

	@Override
	protected int getOffset() {
		return Constants.ParserFunctions.pSwitch.length();
	}
}
