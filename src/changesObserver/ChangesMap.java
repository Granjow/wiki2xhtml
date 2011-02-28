package src.changesObserver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import src.project.WikiProject;


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
 */

/**
 * Stores the hash map for wiki2xhtml and looks whether files have been changed since last parsing.
 *
 * @author Simon Eugster
 *
 */
public class ChangesMap {

	private HashMap<String, src.changesObserver.ChangesWriter.ChangeItem> _hashes = null;
	private final WikiProject _project;
	private final File hashFile;
	
	public ChangesMap(WikiProject project) {
		this._project = project;
		hashFile = new File(_project.projectDirectory().getAbsolutePath() + File.separator + ".wiki2xhtml-hashes");
	}

	/**
	 * Gets changed files
	 */
	public void buildHashes() {
		ArrayList<String> al = new ArrayList<String>();

		// Add all files to be parsed to a list
		for (int i = 0; i < _project.fileCount(); i++) {
			al.add(_project.projectDirectory().getAbsolutePath() + File.separator + _project.getFile(i).name);
		}
		// TODO common/menu files

		// Get the old hashes of all files
		_hashes = ChangesWriter.getHashes(al);

		// Compare hashes with hashes of new files
		ChangesWriter.getUnchangedFiles(hashFile, _hashes, true);
	}

	/**
	 * Writes the new hashes
	 */
	public void writeHashes() {
		if (_hashes != null) {
			ChangesWriter.write(hashFile, _hashes);
		}
	}

	/**
	 * Checks whether a file has changed since last parsing operation, using the hash map.
	 * @param filename
	 * @return Necessity to write the file
	 */
	public boolean needToWrite(String filename) {
//		if (!src.Container_Files.getInstance().sc.incremental) return true;
//		if ((c.menuFile != null && hasChanged(c.menuFile.getPath()))
//				|| (c.footerFile != null && hasChanged(c.footerFile.getPath()))
//				|| (c.commonFile != null && hasChanged(c.footerFile.getPath()))
//		   ) {
//			// Always rebuild files if menu, common or footer file has changed.
//			return true;
//		}
		return hasChanged(filename);
	}

	/**
	 * @param filename
	 * @return false, if the file has remained the same since last parsing
	 */
	private boolean hasChanged(String filename) {
		if (_hashes == null) return true;

		if (_hashes.containsKey(filename)) {
			return _hashes.get(filename).changed;
		}
		return true;
	}

}
