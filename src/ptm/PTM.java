package src.ptm;

import java.util.ArrayList;
import java.util.List;


/*
 *   Copyright (C) 2010 Simon Eugster <granjow@users.sf.net>

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

/**
 * <p>This is the <b>Advanced Template and Parser Function Manager.</b></p>
 * <p>PTM builds a tree of the input text and then continuously parses it.
 */
public class PTM {

	public static enum PTMObjects {
		Text,
		Function,
		FunctionIf,
		Parameter,
		Argument
	}
	
	/** Aborts only at the end of the file */
	public static final AbortFunction eofAbortFunction;
	public static final List<PTMObjects> defaultChildren;
	
	static {
		 eofAbortFunction = createAbortFunction(new ArrayList<String>());
		 defaultChildren = new ArrayList<PTMObjects>();
		 for (PTMObjects o : PTMObjects.values()) {
			 if (o != PTMObjects.Argument) {
				 defaultChildren.add(o);
			 }
		 }
	}
	
	/** 
	 * Creates an abort function. It aborts on all given terminators or when the end of the file is reached.
	 */
	public static final AbortFunction createAbortFunction(final List<String> terminators) {
		AbortFunction f = new AbortFunction() {
			public boolean abort(StringBuffer content, int index) {
				boolean abort = false;
				if (index >= content.length()) {
					abort = true;
				} else {
					for (String s : terminators) {
						try {
							if (s.equals(content.substring(index, index+s.length()))) {
								abort = true;
								break;
							}
						} catch (StringIndexOutOfBoundsException e) { }
					}
				}
				return abort;
			}
		};
		return f;
	}
	
}
