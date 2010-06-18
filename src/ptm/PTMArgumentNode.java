package src.ptm;

public class PTMArgumentNode extends PTMNode {
	
	public PTMArgumentNode(StringBuffer content, int beginIndex) {
		super(content, beginIndex);
	}

	private static final char identifier = '|';
	
	public boolean applies(StringBuffer content, int index) {
		try {
			return identifier == content.charAt(index);
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

}
