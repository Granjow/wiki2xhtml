package src.ptm;

import java.util.HashMap;

public class PTMState extends HashMap<String, PTMArgumentValueNode> {
	private static final long serialVersionUID = 1L;

	public PTMState(PTMNode parent) {
		for (PTMObject o : parent.childTree) {
			if (o instanceof PTMArgumentNode) {
				put(o.childTree.get(0).evaluate(), (PTMArgumentValueNode)o.childTree.get(1));
			}
		}
	}
}
