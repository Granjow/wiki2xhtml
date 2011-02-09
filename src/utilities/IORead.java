package src.utilities;

import java.io.*;
import java.net.URL;


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
 * Reading utilities
 *
 * @author Simon Eugster
 * ,		hb9eia
 */
public class IORead {

	/**
	 * Reads a File into a StringBuffer.
	 * @param f - Input File
	 * @return File in StringBuffer
	 */
	public static StringBuffer readSBuffer (File f) throws IOException {
		return new StringBuffer(readSBuilder(f));
	}
	public static StringBuilder readSBuilder (File f) throws IOException {

		FileInputStream fis = null;
		StringBuilder sb = new StringBuilder();

		try {
			fis = new FileInputStream(f);
			byte[] buffer = new byte [8 * 21000];
			int bytes = 0;

			while ((bytes = fis.read(buffer)) != -1) {	//Read until eof
				sb.append(new String(buffer, 0, bytes));
			}
		}

		finally {
			if (fis != null) {	//Close File
				try {
					fis.close();
				} catch (IOException e) {
					System.err.println(e);
				}
				fis = null;
			}
		}

		return sb;
	}
	
	public static StringBuffer readSBuffer(URL url) throws IOException {
		InputStream fis = null;
		StringBuffer sb = new StringBuffer();
		try {
			fis = url.openStream();
			byte[] buffer = new byte [8 * 21000];
			int bytes = 0;

			while ((bytes = fis.read(buffer)) != -1) {	//Read until EOF
				sb.append(new String(buffer, 0, bytes));
			}
		}
		finally {
			if (fis != null) {	//Close File
				try {
					fis.close();
				} catch (IOException e) {
					System.err.println(e);
				}
				fis = null;
			}
		}
		return sb;
	}

	/**
	 * Reads a (text) file from the current jar file.
	 * @param filename - The path to the file
	 * @return The file as a StringBuffer
	 */
	public static StringBuffer readFromJar(String filename) {
		/* Get the file as InputStream */
		IORead ir = new IORead();
		InputStream is = ir.getClass().getResourceAsStream(filename);

		/* Make a BufferedReader from the InputStream */
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();

		/* Read the StringBuffer */
		String line = new String();
		try {
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) {
					sb.append('\n');
				}
				sb.append(line + '\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb;
	}

	/**
	 * Reads a line with readLine() from the command line.
	 */
	public static BufferedReader readCmdLine = new BufferedReader(new InputStreamReader(System.in));
}
