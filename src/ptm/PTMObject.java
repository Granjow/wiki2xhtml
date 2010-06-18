package src.ptm;

abstract public class PTMObject {
	
	/**
	 * Tests whether the characters starting at <code>index</code> match
	 * the start characters of this object.
	 * @return true if it is worth trying to build this object from the 
	 * current start position. Might still fail! (e.g. when the end characters
	 * do not match) 
	 */
	abstract public boolean applies(StringBuffer content, int index);
	
	public class ObjectNotApplicableException extends Exception {
		private static final long serialVersionUID = 1L;
		public ObjectNotApplicableException(String msg) { super(msg); }
	}
	
}
