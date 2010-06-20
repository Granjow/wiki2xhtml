package src.ptm;

import java.util.ArrayList;

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
 * This ArrayList extension is special because it tries to append
 * text leaves to the last element in the list to assemble text.
 */
public class PTMArrayList extends ArrayList<PTMObject> {
	private static final long serialVersionUID = 1L;
	
	public boolean add(PTMObject e) {
		if (e instanceof PTMTextLeaf) {
			if (size() > 0) {
				PTMObject obj = get(size()-1);
				if (obj instanceof PTMTextLeaf) {
					((PTMTextLeaf) obj).append((PTMTextLeaf) e);
					return true;
				}
			}
			return super.add(e);
		}
		else {
			return super.add(e);
		}
	}

}
