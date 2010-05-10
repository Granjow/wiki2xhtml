package src.utilities;

import java.util.ArrayList;

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
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * This class seems to handle lists.
 *
 * @author Simon Eugster
 * ,		hb9eia
 */
public class HandlerLists implements Cloneable {

	private int opened = 0;
	/** how many levels deeper? */
	private int difference = 0;
	private String allowedChars = "";
	private StringBuffer levels = new StringBuffer();
	private ArrayList<String> sameBase = new ArrayList<String>();

	public HandlerLists() {}

	/** @return the deepness of the list entry */
	public int size() {
		return levels.length();
	}

	public char last() {
		return levels.charAt(levels.length() - 1);
	}

	public int difference() {
		return difference;
	}

	public StringBuffer levels() {
		return levels;
	}

	public char get(int level) throws NullPointerException {
		return levels.charAt(level - 1);
	}

	public void parse(String s) {
		levels.setLength(0);
		for (int i = 0; i < s.length(); i++)
			if (allowedChars.indexOf(s.charAt(i)) >= 0)
				levels.append(s.charAt(i));
			else
				break;
	}

	/**
	 * @return true, if they have the same length and the same structure (e.g. : ^= ;)
	 */
	public boolean sameStructureAs(HandlerLists l) {

		if (l.size() != this.size())
			return false;

		else if (nearlyEqual(last(), l.last())) {
			for (int lvl = 1; lvl < (l.size() < this.size() ? l.size() : this.size()) - 1; lvl++)
				if (!nearlyEqual(this.type(lvl), l.type(lvl)))
					return false;
			return true;
		}

		return false;
	}

	/**
	 *
	 * @param l Other ListHandler
	 * @return The number of «equal» entries (: is e.g. equal to ; if defined so in sameBase)
	 */
	public int equalEntries(HandlerLists l) {
		int i = 0;

		int size = levels.length() < l.levels.length() ? levels.length() : l.levels.length();

		boolean next = false;
		while (i < size) {
			next = false;

			if (levels.charAt(i) == l.levels.charAt(i)) {
				i++;
				next = true;
			} else
				for (String s : sameBase)
					if (s.indexOf(levels.charAt(i)) >= 0 && s.indexOf(l.levels.charAt(i)) >= 0) {
						i++;
						next = true;
					}

			if (!next)
				break;
		}

		return i;
	}

	/**
	 * @return True if c1 and c2 are «nearly» equal
	 */
	private boolean nearlyEqual(char c1, char c2) {

		if (c1 == c2)
			return true;

		for (String s : sameBase)
			if (s.indexOf(c1) >= 0 && s.indexOf(c2) >= 0)
				return true;

		return false;
	}

	/**
	 * @param level Which level has to be compared?
	 * @param lh With which other ListHandler?
	 * @return true, if they're «nearly equal» (same item tag)
	 */
	public boolean nearlyEqual(int level, HandlerLists lh) {
		return nearlyEqual(levels.charAt(level - 1), lh.get(level));
	}

	/**
	 * Sets the variable difference
	 * @param l Other ListHandler
	 */
	public void getDiffBetween(HandlerLists l) {
		int i = equalEntries(l);

		difference = size() - i;
		l.difference = l.size() - i;
	}

	public void replace(String levels) {
		if (levels.length() >= this.levels.length())
			this.levels = new StringBuffer(levels);
		else
			this.levels = this.levels.replace(0, levels.length(), levels.toString());
	}

	public void open(int i) {
		opened = opened | 1 << (i-1);
	}

	public boolean openedItem(int i) {
		return (opened & 1 << (i-1)) != 0;
	}

	public void trim(int i) {
		opened = opened & ((1 << i) - 1);
	}

	/**
	 * @param level Level, starting with <b>1</b>
	 * @return The level's character
	 */
	public char type(int level) throws NullPointerException {
		return levels.charAt(level - 1);
	}

	/* General Functions */

	public void clear() {
		levels = new StringBuffer();
		allowedChars = "";
		sameBase.clear();
		opened = 0;
	}

	public HandlerLists clone() {
		try {
			return (HandlerLists) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new InternalError();
		}
	}


	/* Settings */

	public void addSameBase(String sameBase) {
		this.sameBase.add(sameBase);
	}

	public void allowChars(String allowedChars) {
		this.allowedChars = allowedChars;
	}

	public void setOpened(int opened) {
		this.opened = opened;
	}

	public int getOpened() {
		return opened;
	}

}
