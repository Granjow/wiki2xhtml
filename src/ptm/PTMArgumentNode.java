/*
 *   Copyright (C) 2010-2011 Simon A. Eugster <simon.eu@gmail.com>

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

import src.ptm.PTM.PTMObjects;

/**
 * <p>This class handles arguments.</p>
 * <p><code>arg1 | arg2 | arg3</code></p>
 * <p>The end of an argument is determined by the type of the parent,
 * either when a new argument starts (they are separated by the <code>|</code> character)
 * or when the parent object ends (e.g. with <code>}}</code> for functions).</p>
 * <p>A parent object cannot end within a child node of an argument node.</p>
 * <p><code>arg1|arg2|arg3</code> is separated into <code>arg1</code>, <code>|arg2</code>, and <code>|arg3</code>.</p>
 */
public class PTMArgumentNode extends PTMNode {
	
	public static final char identifier = '|';
	private static final List<PTMObjects> allowedChildnodes;
	
	static {
		allowedChildnodes = new ArrayList<PTMObjects>();
		for (PTMObjects o : PTMObjects.values()) {
			if (o != PTMObjects.Argument) allowedChildnodes.add(o);
		}
	}
	
	public PTMArgumentNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) throws ObjectNotApplicableException {
		this(content, beginIndex, parent, root, false);
	}
	
	/**
	 * <p>An argument will always contain two direct children:</p>
	 * <ul>
	 * <li>A {@link PTMArgumentNameNode}</li>
	 * <li>A {@link PTMArgumentValueNode}</li>
	 * </ul>
	 * <p>The name of a value can be set with the assignment <code>name=value</code>. If no assignment is given, the name
	 * is left blank 
	 */
	public PTMArgumentNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root, boolean empty) throws ObjectNotApplicableException {
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
		
		
		if (empty) {
			// Special case desired by the caller: A zero-length argument node. 
			// This is necessary e.g. in
			//   {{#if:||b}}
			// since the first argument starts after the : and ends before the first |. 
			PTMArgumentNameNode ann = new PTMArgumentNameNode(content, endIndex, this, root, "");
			childTree.add(ann);
			childTree.add(ann.toPTMArgumentValueNode());
			
		} else if (content.charAt(endIndex) == identifier && endIndex == content.length()-1) {
			// Another special case: The input string consists of arguments only, i.e. there 
			// are no surrounding characters which terminate the expression. In an expression like
			//   some|arguments|ending|with|a|pipe|
			// the function would try to increase endIndex to after the last pipe and test for 
			// new objects there, which leads to an IndexOutOfBoundsException. This case is recognized
			// here and the error prevented. endIndex still needs to be increased at the end 
			// to avoid an infinite loop (more and more empty nodes added since the index does not change).
			PTMArgumentNameNode ann = new PTMArgumentNameNode(content, endIndex, this, root, "");
			childTree.add(ann);
			childTree.add(ann.toPTMArgumentValueNode());
			endIndex++;
			
		} else {
			
			if (content.charAt(endIndex) == identifier) { endIndex++; }
			if (PTM.eofAbortFunction.abort(content, endIndex)) {
				// This happens e.g. on an input string like «this|» when trying to parse Arguments.
				throw new ObjectNotApplicableException("Trying to create new node at the end of file.");
			}
			
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
		}
		
		if (endIndex <= this.beginIndex) {
			System.err.printf("%d (%d) to %d.", beginIndex, this.beginIndex, endIndex);
		}

		assert (endIndex > this.beginIndex) || empty; // If empty is true, the length is 0. Special case. Needs to be handled by the caller as well.
		assert childTree.size() == 2;
	}

	public String evaluate() throws RecursionException {
		return childTree.get(1).evaluate();
	}

}
