package src.ptm;

import java.util.List;

import src.ptm.PTM.PTMObjects;

public abstract class PTMNode extends PTMObject {
	
	public PTMNode(StringBuffer content, int beginIndex, PTMObject parent) {
		super(content, beginIndex, parent);
	}
	
	abstract public List<PTMObjects> getAllowedChildnodes();
	
}
