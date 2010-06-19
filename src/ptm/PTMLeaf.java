package src.ptm;

abstract public class PTMLeaf extends PTMObject {
	
	abstract public String getContent();
	
	public PTMLeaf(StringBuffer content, int beginIndex) {
		super(content, beginIndex);
	}
	
}
