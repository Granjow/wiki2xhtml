/*
 *   Copyright (C) 2011 Simon Eugster <granjow@users.sf.net>

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

package src.ptm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.ptm.PTM.PTMObjects;


/**
 * <p>Displays debug information.</p>
 * <p><code>{{#debug}}</code></p>
 * <p>Without an argument given, this function lists all available arguments. Only one can be used at the same time.</p>
 */
public class PTMFunctionDebug extends PTMFunctionNode {
	
	public static final Pattern startPattern = Pattern.compile("^\\{\\{\\s*#debug\\s*:?");
	
	private static final List<PTMObjects> allowedChildnodes;
	
	private static final String tdStyle = " style=\"border-bottom: 1px solid #999999; vertical-align: top;\"";
	private static final String tableStyle = " style=\"border: 2px solid #666; margin: 1em; padding: 2px;\"";
	
	static {
		allowedChildnodes = new ArrayList<PTMObjects>();
		allowedChildnodes.add(PTMObjects.Argument);
	}
	
	/**
	 * <p>Inserts debug information at this position.</p>
	 * @param beginIndex At this position the method will try to insert the debug information.
	 */
	public PTMFunctionDebug(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);
		
		Matcher m = startPattern.matcher(PTMObjectFactory.getIndicator(content, beginIndex));
		if (m.find()) {
			endIndex = beginIndex + m.end();
		} else {
			throw new ObjectNotApplicableException("Lost position of the start pattern!");
		}
		
		boolean functionEndReached = false;
		PTMObject obj; 
		do {
			try {
				obj = PTMObjectFactory.buildObject(content, endIndex, this, root, PTMFunctionNode.abort, allowedChildnodes);
				if (obj != null) {
					childTree.add(obj);
					endIndex = obj.endIndex;
				}
			} catch (EndOfExpressionReachedException e) {
				functionEndReached = true;
				break;
			}
		} while (obj != null);
		
		if (!functionEndReached) {
			throw new ObjectNotApplicableException("End of the #debug expression could not be found.");
		}
		
		try {
			if (PTMFunctionNode.endExpression.equals(content.substring(endIndex, endIndex+PTMFunctionNode.endExpression.length()))) {
				functionEndReached = true;
				endIndex += PTMFunctionNode.endExpression.length();
			}
		} catch (StringIndexOutOfBoundsException e) {}
		if (!functionEndReached) {
			throw new ObjectNotApplicableException("End of the #debug expression could not be located.");
		}

		assert endIndex > this.beginIndex;
	}
	
	public String evaluate() throws RecursionException {
		StringBuffer result = new StringBuffer();
		
		result.append("<div class=\"debug\"><strong>Debug information</strong><br/>");
		
		if (childTree.size() == 0) {
			result.append("Supported is <em>list</em> for displaying all arguments.");
		} else {
			if ("list".equals(childTree.get(0).evaluate().trim())) {
				result.append("<p>Argument <em>list</em> given, showing the current state σ. The value bound to <em>name</em> " +
						"can be inserted with <code>{{{name}}}</code>.</p>");
				result.append("<table class=\"debug\"" + tableStyle + "><tr>" +
						"<th" + tdStyle + ">Name</th><th" + tdStyle + ">Value</th></tr>");
				for (Entry<String, PTMArgumentValueNode> e : root.sigma.entrySet()) {
					result.append(String.format("<tr><td%s>%s</td><td%s>%s</td></tr>", tdStyle, e.getKey(), tdStyle, e.getValue().getRawContent()));
				}
				result.append("</table>");
			} else {
				result.append(String.format("%s is not a supported argument. " +
						"See {{#debug}} without arguments for a list of supported arguments.", childTree.get(0).evaluate().trim()));
			}
		}
		result.append("</div>");
		
		return result.toString();
	}
}
