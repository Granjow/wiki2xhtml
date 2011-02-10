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
 * <p>Handles a switch function.</p>
 * <p><code>{{#switch: var | op1=arg1 | op2=arg2 | ... }}</code></p>
 * <p>If var is equal to op1, then the function is replaced by arg1, etc.</p>
 */
public class PTMFunctionSwitch extends PTMFunctionNode {
	
	public static final Pattern startPattern = Pattern.compile("^\\{\\{\\s*#switch\\s*:");
	
	private static final String defaultKeyword = "#default";
	private static final List<PTMObjects> allowedChildnodes;
	
	static {
		allowedChildnodes = new ArrayList<PTMObjects>();
		allowedChildnodes.add(PTMObjects.Argument);
	}
	
	public PTMFunctionSwitch(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) throws ObjectNotApplicableException {
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
			throw new ObjectNotApplicableException("Too few arguments for #switch. Usage: {{#switch: var | op1=val1 | op2=val2 | ... }}");
		}

		assert endIndex > this.beginIndex;
		assert childTree.size() >= 2;
	}
	
	public String evaluate() throws RecursionException {
		String result = "";
		String defaultValue = "";
		String currentName;
		
		boolean found = false;
		
		this.printTree(System.out, " ");
		
		String compare = childTree.get(0).evaluate().trim();
		search: for (int i = 1; i < childTree.size(); i++) {
			currentName = childTree.get(i).childTree.get(0).evaluate().trim();
			if (currentName.equals(defaultKeyword)) {
				defaultValue = childTree.get(i).childTree.get(1).evaluate();
			}
			if (currentName.equals(compare)) {
				result = childTree.get(i).childTree.get(1).evaluate();
				found = true;
				break search;
			}
		}
		
		if (!found) {
			result = defaultValue;
		}
		
		return result;
	}
}
