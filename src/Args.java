package src;

import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;

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
 *
 */

/**
 * This class handles arguments and treats spaces in filenames.
 *
 * @author Simon Eugster
 */
public class Args {

	private static final CommentAtor ca = CommentAtor.getInstance();

	/** Contains all arguments */
	private ArrayList<Argument> args = new ArrayList<Argument>();


	/** An argument container which also handles spaces in filenames. */
	public Args() {	}

	/** An argument container which also handles spaces in filenames. */
	public Args(String s) {
		read(s);
	}


	public static enum GetPolicyE {
		AllArgs, UnhandledOnly;
	}
	public static enum SearchPolicyE {
		Equals, StartsWith;
	}


	/** @return The number of args */
	public int size() {
		return args.size();
	}

	/**
	 * Adds an argument
	 * @return The new Args object
	 */
	public Args add(String s) {
		if (s != null && s.length() > 0) {
			s = s.trim();
			if (s.startsWith("\"") && s.endsWith("\"")) {
				if (s.length() > 2)
					args.add(new Argument(s.substring(1, s.length() - 1)));
			} else
				args.add(new Argument(s));
		}
		return this;
	}

	/**
	 * Adds multiple arguments
	 * @return The new Args object
	 */
	public Args addMulti(String s) {
		if (s == null || s.length() == 0)
			return this;
		Args a = new Args();
		a.read(s);
		append(a);
		return this;
	}

	/**
	 * Adds multiple arguments from an array
	 * @param s Arguments
	 */
	public void add(String[] s) {
		for (String st : s)
			add(st);
	}

	/**
	 * Appends arguments from an other Args object
	 * @return the new Args object
	 */
	public Args append(Args a) {
		for (short s = 0; s < a.size(); s++) {
			args.add(new Argument(a.getPlain(s, GetPolicyE.AllArgs)));
		}
		return this;
	}


	public void setHandled(int i, boolean handled) {
		args.get(i).setHandled(handled);
	}
	public void resetHandled(boolean handled) {
		for (Argument a : args) {
			a.setHandled(false);
		}
	}


	/**
	 * Searches for key in the argument list
	 * @param key
	 * @return -1, if not found, and the position otherwise.
	 */
	public int pos(String key, SearchPolicyE policy) {
		int pos = -1;

		switch (policy) {
		case Equals:
			forLoop:for (int i = 0; i < args.size(); i++) {
				if (args.get(i).arg().equals(key)) {
					pos = i;
					break forLoop;
				}
			}
			break;
		case StartsWith:
			forLoop:for (int i = 0; i < args.size(); i++) {
				if (args.get(i).arg().startsWith(key)) {
					pos = i;
					break forLoop;
				}
			}
			break;
		}

		return pos;
	}


	/** @return The arguments in a String array */
	public String[] getArgsArrPlain() {
		String[] s = new String[args.size()];
		for (short i = 0; i < args.size(); i++)
			s[i] = args.get(i).arg();
		// getPlain(i) would do too here
		return s;
	}

	/** @return All arguments in a StringBuffer.
	 * Arguments containing spaces are put into quotation marks. */
	public StringBuffer getArgs(GetPolicyE policy) {
		StringBuffer sb = new StringBuffer();
		for (short i = 0; i < args.size(); i++) {
			sb.append(' ' + get(i, policy));
		}
		if (sb.length() > 0)
			sb.append(' ');
		return sb;
	}

	/**
	 * @param i
	 * @return The argument i; enclosed with quotation marks if it contains a space
	 */
	public String get(int i, GetPolicyE policy) {
		if (i > args.size() - 1
				|| (policy == GetPolicyE.UnhandledOnly && args.get(i).handled()))
			return "";
		else {
			if (args.get(i).arg().contains(" "))
				return "\"" + args.get(i) + "\"";
			else
				return args.get(i).arg();
		}

	}

	/**
	 * @param i
	 * @return The plain argument without quotation marks
	 */
	public String getPlain(int i, GetPolicyE policy) {
		if (i > args.size() - 1
				|| (policy == GetPolicyE.UnhandledOnly && args.get(i).handled()))
			return "";
		else {
			return args.get(i).arg();
		}
	}

	/**
	 * Reads arguments from a string.
	 * @param s Arguments with spaces have to be enclosed with quotation marks.
	 * 	Quotation marks can be escaped with a backslash.
	 */
	public void read(String s) {

		if (s == null || s.length() == 0)
			;
		else {
			Pattern pQuotationMark = Pattern.compile("(?<!\\\\)\"");
			Matcher mQuotationMark = pQuotationMark.matcher(s);

			/*
			 * If the string contains a quotation mark, enter the search for
			 * quoted names. Otherwise simply separate them where spaces are.
			 */
			if (mQuotationMark.find()) {
				Pattern nospace = Pattern.compile("\\S");
				Matcher mNS;

				String[] tiles;

				Vector<Integer> positions = new Vector<Integer>();
				positions.add(mQuotationMark.start());
				while (mQuotationMark.find()) {
					positions.add(mQuotationMark.start());
				}

				for (int nr = 0; nr < positions.size(); nr++) {
					/*
					 * If it is the first quotation mark, the string between this position and the last one
					 * has to be split where spaces occur. Otherwise it has to be taken with the spaces.
					 */
					if (nr % 2 == 0) {
						String sub = s.substring((nr == 0 ? 0 : positions.get(nr - 1)), positions.get(nr));
						if (sub.length() > 0) {
							mNS = nospace.matcher(sub);
							if (mNS.find()) {
								tiles = sub.split("\\s+");
								for (String tile : tiles) {
									if (tile.length() > 0) args.add(new Argument(tile));
									ca.ol("1>" + tile, CALevel.DEBUG);
								}

							}
						}
					} else {
						String arg = s.substring(positions.get(nr-1) + 1, positions.get(nr));
						if (arg.length() > 0) {
							args.add(new Argument(arg));
							ca.ol("2>" + s.substring(positions.get(nr-1) + 1, positions.get(nr)), CALevel.DEBUG);
						}
					}
				}


				/*
				 * Finally, treat the rest if there is one.
				 */
				String tile = s.substring(positions.get(positions.size() - 1) + 1, s.length());
				if (tile.length() > 0) {
					if ((positions.size() - 1) % 2 == 0) {
						if (tile.length() > 0) args.add(new Argument(tile));
					} else {
						String[] tiles_ = tile.split("\\s+");
						for (String tile_ : tiles_) {
							if (tile_.length() > 0) {
								if (tile.length() > 0) args.add(new Argument(tile));
								ca.ol("3>" + tile_, CALevel.DEBUG);
							}
						}
					}
				}


			} else {
				String[] tiles = s.split("\\s+");
				for (String tile : tiles)
					if (tile.length() > 0) args.add(new Argument(tile));
			}
		}
	}

	public static void main(String[] args) {
		Args a = new Args();
		a.read("test  test2 \"test 2.5 test3\"  d");
		System.out.println(a.getArgs(GetPolicyE.AllArgs));
	}

	/** An argument object with additional information about the argument. */
	private static class Argument {

		private String arg;
		private boolean handled;

		public Argument(String s) {
			arg = s;
		}

		public boolean handled() {
			return handled;
		}
		public String arg() {
			return arg;
		}

		public void setHandled(boolean handled) {
			this.handled = handled;
		}
	}

}
