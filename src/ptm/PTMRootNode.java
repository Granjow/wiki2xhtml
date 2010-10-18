package src.ptm;

import src.utilities.StringTools;

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
 * <p>This root node builds an entire tree from a given input.</p>
 */
public class PTMRootNode extends PTMNode {
	
	public PTMRootNode(StringBuffer content, PTMState sigma) {
		super(content, 0, null, null);

		// Use the delivered state if it is not null.
		if (sigma != null) { this.sigma = sigma; }
		
		long start = System.currentTimeMillis();
		endIndex = 0;
		
		PTMObject obj;
		do {
			obj = PTMObjectFactory.buildObject(content, endIndex, this, this);
			if (obj != null) {
				childTree.add(obj);
				endIndex = obj.endIndex;
			}
		} while (obj != null);
		
		System.out.printf("Time taken: %s\n", StringTools.formatTimeMilliseconds(System.currentTimeMillis()-start));
		
		assert endIndex == content.length();
		assert this.sigma != null;
	}
	
	public String evaluate() {
		StringBuilder sb = new StringBuilder();
		for (PTMObject o : childTree) {
			sb.append(o.evaluate());
		}
		return sb.toString();
	}

}
