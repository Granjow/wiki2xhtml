package src.ptm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import src.ptm.PTMObject.RecursionException;


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
		FunctionIfeq,
		FunctionIfvalexists,
		FunctionSwitch,
		Parameter,
		Template,
		Argument,
		ArgumentName,
		ArgumentValue
	}

	public static final int recursionMaxDepth = 50;
	public static final String recursionKey = "recursionLevel";
	
	/** Aborts only at the end of the file */
	public static final AbortFunction eofAbortFunction;
	/** Without argument nodes */
	public static final List<PTMObjects> defaultChildren;
	
	static {
		eofAbortFunction = createAbortFunction(new ArrayList<String>());
		defaultChildren = new ArrayList<PTMObjects>();
		for (PTMObjects o : new PTMObjects[] { PTMObjects.Text,
				PTMObjects.Function, PTMObjects.Parameter, PTMObjects.Template }) {
			defaultChildren.add(o);
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
	
	/** Applies all parser functions to the input string 
	 * @throws RecursionException */
	public static final String parse(StringBuffer content, File templateDirectory) throws RecursionException {
		if (content.length() > 0) {
			PTMRootNode root = new PTMRootNode(content, new PTMState());
			root.setTemplateDirectory(templateDirectory);
			return root.evaluate();
		} else {
			return new String();
		}
	}
	
	/** Applies all parser functions to the input string, using the given start state. 
	 * @throws RecursionException */
	public static final String parse(StringBuffer content, PTMState sigma) throws RecursionException {
		PTMRootNode root = new PTMRootNode(content, sigma);
		return root.evaluate();
	}
	
}
