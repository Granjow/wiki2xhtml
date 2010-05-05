package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.utilities.StringTools;


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
 *
 * Generates html code for tables.
 *
 * TODO 2 columns
 *
 * @author Simon Eugster
 */
public class WikiTables {

	private static CommentAtor ca = CommentAtor.getInstance();

	public static StringBuffer makeTables(StringBuffer in) {
		final String funcName = "Tables.makeTables: ";

		final String closeTable = "</table>";
		final String openTable = "<table";
		final String closeTr = "</tr>";
		final String openTr = "<tr";
		final String closeTh = "</th>";
		final String openTh = "<th";
		final String closeTd = " </td>";
		final String openTd = "<td";
		final String openCap = "<caption";
		final String closeCap = "</caption>";

		StringBuffer out = new StringBuffer();
		BufferedReader b = new BufferedReader(new StringReader(in.toString()));
		boolean opened = false;
		short counterOpen = 0, counterClose = 0;
		int lnr = -1;

		try {
			for (String line = b.readLine(); line != null; line = b.readLine()) {
				lnr++;
				if (line.length() == 0) {
					out.append("\n");
				} else {

					if (line.startsWith("|}")) {
						/*
						 * *******************************************************
						 * A table ENDS here
						 * *******************************************************
						 */
						if (opened) {
							ca.ol(funcName + "Table closed at line " + lnr,
									CALevel.DEBUG);
							out.append("\n" + closeTr + '\n' + closeTable);
							opened = false;
						} else
							out.append('\n' + line);
					} else if (line.startsWith("{|")) {
						/*
						 * *******************************************************
						 * A new table BEGINS here
						 * *******************************************************
						 */
						ca.ol(funcName + "Table opened at line " + lnr, CALevel.DEBUG);
						out.append("\n" + openTable);

						if (line.length() > 2) {
							if (line.charAt(2) != ' ')
								out.append(' ');
							out.append(line.subSequence(2, line.length()));
							ca.ol(funcName + "Arguments added: "
								  + line.subSequence(2, line.length()), CALevel.DEBUG);
						}
						out.append(">");
						out.append('\n' + openTr + '>');
						opened = true;
					} else if (opened) {
						if (line.startsWith("|-")) {
							/*
							 * ***************************************************
							 * |- New row
							 * ***************************************************
							 */
							/* Remove an opening <tr> if |- is the first element in the table */
							if ("<tr>".equals(out.substring(out.length() - 4, out.length()))) {
								out.setLength(out.length() - 4);
							} else {
								out.append("\n" + closeTr);
							}
							StringBuffer s = new StringBuffer(line.subSequence(
																  2, line.length()));
							s = StringTools.removeChars(s, ' ',
														true, true);
							ca.ol(funcName + "removed: " + s, CALevel.DEBUG);
							out.append("\n" + openTr);
							if (s.length() > 0)
								out.append(" " + s);
							out.append(">");


						} else if (line.startsWith("|+")) {
							/* ***************************************************
							 * |+ Caption
							 * ***************************************************
							 */


							/* Remove an opening <tr> if |- is the first element in the table */
							if ("<tr>".equals(out.substring(out.length() - 4, out.length()))) {
								out.setLength(out.length() - 4);
							} else {
								out.append("\n" + closeTr);
							}

							String s = line.subSequence(
										   2, line.length()).toString();
							s = s.replaceFirst("^\\s+", "");
							s = s.replaceFirst("\\s+$", "");

							String[] args = getArguments(s);

							out.append("\n" + openCap);
							if (args[0].length() > 0)
								out.append(" " + args[0]);
							out.append(">" + args[1] + closeCap);

							out.append("\n" + openTr + ">");



						} else if (line.startsWith("!") || line.startsWith("|")) {
							/*
							 * ***************************************************
							 * ! New cell (header)
							 * ***************************************************
							 */
							boolean header = line.startsWith("!");

							if (header)
								out.append("\n" + openTh);
							else
								out.append("\n" + openTd);

							int newPos;
							int oldPos = 1;
							int a, c;
							boolean newHeader, oldHeader = true;

							while (true) {

								/*
								 * Every loop closes the previously opened <th or <td after
								 * having inserted the arguments, adds the content and closes with
								 * a </th> or a </td>. Then a new <th or <td for the next cell is opened.
								 */

								a = line.indexOf("!!", oldPos);
								c = line.indexOf("||", oldPos);

								if (a < 1 && c < 1)
									break;
								else
									if (a < c && a >= 1) {
										newPos = a;
										newHeader = true;
									} else {
										if (c >= 1) {
											newPos = c;
											newHeader = false;
										} else {
											newPos = a;
											newHeader = true;
										}
									}

								/*
								 * As there follows at least one cell after the
								 * for-loop, the <th can stay opened
								 */

								String arg = line.subSequence(oldPos, newPos)
											 .toString();
								String[] args = getArguments(arg);

								out.append(args[0] + '>');
								out.append(args[1]);
								if (oldHeader)
									out.append(closeTh + '\n');
								else
									out.append(closeTd + '\n');

								if (newHeader)
									out.append(openTh);
								else
									out.append(openTd);

								oldPos = newPos + 2;
								oldHeader = newHeader;

								ca.ol(funcName + "Cell-h with arguments ("
									  + args[0] + ") and content (" + args[1]
									  + ") found in a line (" + line + "). Header: " + newHeader,
									  CALevel.DEBUG);

							} // end while

							String arg = line
										 .subSequence(oldPos, line.length())
										 .toString();
							String[] args = getArguments(arg);

							out.append(args[0] + '>');
							out.append(args[1]);
							if (header)
								out.append(closeTh);
							else
								out.append(closeTd);

						} else {
							out.append("\n" + line);
						}
					} else {
						out.append('\n' + line);
					}
				}

			} // end for

			/*
			 * close table?
			 */

		} catch (IOException e) {
			ca.ol(funcName + "" + e, CALevel.MSG);
			return in;
		}

		/* Statistics */
		Statistics.getInstance().counter.tablesOpened.increase(counterOpen);
		Statistics.getInstance().counter.tablesClosed.increase(counterClose);

		return out;
	}

	/**
	 * @return {arguments, text}
	 * TODO Unit Test!
	 */
	public static String[] getArguments(String s) {
		String[] out, temp;

		if (s.length() > 0) {
			temp = s.split("\\|", 2);
			if (temp.length == 1) {
				out = new String[2];
				out[0] = "";
				out[1] = temp[0];
			} else {
				out = temp;
			}

			out[0] = StringTools.removeChars(new StringBuffer(out[0]), ' ', true, true).toString();

			if (out[0].length() > 0)
				out[0] = ' ' + out[0];
			out[1] = StringTools.removeChars(new StringBuffer(out[1]), ' ', true, true).toString();

		} else {
			out = new String[] {"", ""};
		}

		return out;
	}

}
