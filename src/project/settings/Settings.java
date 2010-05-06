package src.project.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 *   Copyright (C) 2007-2009 Simon Eugster <granjow@users.sf.net>

 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

public abstract class Settings<K extends Comparable<?>, V extends Comparable<?>> {
	
	private List<CheckerObject> checkerList = new ArrayList<CheckerObject>();
	private HashMap<K, V> settingsMap = new HashMap<K, V>();
	
	public static enum SettingContext {
		page,
		project
	}

	/**
	 * Calling <code>set_(K, nullValue())</code> removes K from the settingsMap.
	 * @return The «null value»
	 */
	abstract public V nullValue();
	
	/**
	 * @return <code>true</code> if <code>property</code> is available (has been set).
	 */
	public boolean contains(final K property) {
		return settingsMap.containsKey(property);
	}
	
	/**
	 * @return The value belonging to <code>property</code>
	 */
	public V get_(final K property) {
		return settingsMap.get(property);
	}
	/**
	 * If <code>value</code> is equal to {@link #nullValue()} then <code>property</code> will be removed.
	 * @param value Value to insert.  
	 * @return true if <code>value</code> is valid.
	 */
	public boolean set_(final K property, final V value) {
		
		boolean success = true;

		// Remove if value is the null value
		boolean isnull = nullValue() == value;
		if (!isnull) {
			try {
				isnull = nullValue().equals(value);
			} catch (NullPointerException e) {}
		}
		if (isnull) {
			settingsMap.remove(property);
			return true;
		}
		
		// Add otherwise (if valid)
		success = valid(property, value);
		if (success) {
			settingsMap.put(property, value);
		}
		
		return success;
	}
	
	/**
	 * <p>Checks with the checkers added via {@link #addChecker(Checker, Comparable)}
	 * whether the <code>value</code> is valid for the given <code>property</code>.</p>
	 * <p>Invalid values cannot be set with {@link #set_(Object, Comparable)}.</p>
	 * @return <code>true</code> if <code>value</code> may be set.
	 */
	public boolean valid(final K property, final V value) {
		boolean valid = true;
		for (CheckerObject co : checkerList) {
			if (!co.valid(property, value)) {
				valid = false;
				break;
			}
		}
		return valid;
	}
	
	/**
	 * Adds a checker. The task of the checker is to check values to be assigned to <code>key</code>
	 * for validity. Invalid values will be rejected.
	 */
	public void addChecker(Checker<V> c, K key) {
		checkerList.add(new CheckerObject(c, key));
	}
	
	/**
	 * Value checker
	 */
	public static interface Checker<V> {
		/**
		 * @return true if <code>value</code> is valid.
		 */
		public boolean check(V value);
	}
	
	private class CheckerObject  {
		private final Checker<V> checker;
		private final K key;
		public CheckerObject(Checker<V> checker, K key) {
			this.checker = checker;
			this.key = key;
		}
		public boolean valid(K key, V value) {
			if (key.equals(this.key)) {
				return checker.check(value);
			}
			return true;
		}
	}
	
}
