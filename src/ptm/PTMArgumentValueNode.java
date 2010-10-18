package src.ptm;

public class PTMArgumentValueNode extends PTMNode {

	public PTMArgumentValueNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root, AbortFunction abort) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);

		endIndex = beginIndex;
		
		boolean nameEndReached = false;
		PTMObject obj;
		do {
			try {
				obj = PTMObjectFactory.buildObject(content, endIndex, this, root, abort, PTM.defaultChildren);
				if (obj != null) {
					childTree.add(obj);
					endIndex = obj.endIndex;
				}
			} catch (EndOfExpressionReachedException e) {
				nameEndReached = true;
				break;
			}
		} while (obj != null);
		if (!nameEndReached) {
			throw new ObjectNotApplicableException("No end of name in sight.");
		}
		
		assert endIndex > this.beginIndex;
	}
	
	public PTMArgumentValueNode(StringBuffer content, int beginIndex, int endIndex, PTMNode parent, PTMRootNode root, PTMArrayList children) {
		super(content, beginIndex, parent, root);
		this.endIndex = endIndex;
		if (children != null) childTree.addAll(children);
		assert this.beginIndex <= this.endIndex;
	}

	public String evaluate() {
		//TODO
		if (childTree.size() == 1) {
			return childTree.get(0).evaluate();
		}
		return getRawContent();
	}

}
