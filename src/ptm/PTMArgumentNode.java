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
	
	/**
	 * <p>An argument consists of an optional  node may contain upto two direct children:</p>
	 * <ul> TODO
	 * <li>A {@link PTMArgumentNameNode}
	 * @param content
	 * @param beginIndex
	 * @param parent
	 * @param root
	 * @throws ObjectNotApplicableException
	 */
	public PTMArgumentNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);
		
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
		
		// Don't catch the exception here; if no name node can be created, 
		// then the argument node cannot be created either.
		PTMArgumentNameNode ann = new PTMArgumentNameNode(content, endIndex, this, root, abort);
		endIndex = ann.endIndex;
		if (abort.abort(content, endIndex)) {
			// End of the argument reached, so the object must be a value and not a name.
			// Set an implicit name instead.
			childTree.add(new PTMArgumentNameNode(content, ann.beginIndex, this, root, parent.getNextArgNumber()));
			childTree.add(ann.toPTMArgumentValueNode());
		} else {
			// we are at a name/value separator
			endIndex++;
			childTree.add(ann);
			
			// Again, don't catch the exception
			PTMArgumentValueNode avn = new PTMArgumentValueNode(content, endIndex, parent, root, abort);
			childTree.add(avn);
			endIndex = avn.endIndex;
		}

		assert endIndex > this.beginIndex;
		assert childTree.size() == 2;
	}

	public String evaluate() {
		//return childTree.get(1).evaluate();
		String s = getRawContent();
		if (s.startsWith(Character.toString(identifier))) {
			return s.substring(1);
		} else {
			return s;
		}
	}

}
