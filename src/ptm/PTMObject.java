package src.ptm;

import java.util.ArrayList;

abstract public class PTMObject {

	protected final ArrayList<PTMObject> childTree;
	protected final StringBuffer content;
	protected final int beginIndex;
	protected int endIndex;
	
	public PTMObject(StringBuffer content, int beginIndex) {
		this.childTree = new ArrayList<PTMObject>();
		this.beginIndex = beginIndex;
		this.content = content;
		
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
	
	public final String getRawContent() {
		return content.substring(beginIndex, endIndex);
	}
	
}
