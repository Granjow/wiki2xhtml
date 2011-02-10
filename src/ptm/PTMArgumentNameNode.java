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

public class PTMArgumentNameNode extends PTMNode {
	
	public static final char separator = '=';
	private String implicitName = null;

	public PTMArgumentNameNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root, final AbortFunction abortParent) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);
		AbortFunction abort = new AbortFunction() {
			public boolean abort(StringBuffer content, int index) {
				return abortParent.abort(content, index) || content.charAt(index) == separator;
			}
		};
		endIndex = beginIndex;
		
		boolean nameEndReached = false;
		PTMObject obj;
		do {
			try {
				obj = PTMObjectFactory.buildObject(content, endIndex, this, root, abort, PTM.defaultChildren);
				if (obj != null) {
					childTree.add(obj);
					endIndex = obj.endIndex;
				}
			} catch (EndOfExpressionReachedException e) {
				nameEndReached = true;
				break;
			}
		} while (obj != null);
		if (!nameEndReached) {
			throw new ObjectNotApplicableException("No end of name in sight.");
		}
		
		assert endIndex >= this.beginIndex;
	}
	
	/**
	 * Creates a node with an implicit name (if no name explicitly given with <code>name=value</code>).
	 */
	public PTMArgumentNameNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root, String name) {
		super(content, beginIndex, parent, root);
		endIndex = this.beginIndex;
		implicitName = name;
	}
	
	public PTMArgumentValueNode toPTMArgumentValueNode() {
		return new PTMArgumentValueNode(content, beginIndex, endIndex, parent, root, childTree);
	}
	

	public String evaluate() throws RecursionException {
		if (implicitName != null) {
			return implicitName;
		} else {
			StringBuilder sb = new StringBuilder();
			for (PTMObject o : childTree) {
				sb.append(o.evaluate());
			}
			return sb.toString();
		}
	}

}
