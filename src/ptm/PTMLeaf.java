package src.ptm;

abstract public class PTMLeaf extends PTMObject {

	public final StringBuffer content;
	protected final int beginIndex;
	protected int endIndex;
	
	public PTMLeaf(StringBuffer content, int beginIndex) {
		this.content = content;
		this.beginIndex = beginIndex;
	}
	
}
