package src.project.settings;

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
 */

public abstract class SettingReader<K extends Comparable<?>, V extends Comparable<?>> {
	
	/**
	 * Reads one (or more) setting from <code>in</code> into <code>settings</code>.
	 * @param remove Remove setting in <code>in</code> if found.
	 * @param in StringBuffer containing the settings. */
	abstract public boolean read(Settings<K, V> settings, StringBuffer in, boolean remove);
	
	/** The ID will be used for identifying a reader. */
	abstract public String getID();
	
	/** Two Readers are identical if their ID is equal. */
	public boolean equals(Object obj) {
		if (obj instanceof SettingReader<?, ?>) {
			return ((SettingReader<?,?>) obj).getID().equals(this.getID());
		}
		return false;
	}

}
