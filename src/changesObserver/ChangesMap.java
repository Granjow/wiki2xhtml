package src.changesObserver;

import java.util.ArrayList;
import java.util.HashMap;

import src.Container_Files.WikiFile;


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
 * Stores the hash map for wiki2xhtml and looks whether files have been changed since last parsing.
 *
 * @author Simon Eugster
 *
 */
public class ChangesMap {

	private HashMap<String, src.changesObserver.ChangesWriter.ChangeItem> hashes = null;

	private src.Container_Files.Container c = null;

	public void init() {
		c = src.Container_Files.getInstance().cont;
	}

	/**
	 * Gets changed files
	 */
	public void buildHashes() {
		// TODO 0 Templates: Read file and test
		ArrayList<String> al = new ArrayList<String>();

		// Add all files to be parsed to a list
		for (WikiFile wf : c.files) {
			al.add(wf.f.getPath());
		}
		if (c.menuFile != null) al.add(c.menuFile.getPath());
		if (c.footerFile != null) al.add(c.footerFile.getPath());
		if (c.commonFile != null) al.add(c.commonFile.getPath());

		// Get the old hashes of all files
		hashes = ChangesWriter.getHashes(al);

		// Compare hashes with hashes of new files
		ChangesWriter.getUnchangedFiles(c.hashFile, hashes, true);
	}

	/**
	 * Writes the new hashes
	 */
	public void writeHashes() {
		if (hashes != null) {
			ChangesWriter.write(c.hashFile, hashes);
		}
	}

	/**
	 * Checks whether a file has changed since last parsing operation, using the hash map.
	 * @param filename
	 * @return Necessity to write the file
	 */
	public boolean needToWrite(String filename) {
		if (!src.Container_Files.getInstance().sc.incremental) return true;
		if ((c.menuFile != null && hasChanged(c.menuFile.getPath()))
				|| (c.footerFile != null && hasChanged(c.footerFile.getPath()))
				|| (c.commonFile != null && hasChanged(c.footerFile.getPath()))
		   ) {
			// Always rebuild files if menu, common or footer file has changed.
			return true;
		}
		return hasChanged(filename);
	}

	/**
	 * @param filename
	 * @return false, if the file has remained the same since last parsing
	 */
	private boolean hasChanged(String filename) {
		if (hashes == null) return true;

		if (hashes.containsKey(filename)) {
			return hashes.get(filename).changed;
		}
		return true;
	}

}
