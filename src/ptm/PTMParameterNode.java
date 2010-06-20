package src.ptm;

import java.util.ArrayList;
import java.util.List;


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
 * <p>This class handles Parameter objects.</p>
 * <p><code>{{{ name | alternative }}}</code></p>
 * <p>If <code>name</code> is bound to a value, then this value will be inserted 
 * at the place of the parameter. If it is not bound, then the alternative text
 * will be used instead.</p>
 * <p><code>{{{ name }}}</code></p>
 * <p>If no alternative is given and the name is not bound then the parameter will 
 * just be left as is.</p>
 */
public class PTMParameterNode extends PTMNode {

	public static final String identifier = "{{{";
	private static final String endExpression = "}}}";
	private static final List<PTMObjects> allowedChildnodes;
	private static final AbortFunction abort;
	
	static {
		allowedChildnodes = new ArrayList<PTMObjects>();
		allowedChildnodes.add(PTMObjects.Argument);
		ArrayList<String> al = new ArrayList<String>();
		al.add(endExpression);
		abort = PTM.createAbortFunction(al);
	}

	/**
	 * @see {@link PTMFunctionIf#PTMFunctionIf(StringBuffer, int, PTMObject)}
	 */
	public PTMParameterNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);
		
		// Make sure the first characters are correct
		try {
			if (!identifier.equals(content.substring(beginIndex, beginIndex+identifier.length()))) {
				throw new ObjectNotApplicableException("Wrong start sequence for a parameter.");
			}
		} catch (StringIndexOutOfBoundsException e) {
			throw new ObjectNotApplicableException("Wrong start sequence for a parameter, and end of file too early");
		}

		// Add all available parameter arguments
		endIndex = beginIndex + identifier.length();
		boolean parameterEndReached = false;
		PTMObject obj;
		do {
			try {
				obj = PTMObjectFactory.buildObject(content, endIndex, this, root, abort, allowedChildnodes);
				if (obj != null) {
					childTree.add(obj);
					endIndex = obj.endIndex;
				}
			} catch (EndOfExpressionReachedException e) {
				parameterEndReached = true;
				break;
			}
		} while (obj != null);
		if (!parameterEndReached) {
			throw new ObjectNotApplicableException("Parameter does not close.");
		}
		
		// Make sure the parameter ends correctly, and update the end index
		parameterEndReached = false;
		try {
			if (endExpression.equals(content.substring(endIndex, endIndex+endExpression.length()))) {
				parameterEndReached = true;
				endIndex += endExpression.length();
			}
		} catch (StringIndexOutOfBoundsException e) {}
		if (!parameterEndReached) {
			throw new ObjectNotApplicableException("Parameter does not close.");
		}
		
		assert endIndex > this.beginIndex;
	}

	@Override
	public String evaluate() {
		if (childTree.size() > 0) {
			
		}
		// TODO Auto-generated method stub
		return getRawContent();
	}

}
