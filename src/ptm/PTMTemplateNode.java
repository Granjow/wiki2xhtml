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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import src.ptm.PTM.PTMObjects;
import src.utilities.IORead_Stats;



/**
 * <p>This class handles templates.</p>
 * <p><code>{{: name | args }}</code></p>
 * <p>States, i.e. name/value bindings, can be forwarded from one template to the other
 * by passing one argument named {@link PTMState#forwardState}:</p>
 * <p><code>{{:name|args|FORWARD_STATE}}</code></p>
 * <p>The <em>name</em> template will then obtain the argument/value bindings from the current template
 * (except for those names which have been bound to a newer value already by this template call).</p>
 * TODO Doc State forwarding
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
		
		if (childTree.size() < 1) {
			throw new ObjectNotApplicableException("Not enough template arguments. Usage: {{:templateFilename|arguments...}}");
		}
		
		assert endIndex > this.beginIndex;
		assert childTree.size() > 0;
	}

	public String evaluate() throws RecursionException {

		File template;
		if (root.templateDirectory() != null) {
			template = new File(root.templateDirectory().getAbsolutePath() + File.separator + childTree.get(0).evaluate().trim());
		} else {
			template = new File(childTree.get(0).evaluate().trim());
		}
		
		try {
			// Read the template from the template file and parse it.
			// Detect recursion via maximum depth (a calls b, b calls a).
			
			StringBuffer sb = IORead_Stats.readSBuffer(template);
			
			// Read the new state into variables.
			PTMState state = new PTMState();
			state.readState(this);
			
			final String identifier = PTMArgumentNode.identifier + PTMState.forwardState; 
			forwardingSearch: for (PTMObject o : childTree) {
				if (identifier.equals(o.getRawContent())) {
					// Bind values from the root node to this node, except if a newer value is available
//					System.out.println("Forwarding state.");
//					state.printValues();
					for (String key : root.sigma.keySet()) {
						if (!state.containsKey(key) || PTMState.forwardState.equals(state.resolve(key))) {
							System.out.printf("Re-binding %s to %s (template value is %s).\n", key, root.sigma.resolve(key), state.resolve(key));
							state.bind(key, root.sigma.resolve(key));
						} else {
							System.out.printf("%s is already bound to %s.\n", key, state.resolve(key));
						}
					}
//					System.out.println("Forwarded. Result:");
//					state.printValues();
					break forwardingSearch;
				}
			}
			
			int depth;
			try {
				depth = Integer.parseInt(root.sigma.resolve(PTM.recursionKey));
			} catch (NumberFormatException e) {
				depth = 0;
				root.sigma.bind(PTM.recursionKey, Integer.toString(depth));
			}
			state.bind(PTM.recursionKey, Integer.toString(depth+1));
			
			if (depth >= PTM.recursionMaxDepth) {
				throw new RecursionException("Attention: Recursion detected in your template. Position: " + getRawContent());
//				return "Attention: Recursion in your templates.";
			} else {
				try {
					return PTM.parse(sb, state);
				} catch (RecursionException e) {
					return "Recursion detected! >>>" + getRawContent() + "<<<";
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return String.format("Error reading the template file %s (%s).", childTree.get(0).evaluate().trim(), template.getAbsolutePath());
	}

}
