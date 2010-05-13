package src.parserFunctions;

import java.util.ArrayList;
import src.Statistics;
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
 * Abstract class for function parsers.
 * 
 * TODO 0 Doc: Parser functions
 *
 * @author Simon Eugster
 *
 * @since wiki2xhtml 3.4
 */
public abstract class ParserFunction {

	/**
	 * @return The number of characters that have to be deleted in the first argument;
	 * usually #if:, #ifeq: etc.
	 */
	protected abstract int getOffset();

	public String parseIt(ArrayList<ArgumentItem> args) {
		Statistics.getInstance().counter.functions.increase();
		return parse(args);
	}

	protected abstract String parse(ArrayList<ArgumentItem> args);

	/**
	 * Trims (removes Spaces at beginning/end) the full argument of @param args at the given @param index;
	 * removes first characters (like #if:) if @oaram offset is set to true.
	 * @param args Argument list
	 * @param index Index of argument.
	 * @param offset Whether to omit characters at the beginning ({@link #getOffset()})
	 * @return Trimmed argument, or "" if index out of bounds.
	 */
	protected String trimmed(ArrayList<ArgumentItem> args, int index, TrimManner tm) {
		if (index >= args.size())
			return "";
		if (tm == TrimManner.CUT_OFFSET)
			return args.get(index).fullArg.substring(getOffset()).trim();
		else if (tm == TrimManner.NOT_FULL_ARG)
			return args.get(index).argument.trim();
		else
			return args.get(index).fullArg.trim();
	}

	/**
	 * Like {@link #trimmed(ArrayList, int, boolean)}, but ignoring offset.
	 * @param args
	 * @param index
	 * @return
	 */
	protected String trimmed(ArrayList<ArgumentItem> args, int index) {
		return trimmed(args, index, null);
	}

	protected enum TrimManner { CUT_OFFSET, NOT_FULL_ARG };

}
