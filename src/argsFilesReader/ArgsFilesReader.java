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

package src.argsFilesReader;

import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import src.Constants;
import src.Wiki2xhtmlArgsParser;
import src.Constants.FileArgs;
import src.project.WikiProject;
import src.project.WikiProject.InvalidLocationException;
import src.project.file.LocalWikiFile;
import src.utilities.IORead_Stats;

public class ArgsFilesReader {
	
	/**
	 * <p>Reads settings from a file.</p>
	 * <dl>
	 * <dt>Comments</dt><dd>Start with a <code>#</code> as first character on the line.
	 * <dt>Arguments</dt><dd>Line starting with <code>args=</code></dd>
	 * <dt>Source directory</dt><dd>Line starting with <code>dir=</code>.
	 * Attention: Setting the source directory does not work for the files listed in 
	 * this config file. The <code>dir=</code> is valid for all files below 
	 * util a new <code>dir=</code> entry is met. Spaces can be escaped by preceding a backslash.</dd>
	 * <dt>Files</dt><dd>Any other line. The filename is the first entry in the line 
	 * (without spaces). Possible additional arguments: See {@link FileArgs}.</dd>
	 * <dt>Examples</dt><dd><code>secret.txt nositemap</dd><dd><code>index.txt</dd> 
	 * </dl>
	 * TODO Doc arg list
	 */
	public static void readArgsFile(WikiProject project, File file) throws IllegalOptionValueException, UnknownOptionException {
		String args = "";
		String dir = "";
		boolean sitemap;
		
		try {
			StringBuffer content = IORead_Stats.readSBuffer(file);
			BufferedReader b = new BufferedReader(new StringReader(content.toString()));
			String line;
			String s;
			File fDir;
			
			System.out.printf("Reading args file %s.\n", file);
			
			for (line = b.readLine(); line != null; line = b.readLine()) {
				if (line.startsWith("#")) {
					// Comment.
					continue;
					
				} else if (line.trim().length() == 0) {
					// Empty line.
					continue;
					
				} else if (line.startsWith("args=")) {
					args += " " + line.substring("args=".length());
					new Wiki2xhtmlArgsParser().readArguments(project, line.substring("args=".length()).split("\\s+"), file.getParentFile());
					System.out.printf("Arguments read: %s\n", args);
					
				} else if (line.startsWith("dir=")) {
					// Only allow files in sub-directories.
					dir = new String();
					fDir = new File(project.projectDirectory().getAbsolutePath() + File.separator + line.substring("dir=".length()));
					if (fDir.exists() && fDir.isDirectory()) {
						if (fDir.getCanonicalPath().startsWith(project.projectDirectory().getCanonicalPath())) {
							dir = fDir.getCanonicalPath().substring(project.projectDirectory().getCanonicalPath().length());
						}
					}
					System.out.printf("Directory set to %s (argument was: %s)", dir, line);
					
				} else {
					// File.
//					System.out.printf("File read: %s\n", line);
					sitemap = true;
					
					// Split at spaces, except when preceded by a \
					// TODO Doc: \ for spaces, noparse removed
					String[] largs = line.split("(?<!\\\\)\\s", 2);
					if (largs.length > 0) {
						s = ((dir.length() > 0) ? dir + File.separator : "") + largs[0].replace("\\ ", " ");
//						System.out.printf("File is %s\n", s);
						if (largs.length > 1) {
							if (largs[1].contains(Constants.FileArgs.noSitemap)) {
								sitemap = false;
								System.out.printf("No sitemap entry for %s\n", s);
							}
						}
						try {
							project.addFile(new LocalWikiFile(project, s, sitemap));
//							System.out.printf("Added %s to the project.\n", s);
						} catch (InvalidLocationException e) {
							System.out.printf("Could not add %s to the project: %s\n", s, e.getMessage());
						}
					}
				}
			}
			
		} catch (IOException e) {
			System.err.println("Could not read file " + file.getAbsolutePath());
		}
		
	}

}
