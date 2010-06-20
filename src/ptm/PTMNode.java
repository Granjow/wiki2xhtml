package src.ptm;

public abstract class PTMNode extends PTMObject {
	
	private int nextArgNumber = 1;
	
	public PTMNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) {
		super(content, beginIndex, parent, root);
	}
	
	/**
	 * <p>This method is a counter for arguments. Arguments without an explicit name binding (name=value)
	 * are numbered. MediaWiki behaviour is:</p>
	 * <code>arg1 | 3=arg3 | arg2</code>
	 */
	public String getNextArgNumber() {
		String next;
		boolean changed;
		
		do {
			changed = false;
			next = Integer.toString(nextArgNumber);
			for (PTMObject o : parent.childTree) {
				if (o instanceof PTMArgumentNode) {
					if (o.childTree.size() > 0) {
						if (o.childTree.get(0) instanceof PTMArgumentNameNode) {
							if (next.equals(o.childTree.get(0).evaluate())) {
								nextArgNumber++;
								changed = true;
							}
						}
					}
				}
			}
		} while (changed);
		nextArgNumber++;
		return next;
	}
	
}
