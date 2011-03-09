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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Constants.Template_TOC;
import src.Constants;
import src.Statistics;
import src.project.FallbackFile;
import src.project.file.WikiFile;
import src.ptm.PTMObject.RecursionException;
import src.ptm.PTMRootNode;
import src.ptm.PTMState;
import src.tasks.Tasks.Task;

/** 
 * Builds the Table of Contents from all wiki headings found in the file. The TOC is now
 * inserted like any ordinary template using {@code {{:tplTOC.txt}}}, with the parameters
 * listed in {@link Template_TOC} added to the ones that are (optionally) already passed
 * to the template.
 */
public class WikiTOC extends WikiTask {
	
	private static final Pattern pHeading = Pattern.compile("^(=={1,5})(.*[^=])\\1(?:[^=]|$)");
	private static final Pattern pTOC = Pattern.compile("(?m)^\\{\\{:tplTOC.txt((?:\\|.*)?)\\}\\}");
	
	public Task desc() {
		return Task.TOC;
	}
	public WikiTask nextTask() {
		return new WikiParserFunctions();
	}

	/**
	 * Creates a TOC from an input file. The <hx> have to be at the beginning of
	 * the line!
	 */	
	public void parse(WikiFile file) {
		
		Matcher mTOC = pTOC.matcher(file.getContent());
		if (mTOC.find()) {
			mTOC.reset();
			
			StringBuffer in = file.getContent();
			StringBuffer out = new StringBuffer();
			int last = 0;
			
			while (mTOC.find()) {
				out.append(in.substring(last, mTOC.start()));
				
				

				
				ArrayList<String> headingIDs = new ArrayList<String>();
				StringBuffer toc = new StringBuffer();
				String completeToc = null;
				String id;

				BufferedReader b = new BufferedReader(new StringReader(file.getContent().toString()));
				byte counter = 0;
				int level = 0;
				
				StringBuffer tplTOC = null;
				try {
					FallbackFile ff = file.project.locate(Constants.Templates.sTplTOC);
					tplTOC = ff.getContent();
				} catch (Exception e) {
					tplTOC = new StringBuffer(e.getMessage());
				}
				assert tplTOC != null;
				assert tplTOC.length() > 0;
				
				PTMState sigma;

				try {
					Matcher m;
					String ol = "#####", ul = "*****";
					for (String line = b.readLine(); line != null; line = b.readLine()) {
						m = pHeading.matcher(line);
						if (m.find()) {
							level = m.group(1).length();
							id = WikiHeadings.getIDtoHeading(m.group(2).trim(), level, headingIDs);
							
							
							sigma = new PTMState()
								.b(Template_TOC.level, Integer.toString(level-1))
								.b(Template_TOC.text, m.group(2).trim())
								.b(Template_TOC.ol, ol.substring(ol.length() - (level-1)))
								.b(Template_TOC.ul, ul.substring(ul.length() - (level-1)))
								.b(Template_TOC.id, id)
								;
							
							if (mTOC.group(1) != null) {
								sigma.bindValuesFromList(mTOC.group(1).split("\\|"));
							}
							
							try {
								toc.append(new PTMRootNode(tplTOC, sigma).evaluate());
							} catch (RecursionException e) {
								e.printStackTrace();
								toc.append(e.getMessage());
							}

							counter++;
						}
					}
					if (counter > 0) {
						completeToc = null;
						sigma = new PTMState()
							.b(Template_TOC.isBlock, "true")
							.b(Template_TOC.text, toc.toString())
							;
						try {
							completeToc = new PTMRootNode(tplTOC, sigma).evaluate();
						} catch (RecursionException e) {
							completeToc = e.getMessage();
						}
						assert completeToc != null;
						
						out.append(completeToc);
					}

				} catch (IOException e)  {
					e.printStackTrace();
				}

				/* Statistics */
				Statistics.getInstance().counter.TOC.increase(counter);
				
				
				
				last = mTOC.end();
			}
			out.append(in.substring(last));
			file.setContent(out);
		}
	}

}
