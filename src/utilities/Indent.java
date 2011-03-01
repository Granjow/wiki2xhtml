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

package src.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class Indent {

	/**
	 * <p>Puts the input text in two rows with a minimum distance between.</p>
	 * <p>Rows are switched with the {@code \t} character (Tabulator).</p>
	 */
	public static final String indent(final String in, final int distance) {
		assert distance >= 0;
		
		int maxLength = 0;
		
		ArrayList<String[]> lines = new ArrayList<String[]>();
		BufferedReader br = new BufferedReader(new StringReader(in));
		
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] cols = line.split("\\t", 2);
				if (cols.length > 1 && cols[0].length() > maxLength) { maxLength = cols[0].length(); }
				lines.add(cols);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return in;
		}
		

		StringBuilder out = new StringBuilder();
		
		maxLength += distance;
		
		for (int i = 0; i < maxLength; i++) {
			out.append(" ");
		}
		String spaces = out.toString();
		
		out.setLength(0);
		for (String[] cols : lines) {
			out.append(cols[0]);
			if (cols.length > 1) {
				out.append(spaces.substring(spaces.length() - (maxLength - cols[0].length())));
				out.append(cols[1]);
			}
			out.append("\n");
		}
		
		return out.toString();
	}
	
	public static void main(String[] args) {
		String s = "Some help text:\n--help\tDisplay help\n-h\tSame\n--incremental\tBuild incrementally\n\tEtc.";
		System.out.println(indent(s, 3));
	}
	
}
