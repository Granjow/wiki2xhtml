package src.utilities;

import java.io.*;

import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;

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
 *
 * Various write utilities.
 * Created: 19.03.2005
 *
 * @author Simon Eugster
 * ,		hb9eia
 *
 */
public class IOWrite {

	/**
	 * Creates a file and its parent paths if it does not exist already
	 * @throws IOException Crating new file failed
	 */
	public static void createIfNotExists(File f) throws IOException {

		if (f.exists()) {
			if (f.isDirectory()) {
				// Rename the directory and create a new configuration file
				File f2 = new File(f.getPath() + "~");
				f.renameTo(f2);
				f.createNewFile();
				CommentAtor.getInstance().ol(f.getAbsolutePath() + " renamed to " + f2.getAbsolutePath(), CALevel.MSG);
				CommentAtor.getInstance().ol(f.getAbsolutePath() + " created", CALevel.MSG);
			}
		} else {
			// Create the file, if it does not exist already
			f.getParentFile().mkdirs();
			f.createNewFile();
			CommentAtor.getInstance().ol(f.getAbsolutePath() + " created", CALevel.MSG);
		}

	}

	/**
	 * @param f - output file
	 * @param s - String to write
	 * @param append - appends the StringBuffer, if true
	 */
	public static void writeString(File f, String s, boolean append) throws IOException {
		FileWriter fw = null;

		try {

			// Create parent directories if necessary
			f.getParentFile().mkdirs();

			// Create the file if necessary
			f.createNewFile();

			// Write the file
			fw = new FileWriter(f, append);
			fw.write(s);

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}

	public static class WriteSuccess {
		public final static int FAILED = -1;
		public final static int FAILED_CLOSING_WRITER = -2;
		public final static int SUCCESS = 0;
	}
}
