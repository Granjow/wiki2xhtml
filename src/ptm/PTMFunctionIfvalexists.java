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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.ptm.PTM.PTMObjects;


/**
 * <p>Handles an ifvalexists function.</p>
 * <p><code>{{#ifvalexists: val | then | else }}</code></p>
 * <p>Searches for <code>val</code> in the parameters in the current state. If it is set (e.g. 1=val, name=val, ...),
 * then the <code>then</code> expression is used, otherwise the <code>else</code> expression.</p>
 */
public class PTMFunctionIfvalexists extends PTMFunctionNode {
	
	public static final Pattern startPattern = Pattern.compile("^\\{\\{\\s*#ifvalexists\\s*:");
	
	private static final List<PTMObjects> allowedChildnodes;
	
	static {
		allowedChildnodes = new ArrayList<PTMObjects>();
		allowedChildnodes.add(PTMObjects.Argument);
	}
	
	/**
	 * <p>Tries to create a new If function node at the current position in the content.</p>
	 * <p>The steps are about as follows:</p>
	 * <ol>
	 * <li>Check for correct start pattern, also to find out where the content starts (where to look for arguments)</li>
	 * <li>Loop: Try to add as many arguments as possible  
	 * 		<ul><li>until there is no argument left. In this case the if function cannot be created because the if function does not end. Or,</li>
	 * 		<li>until the end of the if function is reached. In this case, make sure that the if function really closes correctly.</li></ul>
	 * For each added object, update this object's end index.
	 * </li>
	 * </ol> 
	 * @param beginIndex At this position the method will try to create a new If function.
	 * @throws ObjectNotApplicableException If start/end sequences are not correct, 
	 * e.g. for <code>{{#if: a|{{#if:b|c}}</code> (closing <code>}}</code> forgotten)
	 */
	public PTMFunctionIfvalexists(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);
		
		Matcher m = startPattern.matcher(PTMObjectFactory.getIndicator(content, beginIndex));
		if (m.find()) {
			endIndex = beginIndex + m.end();
		} else {
			throw new ObjectNotApplicableException("Lost position of the start pattern!");
		}
		
		if (content.charAt(endIndex) == '|') {
			PTMArgumentNode obj = new PTMArgumentNode(content, beginIndex, parent, root, true);
			childTree.add(obj);
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
			throw new ObjectNotApplicableException("End of the expression could not be found.");
		}
		
		try {
			if (PTMFunctionNode.endExpression.equals(content.substring(endIndex, endIndex+PTMFunctionNode.endExpression.length()))) {
				functionEndReached = true;
				endIndex += PTMFunctionNode.endExpression.length();
			}
		} catch (StringIndexOutOfBoundsException e) {}
		if (!functionEndReached) {
			throw new ObjectNotApplicableException("End of the If expression could not be located.");
		}
		
		if (childTree.size() < 2) {
			throw new ObjectNotApplicableException("Not enough arguments for the #ifvalexists function. Usage: {{#ifvalexists: val | then | else }}");
		}

		assert endIndex > this.beginIndex;
		assert childTree.size() > 1;
	}
	
	public String evaluate() {
		String result = "";
		
		String testFor = childTree.get(0).evaluate().trim();
		
		boolean exists = false;
		for (String s : root.sigma.keySet()) {
			if (testFor.equals(root.sigma.resolve(s))) {
				exists = true;
				break;
			}
		}
		
		if (exists) {
			result = childTree.get(1).evaluate();
		} else if (childTree.size() > 2) {
			result = childTree.get(2).evaluate();
		}
		
		return result;
	}
}
