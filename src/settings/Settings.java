package src.settings;

import java.util.HashMap;

/*
 *   Copyright (C) 2007-2010 Simon Eugster <granjow@users.sf.net>

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
 *
 *
 */

abstract class Settings<K, V extends Comparable<?>> {

	HashMap<K, V> settingsMap = new HashMap<K, V>();
	
	/**
	 * Calling <code>set_(K, nullValue())</code> removes K from the settingsMap.
	 * @return The «null value»
	 */
	abstract public V nullValue();
	
	/**
	 * @param property
	 * @return <code>true</code> if <code>property</code> is available (has been set).
	 */
	public boolean contains(final K property) {
		return settingsMap.containsKey(property);
	}
	
	/**
	 * @param property
	 * @return The value belonging to <code>property</code>
	 */
	public V get_(final K property) {
		return settingsMap.get(property);
	}
	/**
	 * @param property
	 * @param value Value to insert. 
	 * <ul><li>If <code>value</code> is equal to {@link #nullValue()} then <code>property</code> will be removed.</li> 
	 * <li>If <code>value</code> is <code>null</code>, nothing will be done.</li></ul>
	 * @return true if no error occurred (like negative value for GalleryImagesPerLine)
	 */
	public boolean set_(final K property, final V value) {
		
		boolean success = true;

		if (value == null) return true;
		
		if (nullValue().equals(value)) {
			settingsMap.remove(property);
			return true;
		}
		
		success = setCheck(property, value);
		
		if (success) {
			settingsMap.put(property, value);
		}
		
		return success;
	}
	
	/**
	 * Additional checks for <code>value</code> before it is set. 
	 * May e.g. test whether a value is > 0 and return false if not;
	 * in this case {@link #set_(Object, Comparable)} will not set <code>value</code>.
	 * @param property
	 * @param value
	 * @return <code>true</code> if <code>value</code> may be set.
	 */
	abstract boolean setCheck(final K property, final V value);
	
}
