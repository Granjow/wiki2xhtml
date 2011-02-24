package src.ptm;

import java.io.PrintStream;

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
 * PTM «Tree Object» for creating a parse tree.
 */
abstract public class PTMObject {

	
	protected final PTMNode parent;
	protected final PTMRootNode root;
	protected final PTMArrayList childTree;
	protected final StringBuffer content;
	protected final int beginIndex;
	protected int endIndex;
	
	abstract public String evaluate() throws RecursionException;
	
	/** Variables initialization */
	public PTMObject(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) {
		this.childTree = new PTMArrayList();
		this.beginIndex = beginIndex;
		this.content = content;
		this.parent = parent;
		this.root = root;
		
		if (content == null) {
			throw new NullPointerException("Content must not be null");
		}
		if (content.length() <= 0) {
			throw new IndexOutOfBoundsException(String.format("Template file: Length must be > 0 (is %s).", content.length()));
		}
		if (beginIndex < 0 || beginIndex >= content.length()) {
			throw new IndexOutOfBoundsException("Index must be > 0 and < content length; is " + beginIndex);
		}
	}
	
	public static final class ObjectNotApplicableException extends Exception {
		private static final long serialVersionUID = 1L;
		public ObjectNotApplicableException(String msg) {
			super(msg);
			// TODO fetch error output?
			System.out.println(msg);
		}
	}
	
	public static final class EndOfExpressionReachedException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	public static final class RecursionException extends Exception {
		private static final long serialVersionUID = 1L;
		public RecursionException(String msg) {
			super(msg);
			System.err.println(msg);
		}
	}
	
	/**
	 * @return This object's raw content, including all special characters. 
	 * Example: <code>{{#if:a|A}}</code> for a parser function. 
	 */
	public final String getRawContent() {
		return content.substring(beginIndex, endIndex);
	}
	
	/**
	 * @param depth Which parent to get. 0 is the current element, 1 is this element's parent, 
	 * 2 is the grand-parent, and so on.
	 * @return The <code>depth</code>-th parent of this object, if available, or <code>null</code> otherwise.
	 */
	public final PTMObject getParent(int depth) {
		PTMObject parent = this;
		try {
			for (int i = 0; i < depth; i++) {
				parent = parent.parent;
			}
		} catch (NullPointerException e) {}
		return parent;
	}
	
	/**
	 * Prints a tree of the object and all its child nodes.
	 * @param prefix May initially be null.
	 */
	public final void printTree(PrintStream ps, String prefix) {
		final int max = 50;
		final String suffix = " [...]";
		
		if (prefix == null || "".equals(prefix)) {
			prefix = "|-";
		} else {
			prefix = "| " + prefix;
		}
		String content = getRawContent();
		if (content.length() > max + suffix.length()) {
			content = content.substring(0, max) + " [...]";
		}
		content = content.replace("\n", "");
		ps.printf("%s%s: %s\n", prefix, this.getClass(), content);
		for (PTMObject o : childTree) {
			o.printTree(ps, prefix);
		}
	}
	
}
