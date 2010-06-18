package src.ptm;

public class PTMTemplateNode extends PTMNode {

	
	public PTMTemplateNode(StringBuffer content, int beginIndex) throws ObjectNotApplicableException {
		super(content, beginIndex);
		throw new ObjectNotApplicableException("Template Node");
	}

	public boolean applies(StringBuffer content, int index) {
		return false;
	}

}
