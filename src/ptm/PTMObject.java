package src.ptm;

import java.util.ArrayList;

abstract public class PTMObject {

	
	protected final PTMObject parent;
	protected final ArrayList<PTMObject> childTree;
	protected final StringBuffer content;
	protected final int beginIndex;
	protected int endIndex;
	
	/** Variables initialization */
	public PTMObject(StringBuffer content, int beginIndex, PTMObject parent) {
		this.childTree = new ArrayList<PTMObject>();
		this.beginIndex = beginIndex;
		this.content = content;
		this.parent = parent;
		
		if (content == null) {
			throw new NullPointerException("Content must not be null");
		}
		if (beginIndex < 0 || beginIndex >= content.length()) {
			throw new IndexOutOfBoundsException("Index must be > 0 and < content length");
		}
	}
	
	/**
	 * Tests whether the characters starting at <code>index</code> match
	 * the start characters of this object.
	 * @return true if it is worth trying to build this object from the 
	 * current start position. Might still fail! (e.g. when the end characters
	 * do not match) 
	 */
	abstract public boolean applies(StringBuffer content, int index);
	
	public static final class ObjectNotApplicableException extends Exception {
		private static final long serialVersionUID = 1L;
		public ObjectNotApplicableException(String msg) { super(msg); }
	}
	
	public static final class EndOfExpressionReachedException extends Exception {
		private static final long serialVersionUID = 1L;
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
	
}
