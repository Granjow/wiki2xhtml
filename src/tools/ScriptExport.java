package src.tools;

import java.io.File;
import java.io.IOException;

import src.Args;
import src.Constants;
import src.Handler_Arguments;
import src.Container_Files;
import src.Args.GetPolicyE;
import src.commentator.Logger;
import src.utilities.IORead_Stats;
import src.utilities.IOUtils;
import src.utilities.IOWrite_Stats;


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
 * Read/write config/script files files
 *
 * @author Simon Eugster
 */
public class ScriptExport {

	
	/**
	 * Describes the file type to write
	 * @since wiki2xhtml 3.4
	 */
	public static enum Filetype {
		wx ("wx", ""),
		sh ("sh", "#!/bin/sh\n"),
		bat ("bat", "");

		public final String extension;
		public final String prefix;
		Filetype(String extension, String prefix) {
			this.extension = extension;
			this.prefix = prefix;
		}
	}


	/**
	 * Writes various files (settings and scripts)
	 *
	 * @param filetype - The file type. Possible is .wx, .sh and .bat.
	 */
	public static void writeFile(Filetype filetype) {
		StringBuffer arguments = Container_Files.getInstance().allArguments().getArgs(GetPolicyE.AllArgs).append(Container_Files.getInstance().fileList().getArgs(GetPolicyE.AllArgs));
		if (filetype == Filetype.wx) {
			File f = IOUtils.openOutFileDialog(filetype.extension, new File(Constants.Directories.workingDir), null, true);
			if (f != null && !f.isDirectory()) {
				try {
					IOWrite_Stats.writeString(f, arguments.toString(), false);
				} catch (IOException e) {
					Logger.getInstance().log.append(e);
					e.printStackTrace();
				}
			}

		} else if (filetype == Filetype.sh || filetype == Filetype.bat) {
			File f = IOUtils.openOutFileDialog(filetype.extension, new File(Constants.Directories.workingDir), null, true);
			if (f != null && !f.isDirectory()) {
				try {
					IOWrite_Stats.writeString(f, filetype.prefix + "java -jar wiki2xhtml.jar " + arguments, false);
				} catch (IOException e) {
					Logger.getInstance().log.append(e);
					e.printStackTrace();
				}
			}
		}
	}



	public static void readFile() {
		File f = IOUtils.openInFileDialog("wx", new File(Constants.Directories.workingDir), null, false);
		if (f != null && f.exists() && f.canRead() && f.isFile()) {
			System.out.println(f.getPath());
			StringBuffer args = new StringBuffer();
			try {
				args = IORead_Stats.readSBuffer(f);
			} catch (IOException e) {
				Logger.getInstance().log.append(e);
				e.printStackTrace();
			}

			Handler_Arguments.handleCommandLineArguments(new Args(Handler_Arguments.handleReplaceArguments(args.toString())), false);
		}
	}
}
