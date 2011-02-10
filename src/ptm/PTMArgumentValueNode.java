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

public class PTMArgumentValueNode extends PTMNode {

	public PTMArgumentValueNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root, AbortFunction abort) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);

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
		
		assert endIndex > this.beginIndex;
	}
	
	public PTMArgumentValueNode(StringBuffer content, int beginIndex, int endIndex, PTMNode parent, PTMRootNode root, PTMArrayList children) {
		super(content, beginIndex, parent, root);
		this.endIndex = endIndex;
		if (children != null) childTree.addAll(children);
		assert this.beginIndex <= this.endIndex;
	}
	
	/** This constructor is for an empty Value node and should only be used when required. */
	public PTMArgumentValueNode(StringBuffer content) {
		super(content, 0, null, null);
		this.endIndex = content.length();
	}

	public String evaluate() throws RecursionException {
		
		if (childTree.size() == 0 && beginIndex == 0 && endIndex == content.length()) {
			// This node has been constructed as an empty Value node via PTMArgumentValueNode(StringBuffer content)
			return content.toString();
		}
		
		StringBuilder sb = new StringBuilder();
		for (PTMObject o : childTree) {
			sb.append(o.evaluate());
		}
		return sb.toString();
	}

}
