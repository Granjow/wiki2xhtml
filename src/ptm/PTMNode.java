package src.ptm;

public abstract class PTMNode extends PTMObject {
	
	public final StringBuffer content;
	protected final int beginIndex;
	protected int endIndex;
	
	public PTMNode(StringBuffer content, int beginIndex) {
		this.content = content;
		this.beginIndex = beginIndex;
	}

}
