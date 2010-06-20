package src.ptm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.ptm.PTM.PTMObjects;

/*
 *   Copyright (C) 2010 Simon Eugster <granjow@users.sf.net>

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
	public PTMFunctionIf(StringBuffer content, int beginIndex, PTMObject parent) throws ObjectNotApplicableException {
		super(content, beginIndex, parent);
		
		// Find the end of the starting {{#if: expression (may contain spaces) to set the endIndex appropriately.
		// Then try to add more objects until the end of the function is reached. 
		
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
				obj = PTMObjectFactory.buildObject(content, endIndex, this, PTMFunctionNode.abort, allowedChildnodes);
				if (obj != null) {
					childTree.add(obj);
					endIndex = obj.endIndex;
//					System.out.printf("If: Object %s added. Goes from %d to %d with content >>%s<<.\n", obj, obj.beginIndex, obj.endIndex, obj.getRawContent());
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
			throw new ObjectNotApplicableException("End of the expression could not be found.");
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
			throw new ObjectNotApplicableException("End of the If expression could not be located.");
		}

		assert endIndex > this.beginIndex;
	}
	
	
	public static void main(String[] args) throws ObjectNotApplicableException {
		StringBuffer sb = new StringBuffer("Hallo {{#if: .");
		PTMFunctionIf f;
		for (int i = 0; i <= sb.length()+1; i++) {
			try {
				f = new PTMFunctionIf(sb, i, null);
				System.out.printf("Applies at %d! --->%s<---\n", i, f.getRawContent());
			} catch (ObjectNotApplicableException e) {}
		}
	}
}
