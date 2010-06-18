package src.ptm;

public class PTMParameterNode extends PTMNode {
	
	public PTMParameterNode(StringBuffer content, int beginIndex) {
		super(content, beginIndex);
	}

	private static final String identifier = "{{{";
	
	public boolean applies(StringBuffer content, int index) {
		try {
			return identifier.equals(content.substring(beginIndex, beginIndex+identifier.length()));
		} catch (StringIndexOutOfBoundsException e) {
			return false;
		}
	}
}
