/*
 *   Copyright (C) 2010-2011 Simon Eugster <granjow@users.sf.net>

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
 * <p>Handles an if function.</p>
 * <p><code>{{#if: det | arg1 | arg2 }}</code></p>
 * <p>If det is empty or consists of blanks (whitespace, tab, etc.) only 
 * then arg2 is inserted at the place of the function, and arg1 otherwise.</p>
 */
public class PTMFunctionIf extends PTMFunctionNode {
	
	public static final Pattern startPattern = Pattern.compile("^\\{\\{\\s*#if\\s*:");
	
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
	public PTMFunctionIf(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);
		
		// Find the end of the starting {{#if: expression (may contain spaces) to set the endIndex appropriately.
		// Then try to add more objects until the end of the function is reached. 
		
		Matcher m = startPattern.matcher(PTMObjectFactory.getIndicator(content, beginIndex));
		if (m.find()) {
			endIndex = beginIndex + m.end();
		} else {
			throw new ObjectNotApplicableException("Lost position of the start pattern!");
		}
		
		if (content.charAt(endIndex) == '|') {
			// In the case that there is no space after the #if: and a pipe symbol directly follows,
			// the string to test is skipped since the ArgumentNode reader always needs to 
			// advance one character to avaoid an infinite loop. To get the first argument back, add it here.
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
				// While parsing we reached the end of the if function.
				// This is a «good» exception as it tells us that the function can indeed end correctly.
				// It should _not_ be thrown when reaching EOF. Check nevertheless below, just to make sure.
				functionEndReached = true;
				break;
			}
		} while (obj != null);
		
		if (!functionEndReached) {
			throw new ObjectNotApplicableException("End of the #if expression could not be found.");
		}
		
		// Check that we're not at the EOF but really at the end of a function
		// by looking for the terminating }}.
		try {
			if (PTMFunctionNode.endExpression.equals(content.substring(endIndex, endIndex+PTMFunctionNode.endExpression.length()))) {
				functionEndReached = true;
				endIndex += PTMFunctionNode.endExpression.length();
			}
		} catch (StringIndexOutOfBoundsException e) {}
		if (!functionEndReached) {
			throw new ObjectNotApplicableException("End of the #if expression could not be located.");
		}
		
		if (childTree.size() < 2) {
			throw new ObjectNotApplicableException("Not enough arguments for the #if function. Usage: {{#if: teststring | notEmpty | empty }}");
		}

		assert endIndex > this.beginIndex;
	}
	
	public String evaluate() throws RecursionException {
		String result = "";
		
		if (childTree.size() >= 2) {
			if (childTree.get(0).evaluate().trim().length() > 0) {
				result = childTree.get(1).evaluate();
			} else {
				if (childTree.size() >= 3) {
					result = childTree.get(2).evaluate();
				}
			}
		}
		
		return result;
	}
}
