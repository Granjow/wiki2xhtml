package src.ptm;

import java.util.ArrayList;

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
 * <p>This class handles templates.</p>
 * <p><code>{{: name | args }}</code></p>
 */
public class PTMTemplateNode extends PTMNode {

	public static final String identifier = "{{:";
	private static final String endExpression = "}}";
	private static final AbortFunction abort;
	private static final ArrayList<PTMObjects> allowedChildnodes;
	
	static {
		ArrayList<String> terminators = new ArrayList<String>();
		terminators.add(endExpression);
		abort = PTM.createAbortFunction(terminators);
		allowedChildnodes = new ArrayList<PTMObjects>();
		allowedChildnodes.add(PTMObjects.Argument);
	}
	
	public PTMTemplateNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);
		
		// Check for correct start sequence
		try {
			if (!identifier.equals(content.substring(beginIndex, beginIndex+identifier.length()))) {
				throw new ObjectNotApplicableException("Wrong start sequence for a template");
			} else {
				endIndex = beginIndex + identifier.length();
			}
		} catch (StringIndexOutOfBoundsException e) {
			throw new ObjectNotApplicableException("Wrong start sequence for a template, and end of file too early");
		}
		
		boolean templateEndReached = false;
		PTMObject obj;
		do {
			try {
				obj = PTMObjectFactory.buildObject(content, endIndex, this, root, abort, allowedChildnodes);
				if (obj != null) {
					childTree.add(obj);
					endIndex = obj.endIndex;
				}
			} catch (EndOfExpressionReachedException e) {
				templateEndReached = true;
				break;
			}
		} while (obj != null);
		if (!templateEndReached) {
			throw new ObjectNotApplicableException("Template does not close.");
		}
		
		templateEndReached = false;
		try {
			if (endExpression.equals(content.substring(endIndex, endIndex+endExpression.length()))) {
				templateEndReached = true;
				endIndex += endExpression.length();
			}
		} catch (StringIndexOutOfBoundsException e) { }
		
		if (!templateEndReached) {
			throw new ObjectNotApplicableException("Template does not close.");
		}
		
		
		// Update the state of the children to the new state defined by this node
		// TODO test
		PTMState childSigma = new PTMState();
		for (PTMObject o : childTree) {
			if (o instanceof PTMNode) {
				((PTMNode) o).sigma = childSigma;
			}
		}
		
		assert endIndex > this.beginIndex;
	}

	@Override
	public String evaluate() {
		// TODO Auto-generated method stub
		return getRawContent();
	}

}
