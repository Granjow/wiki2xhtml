package src.utilities;

import java.io.File;
import java.io.IOException;

import src.Statistics;

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
 *
 * Various write utilities.
 * Created: 19.03.2005
 *
 * @author Simon Eugster
 * ,		hb9eia
 *
 */
public class IOWrite_Stats extends IOWrite {

	public static void writeString(File f, String s, boolean append) throws IOException {
		try {
			Statistics.getInstance().sw.timeWritingFiles.continueTime();
			IOWrite.writeString(f, s, append);
		} finally {
			Statistics.getInstance().sw.timeWritingFiles.stop();
		}
	}
}
