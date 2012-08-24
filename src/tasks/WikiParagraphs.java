/*
 *   Copyright (C) 2010-2011 Simon A. Eugster <simon.eu@gmail.com>

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

package src.tasks;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Resources;
import src.Statistics;
import src.project.file.WikiFile;
import src.tasks.Tasks.Task;
import src.utilities.Placeholder;

public class WikiParagraphs extends WikiTask {

	public WikiTask nextTask() {
		return new WikiInsertNowikiContent();
	}

	public Task desc() {
		return Task.Paragraphs;
	}


	public static final PrintStream o = System.out;

	public void parse(WikiFile file) {
		

		StringBuffer out = file.getContent();
		
		ArrayList<Placeholder> phs = new ArrayList<Placeholder>();

		Statistics.getInstance().sw.timeInsertingParagraphs.continueTime();

		/* Temporarily remove blocks which may not contain a paragraph */
		for (String remove : Resources.tagsBlockNoParagraphsInside()) {

//			Placeholder p = new Placeholder("<" + remove + "[^>]*>", "</" + remove + ">");	// Not working! Matches <p*> and therefore also <param> (not desired).
			// Either <p> or <p\s[^>]*>.
			Placeholder p = new Placeholder("<" + remove + "(?:\\s[^>]*>|>)", "</" + remove + ">");
			out = p.removeContent(out);
			phs.add(p);
		}
		/* Temporarily replace references tag and TOC */
//		Matcher m = Pattern.compile("<references />").matcher(out);
//		out = new StringBuffer(m.replaceAll("<references></references>"));
//		Matcher m2 = Pattern.compile("\\{\\{TOC\\}\\}").matcher(out);
//		out = new StringBuffer(m2.replaceAll("<toc></toc>"));

		/* Create block pattern (no paragraphs allowed there) */
		StringBuffer blocks = new StringBuffer();
		for (String s : Resources.tagsBlockAll()) {
			blocks.append(s + "|");
		}
		
		// Special case for <!-- comments -->.
		Pattern pBlocksStart = Pattern.compile("(?s)^<(?:(?:" + blocks.substring(0, blocks.length() - 1) + ")[\\s>]|!--)");
		Pattern pBlocksEnd = Pattern.compile("(?s)^(?:</(?:" + blocks.substring(0, blocks.length() - 1) + ")[\\s>]|-->)");
		Matcher mBlocksStart;
		Matcher mBlocksEnd;
		
		Pattern pExceptions = Pattern.compile("(?s)^(?:<br[^>]*/?>|<references\\s*/>)");
		Matcher mExceptions;

		boolean open = false;
		boolean block = false;
		int pos = -1;
		int last = 0;
		StringBuffer in = out;
		out = new StringBuffer();

		/* Insert paragraphs */
		while (pos < in.length()) {
			if (open) {
				while (true) {
					pos++;
					if (pos >= in.length()) {
						out.append(in.substring(last, pos));
						out.append("</p>");
						last = pos;
						open = false;
						break;
					}
					if (in.charAt(pos) == '<') {
						mBlocksStart = pBlocksStart.matcher(in.substring(pos, ( pos + 20 > in.length()? in.length() : pos+20 ) ));
						if (mBlocksStart.find()) {
							if (pos > 0 && in.charAt(pos-1) == '\n') {
								// Preserver newline order. mbt #6
								pos--;
							}
							out.append(in.substring(last, pos));
							out.append("</p>");
							last = pos;
							open = false;
							block = true;
						}
						break;
					}
					if (pos < (in.length()-1) && in.charAt(pos) == '\n' && in.charAt(pos+1) == '\n') {
						out.append(in.substring(last, pos));
						out.append("</p>");
						last = pos;
						open = false;
						break;
					}
				}
			} else {	// !open
				pos++;

				if (!block) {	// !block, !open
					while (pos < in.length() && (in.charAt(pos) == ' ' || in.charAt(pos) == '\n')) {
						pos++;
					}
					if (pos < in.length()) {
						mBlocksStart = pBlocksStart.matcher(in.substring(pos, ( pos + 20 > in.length()? in.length() : pos+20 ) ));
						if (mBlocksStart.find()) {
							block = true;
						} else {
							mExceptions = pExceptions.matcher(in.substring(pos, ( pos + 512 > in.length()? in.length() : pos+512 )));
							if (mExceptions.find()) { // <br/> etc.
								pos += mExceptions.group().length();
							} else {
								out.append(in.substring(last, pos));
								out.append("<p>");
								last = pos;
								open = true;
							}
						}
					}

				} else {	// block, !open
					do {
						mBlocksEnd = pBlocksEnd.matcher(in.substring(pos, ( pos + 20 > in.length()? in.length() : pos+20 ) ));

						if (mBlocksEnd.find()) {
							block = false;
							pos += mBlocksEnd.group().length();
							break;
						}
						pos++;
					} while (block = true && pos < in.length());
					while (pos < in.length() && (in.charAt(pos) == ' ' || in.charAt(pos) == '\n')) {
						pos++;
					}
					if (pos < in.length()) {
						mBlocksStart = pBlocksStart.matcher(in.substring(pos, ( pos + 20 > in.length()? in.length() : pos+20 ) ));
						if (mBlocksStart.find()) {
							block = true;
						} else {
							mExceptions = pExceptions.matcher(in.substring(pos, ( pos + 512 > in.length()? in.length() : pos+512 )));
							if (mExceptions.find()) { // <br/> etc.
								pos += mExceptions.group().length();
							} else {
								out.append(in.substring(last, pos));
								out.append("<p>");
								last = pos;
								open = true;
							}
						}
					}
				}
			}
		}
		out.append(in.substring(last, in.length()));

		/* Insert original reference tags and TOC again */
//		m2 = Pattern.compile("<toc></toc>").matcher(out);
//		out = new StringBuffer(m2.replaceAll("{{TOC}}"));
//		m = Pattern.compile("<references></references>").matcher(out);
//		out = new StringBuffer(m.replaceAll("<references />"));

		/* Insert the removed blocks again
		 * Reverse insert! (otherwise: problems with nested elements */
		for (int i = phs.size() - 1; i >= 0; i--) {
			out = phs.get(i).insertContent(out);
		}

		file.setContent(out);
		
		Statistics.getInstance().sw.timeInsertingParagraphs.stop();
	}

}
