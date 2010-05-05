package src.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.ConstantTexts;
import src.Constants;
import src.commentator.CommentAtor;
import src.commentator.Logger;
import src.commentator.CommentAtor.CALevel;
import src.utilities.IORead_Stats;
import src.utilities.IOUtils;
import src.utilities.IOWrite_Stats;


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
 *
 * Converts an old menu file into a «new» one
 *
 * @since wiki2xhtml 3.3
 *
 * @author Simon Eugster
 */
public class UpdateMenufile {

	File menu = null;

	/**
	 * @param file The old menu file to update
	 */
	public static boolean run(String file, boolean exitOnClose) {
		File f;

		/*
		 * Get file
		 */
		if (file == null || file.length() == 0) {
			f = IOUtils.openInFileDialog("", new File(Constants.Directories.workingDir), ConstantTexts.NounCap.menuFile, false);
			if (f == null) {
				if (exitOnClose)
					System.exit(0);
				return false;
			}
		} else
			f = new File(file);

		/*
		 * Check permissions
		 */
		{
			boolean e, i = false, r = false, w = false;
			if (!(e = f.exists()) | !(i = f.isFile()) | !(r = f.canRead()) | !(w = f.canWrite())) {
				System.err.println("Error. exists: " + e + ", is file: " + i + "; read: " + r + "; write: " + w);
				System.exit(1);
			}
		}

		BufferedReader br;
		try {
			br = new BufferedReader(new StringReader(IORead_Stats.readSBuffer(f).toString()));

			StringBuffer out = new StringBuffer();

			String line;
			Pattern p = Pattern.compile("^\\**");
			Matcher m;
			boolean b = false;
			String stars = "";
			String link = "";

			try {
				while ((line = br.readLine()) != null) {
					if (b) {
						out.append(stars + ' ' + line + ", " + link + '\n');
					} else {
						m = p.matcher(line);
						m.find();
						stars = "*" + m.group();
						link = line.substring(m.end());
					}
					b = !b;
				}

				br.close();

			} catch (IOException e) {
				e.printStackTrace();
				Logger.getInstance().log.append(e);
			}

			File updatedFile = new File(f.getPath() + "~old");

			IOUtils.copy(f, updatedFile);

			try {
				IOWrite_Stats.writeString(f, out.toString(), false);
				CommentAtor.getInstance().ol("Successfully written. Backup is " + updatedFile.getPath(), CALevel.MSG);

			} catch (IOException e) {
				Logger.getInstance().log.append(e);
				e.printStackTrace();
			}
		} catch (IOException e1) {
			Logger.getInstance().log.append(e1);
			e1.printStackTrace();
			return false;
		}

		return true;

	}

}
