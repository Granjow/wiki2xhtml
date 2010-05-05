package src.utilities;

import java.io.File;

import javax.swing.JFileChooser;

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
 */

/**
 * Some useful (?) dialogues
 *
 * @author Simon Eugster
 * ,		hb9eia
 *
 */
public class FileDialogues {

	public static File getFile(String title, File currentDir) {
		JFileChooser jfc = new JFileChooser(currentDir);
		jfc.setDialogType(JFileChooser.OPEN_DIALOG);
		jfc.setDialogTitle(title);

		int returnValue = jfc.showOpenDialog(null); // showSaveDialog()
		File file = null;

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			file = jfc.getSelectedFile();
		}

		return file;
	}

	public static File[] getFiles(String title, File currentDir,
								  File[] selectedFiles) {
		JFileChooser jfc = new JFileChooser(currentDir);
		jfc.setDialogType(JFileChooser.OPEN_DIALOG);
		jfc.setMultiSelectionEnabled(true);
		jfc.setDialogTitle(title);
		jfc.setSelectedFiles(selectedFiles);

		int returnValue = jfc.showOpenDialog(null); // showSaveDialog()
		File[] files = null;

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			files = jfc.getSelectedFiles();
		}

		return files;
	}

	public static File getDirectory(String title) {
		return getDirectory(title, new File(java.lang.System
											.getProperty("user.dir")));
	}

	public static File getDirectory(String title, File currentDir) {
		JFileChooser jfc = new JFileChooser(currentDir);
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setDialogType(JFileChooser.OPEN_DIALOG);
		jfc.setDialogTitle(title);

		if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			return jfc.getSelectedFile();
		else
			return null;
	}

	public static void main(String[] args) {
		getDirectory("bla");
	}
}
