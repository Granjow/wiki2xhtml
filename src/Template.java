package src;

import java.io.File;
import java.io.IOException;

import src.utilities.IORead_Stats;
import src.utilities.StringTools;
import src.commentator.Logger;


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
 *
 * For the work with templates: Reading templates, replacing tags etc.
 *
 * @author Simon Eugster
 */
public class Template {

	private StringBuffer source = new StringBuffer();
	private StringBuffer output = new StringBuffer();
	public StringBuffer content = new StringBuffer();

	public Template() {	}

	public Template(StringBuffer alternative) {
		source = alternative;
	}

	public Template(File f, File f2) {
		readFile(f, f2);
	}

	public Template(File f, StringBuffer alternative) {
		readFile(f, alternative);
	}

	public void clear() {
		output.setLength(0);
		content.setLength(0);
	}

	private boolean readFile(File f, StringBuffer alternative) {
		boolean resource = false;

		if (f != null && f.exists() && f.isFile()) {
			try {
				source = IORead_Stats.readSBuffer(f);
			}
			catch (IOException e) {
				Logger.getInstance().log.append(e);
				e.printStackTrace();
			}
		} else
			resource = true;

		if (resource || source.length() == 0 && alternative != null) {
			source = alternative;
		} else
			return false;

		return true;
	}

	private boolean readFile(File f, File f2) {
		boolean resource = false;

		if (f != null && f.exists() && f.isFile()) {
			try {
				source = IORead_Stats.readSBuffer(f);
			}
			catch (IOException e) {
				Logger.getInstance().log.append(e);
				e.printStackTrace();
			}
		} else
			resource = true;

		if (resource || source.length() == 0 && f2 != null && f2.exists() && f2.isFile()) {
			try {
				source = IORead_Stats.readSBuffer(f2);
			} catch (IOException e) {
				Logger.getInstance().log.append(e);
				e.printStackTrace();
			}
		} else
			return false;

		return true;
	}

	public void replace(String search, String replace) {
		checkOutput();
		StringTools.replaceOnce(output, search, replace);
	}

	public void replaceAll(String search, String replace) {
		checkOutput();
		StringTools.replaceAll(output, search, replace);
	}

	public void replaceContent() {
		checkOutput();
		StringTools.replaceOnce(output, Constants.TemplateTags.content, content.toString());
	}

	public StringBuffer getOutput() {
		return output;
	}

	public void removeTag(String s) {
		checkOutput();
		StringTools.replaceOnce(output, s, "");
	}

	private void checkOutput() {
		if (output.length() == 0)
			output = new StringBuffer(source);
	}

}
