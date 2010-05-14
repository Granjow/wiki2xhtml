package src.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import src.Statistics;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.project.file.WikiFile;
import src.tasks.Tasks.Task;
import src.utilities.StringTools;
import src.utilities.Tuple;


public class WikiTables extends WikiTask {
	
	private static CommentAtor ca = CommentAtor.getInstance();
	
	public Task desc() {
		return Task.Tables;
	}
	public WikiTask nextTask() {
		return new WikiLinks();
	}
	public void parse(WikiFile file) {
		final String funcName = "Tables.makeTables: ";
		StringBuffer in = file.getContent();

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
							ca.ol(funcName + "Table closed at line " + lnr, CALevel.DEBUG);
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

							ArgTuple args = getArguments(s);

							out.append("\n" + openCap);
							if (args.k().length() > 0)
								out.append(" " + args.k());
							out.append(">" + args.v() + closeCap);

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
								ArgTuple args = getArguments(arg);

								out.append(args.k() + '>');
								out.append(args.v());
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
									  + args.k() + ") and content (" + args.v()
									  + ") found in a line (" + line + "). Header: " + newHeader,
									  CALevel.DEBUG);

							} // end while

							String arg = line
										 .subSequence(oldPos, line.length())
										 .toString();
							ArgTuple args = getArguments(arg);

							out.append(args.k() + '>');
							out.append(args.v());
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
		}

		/* Statistics */
		Statistics.getInstance().counter.tablesOpened.increase(counterOpen);
		Statistics.getInstance().counter.tablesClosed.increase(counterClose);
		
		file.setContent(out);
	}

	/**
	 * @return {arguments, text}
	 */
	public static ArgTuple getArguments(String s) {
		ArgTuple tuple = new ArgTuple();

		if (s.length() > 0) {
			String[] temp = s.split("\\|", 2);
			if (temp.length == 1) {
				tuple.setValue(temp[0].trim());
			} else {
				tuple.setKey(temp[0].trim());
				tuple.setValue(temp[1].trim());
			}
		}
		return tuple;
	}
	
	public static class ArgTuple extends Tuple<String, String> {
		public ArgTuple() {
			super();
			key = "";
			val = "";
		}
		public String k() {
			return (key.length() > 0) ? " " + key : key;
		}
	}
}
