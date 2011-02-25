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

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Container_Resources;
import src.Constants.Template_References;
import src.Statistics;
import src.project.FallbackFile;
import src.project.file.WikiFile;
import src.ptm.PTMObject.RecursionException;
import src.ptm.PTMRootNode;
import src.ptm.PTMState;
import src.tasks.Tasks.Task;

/**
 * <p>Creates references in a text.</p>
 * <p>References may be given with <code>&lt;ref&gt;content&lt;/ref&gt;</code>. To refer to a single reference multiple times,
 * use <code>&lt;ref name="aUniqueName"&gt;Reference notes&lt;/ref&gt;</code> and later <code>&lt;ref name="aUniqueName"&gt;This text
 * will be ignored&lt;/ref&gt;.</p>
 * <p>The tag &lt;references/&gt; (if available) will be replaced by the reference notes.</p>
 */
public class WikiReferences extends WikiTask {

	public WikiTask nextTask() {
		return new PageTemplate();
	}

	public Task desc() {
		return Task.References;
	}

	
	public void parse(WikiFile file) {
		
		String s = "<references />";
		if (file.getContent().indexOf(s) < 0) {
			s = "<references/>";
		}
		
		if (file.getContent().indexOf(s) >= 0) {
			
			makeReferences(file);
			
			String refs = generateReferenceNotesList(file, file.references);
			
			int pos = file.getContent().indexOf(s);
			file.getContent().replace(pos, pos + s.length(), refs.toString());
			
		}
	}

	/**
	 * Removes <ref> entries from the file and extracts them to {@link WikiFile#references}.
	 */
	public void makeReferences(WikiFile file) {
		short counter = 0;
		
		HashMap<String, PTMState> references = new HashMap<String, PTMState>();
		StringBuffer content = file.getContent();
		

		if (content.length() == 0)
			;
		else {
			short refNr = 0;
			String name;
			String linkEntry;
			
			StringBuffer template = null;
			try {
				template = file.project.locate(Container_Resources.sTplCiteRef).getContent();
			} catch (Exception e) {
				template = new StringBuffer(e.getMessage());
			}
			assert template != null;
			assert template.length() > 0;
			
			
			PTMState sigma = new PTMState();
			
			// (?s) makes the dot match also new lines
			// Group 1: name="refName"
			// Group 2: refName
			// Group 3: reference text
			Matcher m = Pattern.compile("(?s)<ref(\\s+name=\"(\\w+)\")?>(.*?)</ref>").matcher(content.toString());
			StringBuffer out = new StringBuffer();
			int last = 0, first;
			if (m.find()) {
				do {
					counter++;

					first = m.start();
					out.append(content.subSequence(last, first));
					last = m.end();
					
					name = m.group(2);
					if (name == null) {
						name = "cite_" + (refNr+1);
					}
					
					if (!references.containsKey(name)) {
						refNr++;
						sigma = new PTMState()
							.b(Template_References.text, m.group(3))
							.b(Template_References.number, Integer.toString(refNr))
							.b(Template_References.refID, name + "-ref")
							.b(Template_References.noteID, name + "-note");
						references.put(name, sigma);
						System.out.println("Reference added: " + name);
						
					} else {
						sigma = references.get(name);
						System.out.println("Reference already available for " + name);
					}
					assert sigma != null;
					
					try {
						linkEntry = new PTMRootNode(template, sigma).evaluate();
					} catch (RecursionException e) {
						e.printStackTrace();
						linkEntry = e.getMessage();
					}
					out.append(linkEntry);
				} while (m.find());
				out.append(content.subSequence(last, content.length()));
				content = new StringBuffer(out);
				file.setContent(out);
				
			}
		}

		/* Statistics */
		Statistics.getInstance().counter.references.increase(counter);
		
		file.references = references;
	}

	/**
	 * Builds a list of all reference notes.
	 */
	public final String generateReferenceNotesList(final WikiFile file, final HashMap<String, PTMState> references) {
		StringBuffer out = new StringBuffer();
		StringBuffer template = null;
		
		try {
			template = new FallbackFile(Container_Resources.sTplCiteNote, file.project).getContent();
		} catch (Exception e) {
			e.printStackTrace();
			template = new StringBuffer(e.getMessage());
		}
		

		String result;
		
		if (template.length() > 0) {
			if (references.size() > 0) {
				
				// The TreeSet sorts the references by their number
				TreeSet<PTMState> sigmaSet = new TreeSet<PTMState>(new PTMStateComparator());
				sigmaSet.addAll(references.values());
				
				for (PTMState sigma : sigmaSet) {
					
					try {
						out.append(new PTMRootNode(template, sigma).evaluate());
					} catch (RecursionException e) {
						e.printStackTrace();
						out.append(e.getMessage());
					}
				}
			}
			
			PTMState sigma = new PTMState()
				.b(Template_References.text, out.toString())
				.b(Template_References.container, "true");
			
			try {
				result = new PTMRootNode(template, sigma).evaluate();
			} catch (RecursionException e) {
				e.printStackTrace();
				result = e.getMessage();
			}
		} else {
			result = "";
		}
		
		return result;
	}
	
	private static final class PTMStateComparator implements Comparator<PTMState> {

		public int compare(PTMState o1, PTMState o2) {
			int diff;
			try {
				diff = Integer.parseInt(o1.resolve(Template_References.number)) - Integer.parseInt(o2.resolve(Template_References.number));
			} catch (NumberFormatException e) {
				diff = 0;
			}
			return diff;
		}
		
	}
	
}
