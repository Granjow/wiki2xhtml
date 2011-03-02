/*
 *   Copyright (C) 2007-2011 Simon A. Eugster <simon.eu@gmail.com>

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


package src.utilities;

import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * Tools for the work with Strings.
 */
public class StringTools {

	/**
	 * @param s Input CSV
	 * @return A vector containing the values.
	 */
	public static Vector<String> importSimpleCSV(String s) {
		Vector<String> v = new Vector<String>();

		if (s == null || s.length() == 0)
			return v;

		int i = 0;
		if (s.indexOf(',') > 0) {
			Vector<String> v_ = new Vector<String>();

			do {
				v_.add(s.substring(i, (i = s.indexOf(',', i+1)) ));
				i++;
			} while (i < s.lastIndexOf(','));

			/* Add the last element. If there is an empty one (,,) before, take i+1. */
			if (i > 0)
				v_.add(s.substring((i == s.lastIndexOf(',') ? i + 1 : i), s.length()));

			for (String str : v_) {
				str = str.trim();
				if (str.length() > 0)
					v.add(str);
			}

		} else
			v.add(s);

		return v;
	}

	/**
	 * @param v
	 * @return The elements in v, separated with a comma and a space, nothing more.
	 */
	public static StringBuffer exportSimpleCSV(Vector<String> v) {
		StringBuffer sb = new StringBuffer();

		if (v == null || v.size() == 0)
			return sb;

		for (String s : v)
			sb.append(s + ", ");

		/* remove the last «, » */
		sb.delete(sb.length() - 2, sb.length());

		return sb;
	}

	/**
	 * Arguments: arg1 "arg 2" "arg \"3\""
	 * @param s
	 * @return The connected arguments in a vector.
	 */
	public static Vector<String> getConnectedArgs(String s) {
		Vector<String> v = new Vector<String>();

		if (s == null || s.length() == 0)
			return v;

		Pattern p = Pattern.compile("(?<!\\\\)\"");
		Matcher m = p.matcher(s);

		/*
		 * If the string contains a quotation mark, enter the search for
		 * quoted names. Otherwise simply separate them where spaces are.
		 */
		if (m.find()) {
			Pattern nospace = Pattern.compile("\\S");
			Matcher mNS;

			String[] tiles;

			Vector<Integer> positions = new Vector<Integer>();
			positions.add(m.start());
			while (m.find()) {
				positions.add(m.start());
			}

			for (int nr = 0; nr < positions.size(); nr++) {
				/*
				 * If it is the first quotation mark, the string between this position and the last one
				 * has to be splitted where spaces occur. Otherwise it has to be taken with the spaces.
				 */
				if (nr % 2 == 0) {
					String sub = s.substring((nr == 0 ? 0 : positions.get(nr - 1)), positions.get(nr));
					if (sub.length() > 0) {
						mNS = nospace.matcher(sub);
						if (mNS.find()) {
							tiles = sub.split("\\s+");
							for (String tile : tiles) {
								v.add(tile);
//								System.err.println("1>" + tile);
							}

						}
					}
				} else {
					v.add(s.substring(positions.get(nr-1) + 1, positions.get(nr)));
//					System.err.println("2>" + s.substring(positions.get(nr-1) + 1, positions.get(nr)));
				}
			}


			/*
			 * Finally, treat the rest if there is one.
			 */
			String tile = s.substring(positions.get(positions.size() - 1) + 1, s.length());
			if (tile.length() > 0) {
				if ((positions.size() - 1) % 2 == 0) {
					v.add(tile);
				} else {
					String[] tiles_ = tile.split("\\s+");
					for (String tile_ : tiles_) {
						if (tile_.length() > 0) {
							v.add(tile_);
//							System.err.println("3>" + tile_);
						}
					}
				}
			}


		} else {
			String[] tiles = s.split("\\s+");
			for (String tile : tiles)
				v.add(tile);
		}

		return v;
	}

	/**
	 * Adds quotation marks if the String contains a space.
	 */
	public static String quoteIfContainingSpace(String s) {
		if (s.indexOf(" ") > 0) {
			if (!s.startsWith("\"") && !s.endsWith("\""))
				return "\"" + s + "\"";
		}
		return s;
	}

	/**
	 * Counts how many times a pattern occurs in a string
	 */
	public static int countString(String s, String pattern) {
		int counter = 0, last = -1;
		while ((last = s.indexOf(pattern, last + 1)) >= 0) {
			counter++;
		}
		return counter;
	}

	/**
	 * Fills a StringBuffer with spaces to a certain length
	 *
	 * @return Input StringBuffer filled with spaces, if it wasn't longer than
	 *         max length
	 */
	public static StringBuffer fillUp(String s, int length) {
		StringBuffer out = new StringBuffer(s);
		if (s.length() < length)
			for (int i = s.length(); i <= length; i++)
				out.append(" ");
		return out;
	}

	/**
	 * Removes all chars at the beginning and/or at the end of the input.
	 *
	 * @param in input
	 * @param c Char to remove
	 * @param start Remove at the beginning?
	 * @param end Remove at the end?
	 */
	public static StringBuffer removeChars(StringBuffer in, char c, boolean start, boolean end) {
		if (in == null)
			return new StringBuffer();
		StringBuffer s = new StringBuffer(in);
		try {
			if (start)
				while (s.length() > 0 && s.charAt(0) == c) {
					s.deleteCharAt(0);
				}
			if (end)
				while (s.length() > 0 && s.charAt(s.length() - 1) == c) {
					s.deleteCharAt(s.length() - 1);
				}
		} catch (IndexOutOfBoundsException e) {
			System.out.println(e);
		}
		return s;
	}

	/**
	 * Removes e.g. all new lines (\n)
	 */
	public static StringBuffer removeAllChars(StringBuffer in, String pattern) {
		if (pattern != null && pattern.length() > 0) {
			int i = 0;
			int l = pattern.length();
			while ((i = in.indexOf(pattern, i)) >= 0) {
				in.delete(i, i + l);
			}
		}
		return in;
	}

	/**
	 * @return A (human readable) string of a time in milliseconds, up to days
	 */
	public static String formatTimeMilliseconds(long milliSeconds) {
		return formatTimeNanoseconds((long) (milliSeconds * Math.pow(10, 6)));
	}

	public static enum Precision {
		DAY,
		HOUR,
		MINUTE,
		SECOND,
		MILLISECOND,
		MICROSECOND,
		NANOSECOND
	}

	/** @see #formatTimeNanoseconds(long, Precision) */
	public static String formatTimeNanoseconds(long nanoSeconds) {
		return formatTimeNanoseconds(nanoSeconds, Precision.NANOSECOND);
	}
	/**
	 * @return A (human readable) string of a time in nanoseconds, up to days
	 */
	public static String formatTimeNanoseconds(long nanoSeconds, Precision precision) {
		String time = new String();
		String sNs, sUs, sMs, sS, sMin, sH, sD;

		// Calculate sw
		int ms =	(int) ( (nanoSeconds%Math.pow(10, 9))/Math.pow(10, 6) );
		int mues =	(int) ( (nanoSeconds%Math.pow(10, 6))/Math.pow(10, 3) );
		int ns =	(int) ( (nanoSeconds%Math.pow(10, 3)) );
		int sFull =	(int) ( ( (nanoSeconds - nanoSeconds%Math.pow(10, 9)) / Math.pow(10, 9) ) );
		int s = 	(int) ( sFull % 60 );
		int mFull =	(int) ( (sFull - s) /60 );
		int min =	(int) ( mFull % 60 );
		int hFull =	(int) ( (mFull - min ) /60 );
		int h = 	(int) ( hFull %24 );
		int dFull =	(int) ( (hFull - h) / 24 );



		// Assemble string
		sD = (dFull > 0? dFull + " d " : "");
		sH = (h > 0? h + " h " : "");
		sMin = (min > 0? min + " min " : "");
		sS = (s > 0? s + " s " : "");
		sMs = (ms > 0? ms + " ms " : "");
		sUs = (mues > 0? mues + " µs " : "");
		sNs = (ns > 0? ns + " ns " : "");

		for (int i = 0; i == 0; i++) {
			// Hack to avoid multiple IFs; better way?
			time = sD;
			if (precision == Precision.DAY) break;
			time += sH;
			if (precision == Precision.HOUR) break;
			time += sMin;
			if (precision == Precision.MINUTE) break;
			time += sS;
			if (precision == Precision.SECOND) break;
			time += sMs;
			if (precision == Precision.MILLISECOND) break;
			time += sUs;
			if (precision == Precision.MICROSECOND) break;
			time += sNs;
		}

		if (time.length() == 0) {
			time = "0 ns";
		}

		return time.trim();
	}

	public static StringBuffer replaceOnce(StringBuffer in, String pattern, String replace) {
		int pos;
		int l = pattern.length();

		pos = in.indexOf(pattern);
		if (pos >= 0) {
			in.delete(pos, pos + l);
			in.insert(pos, replace);
		}

		return in;
	}

	public static StringBuffer stringArrayToSB(String[] in, String part) {
		StringBuffer sb = new StringBuffer();
		int l = in.length;
		for (int i = 0; i < l; i++)
			if (in[i] != null)
				sb.append(in[i] + part);

		return sb;
	}

}
