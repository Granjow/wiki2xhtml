package src.ptm;

public class PTMArgumentNameNode extends PTMNode {
	
	public static final char separator = '=';
	private String implicitName = null;

	public PTMArgumentNameNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root, final AbortFunction abortParent) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);
		AbortFunction abort = new AbortFunction() {
			public boolean abort(StringBuffer content, int index) {
				return abortParent.abort(content, index) || content.charAt(index) == separator;
			}
		};
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
		
		assert endIndex >= this.beginIndex;
	}
	
	/**
	 * Creates a node with an implicit name (if no name explicitly given with <code>name=value</code>).
	 */
	public PTMArgumentNameNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root, String name) {
		super(content, beginIndex, parent, root);
		endIndex = this.beginIndex;
		implicitName = name;
	}
	
	public PTMArgumentValueNode toPTMArgumentValueNode() {
		return new PTMArgumentValueNode(content, beginIndex, endIndex, parent, root, childTree);
	}
	

	public String evaluate() {
		if (implicitName != null) {
			return implicitName;
		} else {
			StringBuilder sb = new StringBuilder();
			for (PTMObject o : childTree) {
				sb.append(o.evaluate());
			}
			return sb.toString();
		}
	}

}
