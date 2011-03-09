package src.programSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map.Entry;

import src.Constants;
import src.utilities.IORead_Stats;
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
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * Reads and writes program settings.
 */
final class ProgramSettingsWriter {


	/**
	 * Reads the program configuration
	 */
	public static boolean readSettings(ProgramSettings ps) {

		if (Constants.Files.settings.isDirectory())
			return false;

		if (!Constants.Files.settings.exists()) {
			try {
				IOWrite_Stats.createIfNotExists(Constants.Files.settings);
			} catch (IOException e) {
				return false;
			}
		}

		try {

			StringBuffer content = IORead_Stats.readSBuffer(Constants.Files.settings);
			BufferedReader br = new BufferedReader(new StringReader(content.toString()));
			String line;

			while ((line = br.readLine()) != null) {

				for (String key : ps.settings.keySet()) {
					if (line.startsWith(key)) {
						if (line.indexOf('=') > 0) {
							ps.settings.put(key, line.substring(line.indexOf('=')+1).trim());
						} else
							ps.settings.put(key, "true");
						break;
					}
				}
			}

		} catch (IOException e) {

			return false;
		}

		return true;

	}

	/**
	 * Writes the program configuration
	 */
	public static boolean writeSettings(ProgramSettings ps) {

		try {

			IOWrite_Stats.createIfNotExists(Constants.Files.settings);
			StringBuilder out = new StringBuilder();

			for (Entry<String, String> e : ps.settings.entrySet()) {
				out.append(e.getKey() + " = " + e.getValue());
				out.append('\n');
			}

			IOWrite_Stats.writeString(Constants.Files.settings, out.toString(), false);

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

}
