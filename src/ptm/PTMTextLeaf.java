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
 * <p>This class represents a simple text leaf.</p>
 * <p>At the end, all content ends in a text leaf. Parameter names, arguments, plaintext, etc.</p>
 */
public class PTMTextLeaf extends PTMLeaf {

	public PTMTextLeaf(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);
		endIndex = beginIndex+1;
		if (endIndex > content.length()) {
			throw new ObjectNotApplicableException("End reached!");
		}
	}
	
	public String getContent() {
		return content.substring(beginIndex, endIndex);
	}
	
	public String evaluate() {
		return content.substring(beginIndex, endIndex);
	}
	
	/**
	 * Appends another text leaf. The condition for appending 
	 * is that <code>other</code> starts where <b><code>this</code></b> ends,
	 * as both work on the same StringBuffer.
	 * @return false if start and end point did not match
	 */
	public boolean append(PTMTextLeaf other) {
		if (endIndex == other.beginIndex) {
//			System.out.printf("Updating text end from %d to %d\n", endIndex, other.endIndex);
			endIndex = other.endIndex;
			return true;
		}
		return false;
	}

}
