package src.project.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
	protected List<CheckerObject<K, V>> checkerList = new ArrayList<CheckerObject<K, V>>();
	/** Adjustments to make before retrieving a value, e.g. append a / for directory paths */
	protected List<ValuePreparserObject<K, V>> preparserList = new ArrayList<ValuePreparserObject<K,V>>();
	/** Settings storage */
	protected HashMap<K, V> settingsMap = new HashMap<K, V>();
	

	
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
	 * @return <code>true</code>, if the given <code>value</code> is equal to the null value.
	 * @see #nullValue()
	 * @see #isSet(Comparable) for checking whether a value is set.
	 */
	public boolean isNull(final V value) {
		boolean isnull = nullValue() == value;
		if (!isnull) {
			try {
				isnull = nullValue().equals(value);
			} catch (NullPointerException e) {}
		}
		if (isnull) {
			return true;
		}
		return false;
	}
	
	
	

	////////// JUST4FUN ////////////
	/** Prints all properties to get an overview */
	public void print(String sep) {
		for (Entry<K, V> e : settingsMap.entrySet()) {
			System.out.println(e.getKey() + sep + e.getValue());
		}
	}
	/** Clamps all properties together */
	public String getList(String propertySeparator, String kvSeparator, boolean ignoreEmpty) {
		StringBuffer sb = new StringBuffer();
		for (Entry<K, V> e : settingsMap.entrySet()) {
			if (isSet(e.getKey()) || !ignoreEmpty) {
				sb.append(e.getKey() + kvSeparator + get_(e.getKey()) + propertySeparator);
			}
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - propertySeparator.length());
		}
		return sb.toString();
	}
	
	
	
	
	
	//////// SETTER/GETTER /////////
	
	/**
	 * @return The value belonging to <code>property</code>
	 */
	public V get_(final K property) {
		V value = settingsMap.get(property);
		for (ValuePreparserObject<K, V> vpo : preparserList) {
			value = vpo.adjust(property, value);
		}
		return value;
	}
	/**
	 * If <code>value</code> is equal to {@link #nullValue()} then <code>property</code> will be removed.
	 * @param value Value to insert.  
	 * @return true if <code>value</code> is valid.
	 */
	public boolean set_(final K property, final V value) {
		
		boolean success = true;

		// Remove if value is the null value
		if (isNull(value)) {
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
	 * <p>Adds a value preparser. The task of a value preparser is to change an outgoing value to meet
	 * certain properties, like paths ending always with /.</p>
	 * <p>Adjusting of the values is done when retrieving a value and not when setting it because 
	 * it is possible to append to values (resulting in uncontrollable behaviour).
	 */
	public void addPreparser(final ValuePreparser<V> a, final K key) {
		preparserList.add(new ValuePreparserObject<K, V>(a, key));
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
	abstract public static interface Adjuster<V> {
		public V adjust(V value);
	}
	abstract private static class AdjusterObject<K, V> {
		private final Adjuster<V> adjuster;
		private final K key;
		public AdjusterObject(final Adjuster<V> adjuster, final K key) {
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
	
	public static interface ValuePreparser<V> extends Adjuster<V> {
		/** @return Preparsed value. A value will be preparsed before leaving the getter method. */
		public V adjust(V value);
	}
	private static class ValuePreparserObject<K, V> extends AdjusterObject<K, V> {
		public ValuePreparserObject(ValuePreparser<V> preparser, K key) {
			super(preparser, key);
		}
	}
	
}
