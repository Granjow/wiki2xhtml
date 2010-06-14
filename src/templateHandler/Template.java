package src.templateHandler;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

import src.Container_Resources;
import src.Globals;
import src.XHTML;
import src.argumentHandler.ArgumentReaderObject;
import src.argumentHandler.ArgumentItem;
import src.project.WikiProject;
import src.project.WikiProject.FallbackFile;
import src.resources.RegExpressions;

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
 * Template object.
 * 
 * This class will once be very powerful and handle templates of any kind.
 * (Receives the name of a template and its arguments and returns the template text afterwards)
 */
public class Template {
	
	public final String templateFilename;
	
	private final WikiProject wikiProject;
	private FallbackFile templateSource;
	private StringBuffer template = new StringBuffer();
	private HashMap<String, ArgumentItem> arguments = new HashMap<String, ArgumentItem>();

	public enum WarningType {
		NONE,
		RECURSION,
		MISSING
	}

	@Deprecated
	public Template(String templateFilename) throws FileNotFoundException {
		this(templateFilename, null);
	}
	/**
	 * @param wikiProject Used for locating the template
	 */
	public Template(String templateFilename, WikiProject wikiProject) throws FileNotFoundException {
		if (wikiProject == null) {
			wikiProject = new WikiProject(".");
		}
		templateSource = wikiProject.locate(templateFilename);
		this.wikiProject = wikiProject;
		this.templateFilename = templateFilename;
	}

	@SuppressWarnings("unused")
	private static int getCorrespondingBrackets(String s) {
		int pos = 0;
		int openBrackets = 0;

		for (char c : s.toCharArray()) {
			pos++;
			if (c == '{')
				openBrackets++;
			if (c == '}')
				openBrackets--;

			if (openBrackets == 0)
				return pos;
		}
		return -1;
	}

	/**
	 * <p>Reads the template file and inserts the parameters.</p>
	 * 
	 * <h2>Parameter handling:</h2>
	 * <p><code>{{{1}}}</code> etc will be replaced if the parameter was set or if
	 * there is an alternative text, otherwise it will be left as is.</p>
	 * 
	 * <p><code>{{{0}}}</code> contains the template name. <code>{{{1}}}</code> and the
	 * following contain the arguments.</p>
	 * 
	 * <p>If a parameter is empty, an alternative can be provided. Example: If <code>{{{1}}}</code>
	 * is empty, it would be inserted as is in the target text. Using <code>{{{1|}}}</code>
	 * will insert the «empty string», and <code>{{{1|hello wiki}}}</code> would insert
	 * <code>hello wiki</code>.</p>
	 * 
	 * @param callingTemplates A list of templates which call this template, for loop detection.
	 * @return The template with the given arguments applied to
	 * @throws FileNotFoundException 
	 */
	public StringBuffer applyTemplate(String args, ArrayList<String> callingTemplates, 
			HashMap<String, String> cdataSections, WarningType warning) throws FileNotFoundException {
		// Try to read the template
		if (!readTemplate()) {
			if (warning == WarningType.RECURSION) {
				templateSource = wikiProject.locate(Container_Resources.srecursionTemplate);
				readTemplate();
			} else {
				templateSource = wikiProject.locate(Container_Resources.smissingTemplate);
				warning = WarningType.MISSING;
				readTemplate();
			}
		}

		/** Contains the arguments given when the template was called;
		 *  the {{{1}}} etc. have to be replaced with them. */
		arguments = src.argumentHandler.ArgumentReader.getArgumentsMap(args);
		/** Contains the arguments of {{{1|alternative}}} tags */
		ArgumentReaderObject aro = new ArgumentReaderObject();
		Matcher m = RegExpressions.tplParameterInTemplate.matcher(template);
		StringBuffer out = new StringBuffer();
		int last = 0;

		/*
		 * Inserting parameters
		 */
		while (m.find()) {
			out.append(template.substring(last, m.start()));
			aro.setArguments(m.group(1));
			if (aro.size() > 0) {
				if (arguments.containsKey(aro.argsList.get(0).fullArg)) {
					out.append(arguments.get(aro.argsList.get(0).fullArg).argument);
				} else {
					if (aro.size() > 1) {
						out.append(aro.argsList.get(1).fullArg);
					}
					else {
						out.append(m.group());
					}
				}
			}
			last = m.end();
		}
		out.append(template.substring(last, template.length()));

		template = out;

		// Test for recursion; insert warning template if recursion exists
		if (callingTemplates != null && callingTemplates.size() > 0 && callingTemplates.contains(templateSource.pathInfo())) {
			String s = java.util.Arrays.toString(callingTemplates.toArray());
			template.append(TemplateManager.applyTemplates(
								new StringBuffer(
									"{{:" +
									Container_Resources.srecursionTemplateName + "|" + templateSource.pathInfo()
									+ "|" + s
									+ "}}"
								)
								, null, cdataSections, WarningType.RECURSION)
						   );
			System.err.println("Recursion in template " + TemplateInfo.getTemplateName(args) + ", aborting!");
		} else {
			// Search for sub-templates in the current template and apply them.
			if (callingTemplates == null) callingTemplates = new ArrayList<String>();
			callingTemplates.add((templateSource == null ? "null" : templateSource.pathInfo()));
			template = TemplateManager.applyTemplates(template, callingTemplates, cdataSections, warning);
		}

		return template;
	}

	/**
	 * Reads a template from a file.
	 * @return Success
	 */
	private boolean readTemplate() {
		try {
			// Load template; Convert to Unix lines if necessary
			template = templateSource.getContent();
			template = XHTML.makeUnixLines(template);
			if (template.charAt(template.length()-1) == '\n' && !Globals.templateWarnings.contains(templateSource.pathInfo())) {
				while (template.charAt(template.length()-1) == '\n') {
					template.replace(template.length()-1, template.length(), "");
				}
				Globals.templateWarnings.add(templateSource.pathInfo());
			}
			return true;
		} catch (IOException e) {
//			Logger.append(s + e); //TODO
			e.printStackTrace();
		} catch (NullPointerException e) {
//			Logger.getInstance().log.append(s + e);
			e.printStackTrace();
		}
		return false;
	}
	

}
