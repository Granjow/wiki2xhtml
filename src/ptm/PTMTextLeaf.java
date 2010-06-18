package src.ptm;

public class PTMTextLeaf extends PTMLeaf {

	public PTMTextLeaf(StringBuffer content, int beginIndex) {
		super(content, beginIndex);
	}

	public boolean applies(StringBuffer content, int index) {
		return index < content.length();
	}

}
