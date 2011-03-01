/*
 *   Copyright (C) 2007-2011 Simon A. Eugster <simon.eu@gmail.com>

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


package src.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import src.GenerateID;


/**
 * <p>Detects changes on files based on their MD5 hash.</p>
 * <p>Contains {@code<Name, Hash>} entries.</p>
 */
public class FileChangesMap extends HashMap<String, String> {
	private static final long serialVersionUID = 1L;

	public FileChangesMap(File baseDir, String hashFile) {
		assert baseDir != null;
		_baseDir = baseDir;
		_hashFile = new File(baseDir.getAbsolutePath() + File.separator + hashFile);
		read();
	}
	
	/**
	 * Compares the current hash to the stored hash
	 * @return {@code false} if the file hash has changed.
	 */
	public boolean queryUnchanged(final String filename) {
		boolean unchanged = false;
		
		if (containsKey(filename)) {
			try {
				String hash = hash(filename);
				String oldHash = get(filename);
				unchanged = hash.equals(oldHash);
			} catch (IOException e) { e.printStackTrace(); }
		}
		
		return unchanged;
	}
	
	/**
	 * Updates the hash of the given file name and writes the updated hash file.
	 */
	public void update(final String filename) throws IOException {
		String hash = hash(filename);
		put(filename, hash);
		write();
	}
	
	/**
	 * File format: HASH1 NAME1\nHASH2 NAME2\n
	 * @throws IOException
	 */
	private void write() throws IOException {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> e : entrySet()) {
			sb.append(String.format("%s %s\n", e.getValue(), e.getKey()));
		}
		IOWrite_Stats.writeString(_hashFile, sb.toString(), false);
	}
	
	private void read() {
		try {
			StringBuffer sb = IORead_Stats.readSBuffer(_hashFile);
			BufferedReader br = new BufferedReader(new StringReader(sb.toString()));
			String line;
			while ((line = br.readLine()) != null) {
				String[] entry = line.split(" ", 2);
				if (entry.length < 2) { continue; }
				if (entry[0].trim().length() <= 0) { continue; }
				if (entry[1].trim().length() <= 0) { continue; }
				put(entry[1], entry[0]);
			}
		} catch (IOException e) {
//			System.out.println("Hash file does not exist: " + _hashFile.getAbsolutePath());
		}
	}

	
	private final File _baseDir;
	private final File _hashFile;
	
	private String hash(String filename) throws IOException {
		return GenerateID.getMD5Hex(IORead_Stats.readSBuilder(new File(_baseDir.getAbsolutePath() + File.separator + filename)).toString(), "", true);
	}

}
