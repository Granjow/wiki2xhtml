package src.project.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public abstract class Settings<K extends Comparable<?>, V extends Comparable<?>> {
	
	/** Checks to run before inserting */
	private List<CheckerObject<K, V>> checkerList = new ArrayList<CheckerObject<K, V>>();
	/** Adjustments to make before inserting a value, to preserve some properties */
	private List<ValueAdjusterObject<K, V>> adjusterList = new ArrayList<ValueAdjusterObject<K,V>>();
	/** Settings storage */
	private HashMap<K, V> settingsMap = new HashMap<K, V>();
	

	
	/////// TO IMPLEMENT ////////
	
	/**
	 * Calling <code>set_(K, nullValue())</code> removes K from the settingsMap.
	 * @return The «null value»
	 */
	abstract public V nullValue();
	
	/** Concatenates two V's */
	abstract protected V concatenate(V left, V right);
	
	
	
	//////// BOOL+SIMILAR ///////
	
	/** @returns true if <code>property</code> is set. */
	public boolean isSet(final K property) {
		return settingsMap.containsKey(property);
	}
	
	/**
	 * <p>Checks with the checkers added via {@link #addChecker(Checker, Comparable)}
	 * whether the <code>value</code> is valid for the given <code>property</code>.</p>
	 * <p>Invalid values cannot be set with {@link #set_(Object, Comparable)}.</p>
	 * @return <code>true</code> if <code>value</code> may be set.
	 */
	public boolean isValid(final K property, final V value) {
		boolean valid = true;
		for (CheckerObject<K, V> co : checkerList) {
			if (!co.valid(property, value)) {
				valid = false;
				break;
			}
		}
		return valid;
	}
	
	/**
	 * <p>Adjusts a value according to the ValueAdjusters set with {@link #addAdjuster(ValueAdjuster, Comparable)}.</p>
	 * @return The adjusted value, using the adjuster for the given <code>property</code>.
	 */
	public V adjust(final K property, V value) {
		for (ValueAdjusterObject<K, V> adjuster : adjusterList) {
			value = adjuster.adjust(property, value);
		}
		return value;
	}
	
	
	
	
	//////// SETTER/GETTER /////////
	
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
		success = isValid(property, value);
		if (success) {
			settingsMap.put(property, value);
		}
		
		return success;
	}

	/** 
	 * Appends <code>value</code> to already existing <code>property</code>
	 * using {@link #concatenate(Comparable, Comparable)}. 
	 */
	public void append_(final K property, final V value) {
		if (isSet(property)) {
			set_(property, concatenate(get_(property), value));
		} else {
			set_(property, value);
		}
	}
	
	/**
	 * Adds a checker. The task of the checker is to check values to be assigned to <code>key</code>
	 * for validity. Invalid values will be rejected.
	 */
	public void addChecker(final Checker<V> c, final K key) {
		checkerList.add(new CheckerObject<K, V>(c, key));
	}
	
	/**
	 * Adds a value adjuster. The task of a value adjuster is to change an incoming value to meet
	 * certain properties, like paths ending with / always.
	 */
	public void addAdjuster(final ValueAdjuster<V> a, final K key) {
		adjusterList.add(new ValueAdjusterObject<K, V>(a, key));
	}
	
	
	
	
	////////// CLASSES ///////////
	
	/**
	 * Value checker
	 */
	public static interface Checker<V> {
		/**
		 * @return true if <code>value</code> is valid.
		 */
		public boolean check(V value);
	}
	
	private static class CheckerObject<K, V>  {
		private final Checker<V> checker;
		private final K key;
		public CheckerObject(final Checker<V> checker, final K key) {
			this.checker = checker;
			this.key = key;
		}
		public boolean valid(final K key, final V value) {
			if (key.equals(this.key)) {
				return checker.check(value);
			}
			return true;
		}
	}
	
	/**
	 * Value adjuster
	 */
	public static interface ValueAdjuster<V> {
		/** @return Adjusted value; e.g. for a path make sure that it ends with a / */
		public V adjust(V value);
	}
	private static class ValueAdjusterObject<K, V> {
		private final ValueAdjuster<V> adjuster;
		private final K key;
		public ValueAdjusterObject(final ValueAdjuster<V> adjuster, final K key) {
			this.adjuster = adjuster;
			this.key = key;
		}
		public V adjust(final K key, final V value) {
			if (key.equals(this.key)) {
				return adjuster.adjust(value);
			}
			return value;
		}
	}
	
}
