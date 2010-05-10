package src.templateHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

import src.Resources;
import src.Statistics;
import src.templateHandler.Template.WarningType;
import src.utilities.IORead;

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
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * Template applier.
 *
 * @author Simon Eugster
 */
public class TemplateManager {

	private static HashMap<String, String> cdataSections = new HashMap<String, String>();

	/**
	 * Applies templates. Templates can be called the following way: <br />
	 * {{:templateName|arg1|arg2|&lt;![CDATA[arg|3]]&gt;}}
	 * @param input
	 * @return
	 */
	public static StringBuffer applyTemplates(StringBuffer input, ArrayList<String> callingTemplates, WarningType warning) {
		StringBuffer out = new StringBuffer();
		Template tempTemplate = new Template();

		Statistics.getInstance().sw.timeApplyingTemplates.continueTime();

		input = removeCdataSections(input);

		Matcher m = Resources.Regex.template.matcher(input);
		int end = 0;
		String tplAllArgs;

		while (m.find()) {
			Statistics.getInstance().counter.templates.increase();
			out.append(input.substring(end, m.start()));

			if (m.group(1).charAt(0) != '|')
				tplAllArgs = '|' + m.group(1);
			else
				tplAllArgs = m.group(1);

			tempTemplate = new Template(TemplateInfo.getTemplateName(tplAllArgs));
			out.append(tempTemplate.applyTemplate(tplAllArgs, callingTemplates, warning));

			end = m.end();
		}
		out.append(input.substring(end, input.length()));

		out = insertCdataSections(out);

		Statistics.getInstance().sw.timeApplyingTemplates.stop();

		return out;
	}

	/**
	 * Removes the content of CDATA sections
	 * @param input
	 * @return input with removed CDATA content
	 */
	private static StringBuffer removeCdataSections(StringBuffer input) {
		StringBuffer out = new StringBuffer();
		Matcher m = Resources.Regex.cdata.matcher(input);

		int last = 0;
		while (m.find()) {
			out.append(input.substring(last, m.start()));
			
			if (cdataSections.containsKey(m.group(1))) {
				// Has already been replaced, do nothing
				out.append(m.group());
			} else {
				String hash = randHash(m.group(1));
				cdataSections.put(hash, m.group(1));
				// Replace the CDATA content with a hash of the content
				out.append("<![CDATA[" + hash + "]]>");
			}

			last = m.end();
		}
		out.append(input.substring(last, input.length()));

		return out;
	}

	/**
	 * Re-inserts remaining CDATA sections with original content
	 * @param input
	 * @return input with replaced CDATA sections
	 */
	private static StringBuffer insertCdataSections(StringBuffer input) {
		StringBuffer out = new StringBuffer();
		Matcher m = Resources.Regex.cdata.matcher(input);

		int last = 0;
		while (m.find()) {
			out.append(input.substring(last, m.start()));

			if (cdataSections.containsKey(m.group(1))) {
				out.append(cdataSections.get(m.group(1)));
			} else {
				out.append(m.group());
			}

			last = m.end();
		}
		out.append(input.substring(last, input.length()));

		return out;
	}
	
	private static String randHash(String input) {
		// If fails in future: Add recursion level of template in the hash
		StringBuilder hash = new StringBuilder();
		hash.append(input.hashCode());
		hash.append((int) (java.lang.Math.random() * 100000));
		return hash.toString();
	}

	private static StringBuffer testCdataRemover(StringBuffer input) {
		input = removeCdataSections(input);
		input = insertCdataSections(input);
		return input;
	}

	public static void main(String[] args) throws IOException {
		StringBuffer sb = IORead.readSBuffer(new java.io.File("test"));
		System.out.println(testCdataRemover(sb));
	}

}
