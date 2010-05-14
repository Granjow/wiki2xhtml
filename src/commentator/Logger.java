package src.commentator;

import java.io.PrintStream;
import java.util.ArrayList;

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

public class Logger {
	
	public static final class PrintScope {
		public static final int OUT = 1;
		public static final int ERR = 2;
		public static final int VERBOSE = 4;
	}

	private static ArrayList<ScopedPrintStream> outs = new ArrayList<ScopedPrintStream>();

	public static void attachOutput(PrintStream stream, int scope) {
		outs.add(new ScopedPrintStream(stream, scope));
	}
	
	public static void print(Object obj) {
		for (ScopedPrintStream sps : outs) {
			sps.ps.print(obj);
		}
	}
	public static void print(Object obj, int scope) {
		for (ScopedPrintStream sps : outs) {
			if ((sps.scope & scope) != 0) {
				sps.ps.print(obj);
			}
		}
	}
	public static void println(Object obj) {
		for (ScopedPrintStream sps : outs) {
			sps.ps.println(obj);
		}
	}
	public static void println(Object obj, int scope) {
		for (ScopedPrintStream sps : outs) {
			if ((sps.scope & scope) != 0) {
				sps.ps.println(obj);
			}
		}
	}
	public static void printf(String obj, Object... args) {
		for (ScopedPrintStream sps : outs) {
			sps.ps.printf(obj, args);
		}
	}
	public static void println(String obj, int scope, Object... args) {
		for (ScopedPrintStream sps : outs) {
			if ((sps.scope & scope) != 0) {
				sps.ps.printf(obj, args);
			}
		}
	}
	
	
	private static class ScopedPrintStream {
		public PrintStream ps;
		public int scope;
		public ScopedPrintStream(PrintStream ps, int scope) {
			this.ps = ps; this.scope = scope;
		}
	}
	
}
