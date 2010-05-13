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
 * <p>Parser for the <strong><code>if</code></strong> function 
 * (true if first argument contains something, wrong otherwise).</p>
 * 
 * <p>Examples:
 * <ul>
 * 	<li><code>{{#if: not empty | Insert this | Don't insert this }}</code></li>
 *  <li><code>{{#if:           | Don't insert| Insert (purging spaces/tabs at the beginning/end) }}</code></li>
 * </ul>
 * </p>
 *
 * @author Simon Eugster
 *
 * @since wiki2xhtml 3.4
 */

public class ParserFunctionIf extends ParserFunction {

	@Override
	public String parse(ArrayList<ArgumentItem> args) {

		if (args.size() < 2)
			return "";

		if (trimmed(args, 0, TrimManner.CUT_OFFSET).length() > 0)
			return trimmed(args, 1);
		else {
			if (args.size() >= 3)
				return trimmed(args, 2);
			else
				return "";
		}

	}

	@Override
	protected int getOffset() {
		return Constants.ParserFunctions.pIf.length();
	}

}
