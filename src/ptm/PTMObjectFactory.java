package src.ptm;

import java.util.List;
import java.util.regex.Matcher;

import src.ptm.PTM.PTMObjects;
import src.ptm.PTMObject.EndOfExpressionReachedException;
import src.ptm.PTMObject.ObjectNotApplicableException;

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
 * This class determines which kind of object node to create. It is the
 * managing class that knows everything.
 */
public class PTMObjectFactory {


	public static final PTMObject buildObject(StringBuffer content, int index, PTMNode parent, PTMRootNode root) {
		try {
			return buildObject(content, index, parent, root, PTM.eofAbortFunction);
		} catch (EndOfExpressionReachedException e) {
			return null;
		}
	}
	public static final PTMObject buildObject(StringBuffer content, int index, PTMNode parent, PTMRootNode root, AbortFunction abort) throws EndOfExpressionReachedException {
		return buildObject(content, index, parent, root, abort, PTM.defaultChildren);
	}
	
	
	/**
	 * <p>Tries to build a new {@link PTMObject} at the given index.</p>
	 * <p>This method is called recursively. The steps are as follows:</p>
	 * <ol>
	 * <li>Check whether we are at the end of the file or whether the parent expression ends here. In the latter case, throw an exception to notify the parent.</li>
	 * <li>Try to create a new object of some (given) types. If it can be created, the object itself calls this method again to find further objects.</li>
	 * </ol> 
	 * @param parent The parent object of the child that will/might be created.
	 * @param abort Function handle that checks when to abort because the end of the parent expression has been reached (see point 1).
	 * @param allowedChildnodes Allowed child types. Only these will be tried to create (see point 2).
	 * @return A new object or null, if no suitable found (either due to object restrictions by <code>allowedChildnodes</code> or because the End of File has been reached).
	 * @throws EndOfExpressionReachedException If the end of the parent expression has been reached (see point 1). This is checked here and not in the parent
	 * to abbreviate recognition of plaintext.
	 */
	public static final PTMObject buildObject(StringBuffer content, int index, PTMNode parent, PTMRootNode root, AbortFunction abort, List<PTMObjects> allowedChildnodes) throws EndOfExpressionReachedException {
		if (index >= content.length()) { 
			return null;
		}
		if (abort.abort(content, index)) {
			throw new EndOfExpressionReachedException();
		}

		String indicator = getIndicator(content, index);
		PTMObject obj = null;
		Matcher m;
		
//		System.out.println(indicator);
		
		// Parser function {{#if: a|b|c}}
		if (allowedChildnodes.contains(PTMObjects.Function) || allowedChildnodes.contains(PTMObjects.FunctionIf)) {
			m = PTMFunctionIf.startPattern.matcher(indicator);
			if (m.find()) {
				try {
					obj = new PTMFunctionIf(content, index, parent, root);
				} catch (ObjectNotApplicableException e) { obj = null; }
			}
		}
		
		// Template {{:template|args}}
		if (obj == null) {
			if (allowedChildnodes.contains(PTMObjects.Template)) {
				if (indicator.startsWith(PTMTemplateNode.identifier)) {
					try {
						obj = new PTMTemplateNode(content, index, parent, root);
					} catch (ObjectNotApplicableException e) { obj = null; }
				}
			}
		}
		
		// Argument arg1|arg2|arg3
		if (obj == null) {
			if (allowedChildnodes.contains(PTMObjects.Argument)) {
				// Inside the {{}} text nodes are not allowed (only as subchild!). 
				// If text is allowed, then arguments usually don't make sense because 
				// text leaves should always be children of an Argument.
				if (!allowedChildnodes.contains(PTMObjects.Text)) {
					try {
						obj = new PTMArgumentNode(content, index, parent, root);
					} catch (ObjectNotApplicableException e) { obj = null; }
				}
			}
		}
		
		// Parameter {{{param}}}
		if (obj == null) {
			if (allowedChildnodes.contains(PTMObjects.Parameter)) {
				if (indicator.startsWith(PTMParameterNode.identifier)) {
					try {
						obj = new PTMParameterNode(content, index, parent, root);
					} catch (ObjectNotApplicableException e) { obj = null; }
				}
			}
		}
		
		// Text leaf
		if (obj == null) {
			
			if (allowedChildnodes.contains(PTMObjects.Text)) {
				try {
					obj = new PTMTextLeaf(content, index, parent, root);
				} catch (ObjectNotApplicableException e) { obj = null; }
			}
		}
		
		return obj;
	}
	
	private static final int min(int left, int right) {
		return (left < right) ? left : right;
	}
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("a {{#if:a|a=b|c}} {{{param|}}}");
		
		PTMRootNode root = new PTMRootNode(sb);
		root.printTree(System.out, null);
		System.out.println("\nEvaluation:\n" + root.evaluate());
	}
	
	/**
	 * Returns a short substring to work with regular expressions on.
	 * They are needed because e.g. <code>{{#if:</code> could also be written with whitespaces as <code>{{ #if :</code>.
	 * This short indicator takes just a reasonable number of characters to keep things efficient.
	 */
	public static final String getIndicator(StringBuffer content, int index) {
		return content.substring(index, min(index+15, content.length()));
	}
	
}
