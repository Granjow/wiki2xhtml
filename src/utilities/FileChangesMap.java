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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.GenerateID;


/**
 * <p>Detects changes on files based on their MD5 hash.</p>
 * <p>Contains {@code <Name, Hash>} entries.</p>
 */
public class FileChangesMap {
	private HashMap<String, String> map;
	private HashMap<String, List<String>> includes;
	
	private List<String> includesToUpdate;

	public FileChangesMap(File baseDir, String hashFile) {
		assert baseDir != null;
		
		_baseDir = baseDir;
		_hashFile = new File(baseDir.getAbsolutePath() + File.separator + hashFile);
		map = new HashMap<String, String>();
		includes = new HashMap<String, List<String>>();
		includesToUpdate = new ArrayList<String>();
		
		read();
	}
	
	/**
	 * Compares the current hash to the stored hash
	 * @return {@code false} if the file hash has changed.
	 */
	public boolean queryUnchanged(final String filename) {
		boolean unchanged = false;
		
		if (map.containsKey(filename)) {
			try {
				String hash = hash(filename);
				String oldHash = map.get(filename);
				unchanged = hash.equals(oldHash);
				
				if (unchanged) {
					if (includes.containsKey(filename)) {
						for (String s : includes.get(filename)) {
							if (!queryUnchanged(s)) {
								unchanged = false;
								System.out.println("         Included file " + s + " has changed.");
								break;
							}
						}
					}
				}
			} catch (IOException e) { e.printStackTrace(); }
		}
		
		return unchanged;
	}
	
	/**
	 * Updates the hash of the given file name and writes the updated hash file.
	 */
	public void update(final String filename) throws IOException {
		String hash = hash(filename);
		map.put(filename, hash);
		write();
	}
	
	
	/**
	 * Updates the hashes and bindings for a file including another one (templates) and writes the updated hash file.
	 */
	public void updateInclude(final String filename, final String includedFile) throws IOException {
		if (!includes.containsKey(filename)) {
			ArrayList<String> list = new ArrayList<String>();
			includes.put(filename, list);
		}
		
		if (!includes.get(filename).contains(includedFile)) {
			includes.get(filename).add(includedFile);
			System.err.printf("\t%s includes %s\n", filename, includedFile);
		}
		
		if (!includesToUpdate.contains(includedFile)) {
			includesToUpdate.add(includedFile);
		}
	}
	
	public void updateIncludedHashes() throws IOException {
		for (String s : includesToUpdate) {
			System.err.printf("Updating %s.\n", s);
			update(s);
		}
		includesToUpdate.clear();
	}
	
	/**
	 * <p>File format: </p>
	 * <code>HASH1 NAME1\n<br/>
	 * HASH2 NAME2\n<br/>
	 * NAME1 USES NAME2\n
	 * </code>
	 * @throws IOException
	 */
	private void write() throws IOException {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> e : map.entrySet()) {
			sb.append(String.format("%s %s\n", e.getValue(), e.getKey()));
		}
		for (Map.Entry<String, List<String>> e : includes.entrySet()) {
			for (String s : e.getValue()) {
				sb.append(String.format("%s USES %s\n", e.getKey(), s));
			}
		}
		IOWrite_Stats.writeString(_hashFile, sb.toString(), false);
	}
	
	private void read() {
		try {
			StringBuffer sb = IORead_Stats.readSBuffer(_hashFile);
			BufferedReader br = new BufferedReader(new StringReader(sb.toString()));
			String line;
			while ((line = br.readLine()) != null) {
				
				String[] entry;
				if ( (entry = line.split(" USES ")).length == 2 ) {
					entry[0] = entry[0].trim();
					entry[1] = entry[1].trim();
					if (entry[0].length() <= 0 || entry[1].length() <= 0) { continue; }
					if (includes.containsKey(entry[0]) && !includes.get(entry[0]).contains(entry[1])) {
						includes.get(entry[0]).add(entry[1]);
					} else {
						ArrayList<String> list = new ArrayList<String>();
						list.add(entry[1]);
						includes.put(entry[0], list);
					}
				} else if ( (entry = line.split(" ")).length == 2 ) {
					entry[0] = entry[0].trim();
					entry[1] = entry[1].trim();
					if (entry[0].length() <= 0 || entry[1].length() <= 0) { continue; }
					map.put(entry[1], entry[0]);
				}
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
