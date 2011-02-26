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

package src.project.file;

import java.io.File;
import java.io.IOException;

import src.project.WikiProject;
import src.utilities.IORead_Stats;
import src.utilities.IOWrite_Stats;

/**
 * Represents a file that really exists on the file system. Input and output files are generated from the 
 * file name, the project (input) directory, and the project output directory.
 */
public class LocalWikiFile extends WikiFile {

	private final File fSrc;
	private final File fDest;
	
	/**
	 * @return <p>The {@link WikiFile#internalName()} of the local file.
	 * <code>.txt</code> files will be renamed to <code>.html</code> if they are parsed.</p>
	 * <p>Example: {@code my\file.txt} results in {@code my/file.html}</p>
	 */
	public final String internalName() {
		return buildInternalName(super.internalName());
	}
	private final String buildInternalName(String name) {
		String destName = name;
		if (parse && name.endsWith(".txt")) {
			destName = destName.substring(0, destName.length()-".txt".length()) + ".html";
		}
		return destName;
	}

	/** Creates a new WikiFile. Input and output files are generated automatically.
	 * Does NOT check whether the location is valid. (E.g. input file equals output file.)*/
	public LocalWikiFile(WikiProject project, String name, boolean sitemap, boolean parse) {
		this(project, name, sitemap, parse, false);
	}
	public LocalWikiFile(WikiProject project, String name, boolean sitemap, boolean parse, boolean createTempFile) {
		super(project, name, sitemap, parse);
		if (createTempFile) {
			File f1 = null, f2 = null;
			try {
				f1 = File.createTempFile("wiki2xhtml", "temp");
				f2 = File.createTempFile("wiki2xhtml", "temp");
			} catch (IOException e) {
				e.printStackTrace();
			}
			fSrc = f1;
			fDest = f2;
			fSrc.deleteOnExit();
			fDest.deleteOnExit();
		} else {
			fSrc = new File(project.projectDirectory().getAbsolutePath() + File.separator + name);
			String destName = buildInternalName(name);
			fDest = new File(project.outputDirectory().getAbsolutePath() + File.separator + destName);
			
			System.out.printf("Input file: %s -- Output file: %s\n", fSrc.getAbsolutePath(), fDest.getAbsolutePath());
		}
	}
	
	public boolean validLocation() {
		try {
			return fSrc.getCanonicalPath().startsWith(project.projectDirectory().getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/** Needs to be overridden for comparing. */
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj instanceof LocalWikiFile) {
			LocalWikiFile wf = (LocalWikiFile) obj;
			return fSrc.equals(wf.fSrc) && fDest.equals(wf.fDest);
		}
		return false;
	}
	public StringBuffer getContent() {
		if (!alreadyRead) {
			try {
				content = IORead_Stats.readSBuffer(fSrc);
				alreadyRead = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content; 
	}
	
	public void write() {
		try {
			IOWrite_Stats.writeString(fDest, getContent().toString(), false);
		} catch (IOException e) {}
	}
}
