package src.changesObserver;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import src.GenerateID;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.utilities.IORead_Stats;
import src.utilities.IOUtils;


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
 * Writes the hashes of files into a file and reads them in again;
 * compares the stored hashes to hashes of a file list to find unchanged files.
 *
 * @author Simon Eugster
 *
 */
public class ChangesWriter {

	/**
	 * Gets the hashes of some files.
	 * @param files File list
	 * @return
	 */
	public static HashMap<String, ChangeItem> getHashes(ArrayList<String> files) {

		HashMap<String, ChangeItem> changes = new HashMap<String, ChangeItem>();

		for (String filename : files) {
			ChangeItem ci = new ChangeItem(filename);
			ci.getHash();
			changes.put(filename, ci);
		}

		return changes;
	}


	/**
	 * Compares hashes of some files and their old stored hashes
	 * @param hashFile File containing the old hashes
	 * @param newHashes New hash list; unchanged files will be marked
	 * @param extend If true, hashes of old files will also be added to the HashMap.
	 */
	public static void getUnchangedFiles(File hashFile, HashMap<String, ChangeItem> newHashes, boolean extend) {

		HashMap<String, ChangeItem> oldHashes = new HashMap<String, ChangeItem>();
		BufferedReader br = null;

		if (hashFile.exists() && hashFile.canRead() && hashFile.isFile()) {

			if (IOUtils.binaryCheck(hashFile)) {
				CommentAtor.getInstance().ol("binary", CALevel.ERRORS);
				return;
			}

			// Read the old hashes stored in the file here
			try {

				br = new BufferedReader(new FileReader(hashFile));
				String line;
				ChangeItem ci;
				int n;

				while ((line = br.readLine()) != null) {
					n = line.indexOf(" ");
					if (n > 0) {
						ci = new ChangeItem(line.substring(n + 1));
						ci.hash = line.substring(0, n);
						oldHashes.put(ci.filename, ci);
					}
				}

			} catch (FileNotFoundException e) {
			} catch (IOException e) { }

			finally {
				try {
					br.close();
				} catch (IOException e) {}
			}

			Iterator<Entry<String, ChangeItem>> i = newHashes.entrySet().iterator();
			ChangeItem ci;

			while (i.hasNext()) {
				ci = i.next().getValue();
				if (oldHashes.containsKey(ci.filename)) {
					// Check whether the hash has stayed equal; if so, the file hasn't changed
					if (((ChangeItem) oldHashes.get(ci.filename)).hash.equals(ci.hash)) {
						ci.changed = false;
						CommentAtor.getInstance().ol("Unchanged: " + ci.filename + " (" + ci.hash + ")", CALevel.V_MSG);
					} else {
						CommentAtor.getInstance().ol("Changed: " + ci.filename + " (" + ci.hash + ")", CALevel.V_MSG);
					}
				}
			}

			if (extend) {
				// Add hashes to files which are not in newHashes yet
				i = oldHashes.entrySet().iterator();
				while (i.hasNext()) {
					ci = i.next().getValue();
					if (!newHashes.containsKey(ci.filename)) {
						newHashes.put(ci.filename, ci);
					}
				}
			}

		}

	}

	/**
	 * Writes hash plus filename into a file.
	 * @param hashFile File to contain the hash list
	 * @param hashes Hashes
	 */
	public static void write(File hashFile, HashMap<String, ChangeItem> hashes) {

		BufferedWriter bw = null;

		try {

			bw = new BufferedWriter(new FileWriter(hashFile));

			Iterator<Entry<String, ChangeItem>> i = hashes.entrySet().iterator();
			ChangeItem ci;

			while (i.hasNext()) {
				ci = i.next().getValue();
				bw.write(ci.hash + " " + ci.filename + "\n");
			}

		} catch (IOException e) { }

		finally {
			try {
				bw.close();
			} catch (IOException e) {}
		}

	}

	public static class ChangeItem {

		public String filename = new String();
		public String hash = new String();
		boolean exists = true;

		public boolean changed = true;

		ChangeItem(String filename) {
			this.filename = filename;
			File f = new File(filename);
			exists = f.exists() && f.canRead() && f.isFile();
		}

		public void getHash() {
			try {
				hash = (exists ?
						GenerateID.getMD5Hex(IORead_Stats.readSBuffer(new File(filename)).toString(), "", true) + ""
						: "");
			} catch (NullPointerException e) {
			} catch (IOException e) {
			}
		}

	}

	public static void main(String[] args) {

		CommentAtor.getInstance().setVerbose();

		File f = new File(".wiki2xhtml-hashes");

		ArrayList<String> al = new ArrayList<String>();
		al.add("index.txt");
		al.add("doc.txt");
		al.add("doc-de.txt");
		al.add("t");

		HashMap<String, ChangeItem> hm = getHashes(al);

		getUnchangedFiles(f, hm, true);

		write(f, hm);

	}

}
