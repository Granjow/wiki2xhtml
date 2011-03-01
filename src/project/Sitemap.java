/*
 *   Copyright (C) 2011 Simon A. Eugster <simon.eu@gmail.com>

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


package src.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import src.utilities.IORead_Stats;
import src.utilities.IOWrite_Stats;

/**
 * Sitemap builder. Creates text files (not XML). 
 * See <a href="http://sitemaps.org/">sitemaps.org</a> for details.
 * TODO 0 doc Sitemaps
 */
public class Sitemap {
	
	/**
	 * @param sitemapFile Target file for the sitemap. If it is set to {@code null}, then the methods on this object don't do anything.
	 * @param prefix URL to prefix (e.g. http://example.org)
	 * @param append If {@code true}, then an existing sitemap is read and new entries are added.
	 */
	public Sitemap(File sitemapFile, String prefix, boolean append) {
		if (!prefix.endsWith("/")) {
			prefix += "/"; 
		}
		
		_prefix = prefix;
		_sitemapFile = sitemapFile;
		_append = append;
	}
	
	public void add(String relativePath) {
		if (_sitemapFile == null) { return; }
		readList();
		_sitemapList.add(_prefix + relativePath);
	}
	
	public void write() throws IOException {
		if (_sitemapFile == null) { return; }
		
		int counter = 0;
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = _sitemapList.iterator();
		while (it.hasNext()) {
			sb.append(it.next() + "\n");
			counter++;
		}
		
		_sitemapFile.getAbsoluteFile().getParentFile().mkdirs();
		IOWrite_Stats.writeString(_sitemapFile, sb.toString(), false);
		
		System.out.printf("Sitemap written to %s (%s entries).\n", _sitemapFile.getAbsolutePath(), counter);
	}

	/** List of all files to include in the sitemap */
	private TreeSet<String> _sitemapList = null;
	private File _sitemapFile;
	private final String _prefix;
	private boolean _append;
	
	private void readList() {
		if (_sitemapFile == null) { return; }
		if (_sitemapList == null) {
			_sitemapList = new TreeSet<String>(pathComparator);
		}
		if (_append && _sitemapList != null && _sitemapFile.exists() && _sitemapFile.canRead() && _sitemapFile.isFile()) {
			try {
				BufferedReader br = new BufferedReader(new StringReader(IORead_Stats.readSBuilder(_sitemapFile).toString()));
				String line;
				while ((line = br.readLine()) != null) {
					if ((line = line.trim()).length() > 0) {
						_sitemapList.add(line);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static final Comparator<String> pathComparator = new Comparator<String>() {
		public int compare(String o1, String o2) {
			int n1 = count(o1);
			int n2 = count(o2);
			
			if (n1 != n2) {
				return n1 - n2;
			}
			n1 = o1.length();
			n2 = o2.length();
			if (n1 != n2) {
				return n1 - n2;
			}
			return o1.compareTo(o2);
		}
		
		private final int count(String s) {
			int n = 0;
			int i = 0;
			while (s.indexOf('/', i) >= 0) {
				i++; n++;
			}
			return n;
		}
	};
}
