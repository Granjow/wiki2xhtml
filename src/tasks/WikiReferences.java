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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Statistics;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.tasks.Tasks.Task;
import src.utilities.StringTools;

/**
 * Creates references in a text. References may be given with <ref>content</ref>
 * and inserted with <references />.
 *
 * @author Simon Eugster
 * ,		hb9eia
 *
 * TODO 5 Footnotes
 */
public class WikiReferences extends WikiTask {

	public WikiTask nextTask() {
		return new WikiCleanup();
	}

	public Task desc() {
		return Task.References;
	}

	public ArrayList<StringBuffer> makeReferences(WikiFile file) {
		short counter = 0;
		
		ArrayList<StringBuffer> references = new ArrayList<StringBuffer>();
		StringBuffer content = file.getContent();
		

		if (content.length() == 0)
			;
		else {
			short refNr = 0;
			// (?s) makes the dot match also new lines
			Matcher m = Pattern.compile("(?s)<ref>(.*?)</ref>").matcher(content.toString());
			StringBuffer out = new StringBuffer();
			int last = 0, first;
			if (m.find()) {
				do {
					refNr++;
					counter++;

					first = m.start();
					out.append(content.subSequence(last, first));
					last = m.end();
					out = StringTools.removeChars(out, ' ', false, true);
					references.add(new StringBuffer(m.group(1)));
					out
					.append("<sup><a href=\"#refnote_" + refNr +
							"\" style=\"border-bottom: none; text-decoration: none;\"" +
							" id=\"refmark_" + refNr + "\"" +
							" class=\"refmark\"" +
							">[" + refNr + "]</a></sup>");
				} while (m.find());
				out.append(content.subSequence(last, content.length()));
				content = new StringBuffer(out);
				file.setContent(out);
			}
		}

		/* Statistics */
		Statistics.getInstance().counter.references.increase(counter);
		
		return references;
	}

	public StringBuffer generateReferencesList(ArrayList<StringBuffer> references) {
		StringBuffer out = new StringBuffer();
		if (references.size() > 0) {
			short i = 0;
			for (StringBuffer s : references) {
				i++;
				
				// TODO 0 Use a template
				out.append("\n#" +
									  (i == 1 ? " ((class=\"references\"))" : "") +
									  " class=\"reference\" id=\"refnote_" + i + "\" | <a href=\"#refmark_" + i +
									  "\">[↑]</a> " + s);
			}

			VirtualWikiFile vf = new VirtualWikiFile(null, "", false, true, out);
			vf.addTask(Task.Lists);
			vf.parse();
			out = vf.getContent();
		}
		return out;
	}

	public void parse(WikiFile file) {
		int pos;
		String s = "<references />";
		if (file.getContent().indexOf(s) < 0) {
			s = "<references/>";
		}
		if ((pos = file.getContent().indexOf(s)) >= 0) {
			ArrayList<StringBuffer> references = makeReferences(file);
			StringBuffer refs = generateReferencesList(references);
			file.getContent().replace(pos, pos + s.length(), refs.toString());
		}
	}
	
}
