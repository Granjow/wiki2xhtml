package src.ptm;

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
 * Represents a node in PTM's evaluation tree.
 */
public abstract class PTMNode extends PTMObject {
	
	private int nextArgNumber = 0;
	
	/** Maintains a state (name/value binding) */
	protected PTMState sigma;

	/**
	 * <p>The state <code>sigma</code> is inherited from the parent object (if available).
	 * If the state needs to be updated, this has to be done in a subclass (e.g. when 
	 * creating a template object).</p>
	 */
	public PTMNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) {
		super(content, beginIndex, parent, root);
		
		if (parent != null) {
			sigma = parent.sigma;
		} else {
			sigma = new PTMState();
		}
		
		assert sigma != null;
	}
	
	/**
	 * <p>This method is a counter for arguments. Arguments without an explicit name binding (name=value)
	 * are numbered. MediaWiki behaviour is:</p>
	 * <code>arg1 | 3=arg3 | arg2</code>
	 * @throws RecursionException 
	 */
	public String getNextArgNumber() {
		String next;
		boolean changed;
		
		// root.parent is null; catch this error.
		// TODO good idea?
		PTMObject tree;
		if (this instanceof PTMRootNode) {
			tree = this;
		} else {
			tree = parent;
		}
		assert tree != null;
		assert tree.childTree != null;
		
		do {
			changed = false;
			next = Integer.toString(nextArgNumber);
			for (PTMObject o : tree.childTree) {
				if (o instanceof PTMArgumentNode) {
					if (o.childTree.size() > 0) {
						if (o.childTree.get(0) instanceof PTMArgumentNameNode) {
							try {
								if (next.equals(o.childTree.get(0).evaluate())) {
									nextArgNumber++;
									changed = true;
								}
							} catch (RecursionException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} while (changed);
		nextArgNumber++;
		return next;
	}
	
}
