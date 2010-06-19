package src.ptm;

public class PTMTextLeaf extends PTMLeaf {

	public PTMTextLeaf(StringBuffer content, int beginIndex, PTMObject parent) throws ObjectNotApplicableException {
		super(content, beginIndex, parent);
		endIndex = beginIndex+1;
		if (endIndex > content.length()) {
			throw new ObjectNotApplicableException("End reached!");
		}
	}
	
	public String getContent() {
		return content.substring(beginIndex, endIndex);
	}
	
	/**
	 * Appends another text leaf. The condition for appending 
	 * is that <code>other</code> starts where <b><code>this</code></b> ends,
	 * as both work on the same StringBuffer.
	 * @return false if start and end point did not match
	 */
	public boolean append(PTMTextLeaf other) {
		if (endIndex == other.beginIndex) {
			System.out.printf("Updating text end from %d to %d\n", endIndex, other.endIndex);
			endIndex = other.endIndex;
			return true;
		}
		return false;
	}

}
