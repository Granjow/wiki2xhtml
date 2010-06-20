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
 * <p>This class handles arguments.</p>
 * <p><code>arg1 | arg2 | arg3</code></p>
 * <p>The end of an argument is determined by the type of the parent,
 * either when a new argument starts (they are separated by the <code>|</code> character)
 * or when the parent object ends (e.g. with <code>}}</code> for functions).</p>
 * <p>A parent object cannot end within a child node of an argument node.</p>
 */
public class PTMArgumentNode extends PTMNode {
	
	private static final char identifier = '|';
	private static final List<PTMObjects> allowedChildnodes;
	
	static {
		allowedChildnodes = new ArrayList<PTMObjects>();
		for (PTMObjects o : PTMObjects.values()) {
			if (o != PTMObjects.Argument) allowedChildnodes.add(o);
		}
	}
	
	public PTMArgumentNode(StringBuffer content, int beginIndex, PTMObject parent) throws ObjectNotApplicableException {
		super(content, beginIndex, parent);
		
		// Build the abort function. Abort at | always (new argument),
		// and at either }}} (parameter) or }} (template/function).
		// All possible objects that can contain arguments have to be considered here;
		// needs to be updated for new types.
		ArrayList<String> terminators = new ArrayList<String>();
		terminators.add("|");
		if (parent instanceof PTMParameterNode) {
			terminators.add("}}}");
		} else {
			terminators.add("}}");
		}
		AbortFunction abort = PTM.createAbortFunction(terminators);
		
		endIndex = beginIndex;
		if (content.charAt(endIndex) == identifier) { endIndex++; }
		
		boolean endReached = false;
		PTMObject obj;
		while (true) {
			try {
				obj = PTMObjectFactory.buildObject(content, endIndex, this, abort, allowedChildnodes);
				if (obj != null) {
					childTree.add(obj);
					endIndex = obj.endIndex;
//					System.out.printf("Argument: Object %s added. Goes from %d to %d with content >>%s<<.\n", obj, obj.beginIndex, obj.endIndex, obj.getRawContent());
				} else {
					break;
				}
			} catch (EndOfExpressionReachedException e) {
				endReached = true;
				break;
			}
		}
		if (!endReached) {
			throw new ObjectNotApplicableException("End of argument list could not be found");
		}

		assert endIndex > this.beginIndex;
	}

}
