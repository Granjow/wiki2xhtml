package src.ptm;

import java.util.ArrayList;
import java.util.List;

import src.ptm.PTM.PTMObjects;

public class PTMArgumentNode extends PTMNode {
	
	private static final char identifier = '|';
	private static final List<PTMObjects> allowedChildnodes;
	
	static {
		allowedChildnodes = new ArrayList<PTMObjects>();
		for (PTMObjects o : PTMObjects.values()) {
			if (o != PTMObjects.Argument) allowedChildnodes.add(o);
		}
	}
	
	public PTMArgumentNode(StringBuffer content, int beginIndex, PTMObject parent) throws ObjectNotApplicableException {
		super(content, beginIndex, parent);
		
		// Build the abort function. Abort at | always (new argument),
		// and at either }}} (parameter) or }} (template/function).
		ArrayList<String> terminators = new ArrayList<String>();
		terminators.add("|");
		if (parent instanceof PTMParameterNode) {
			terminators.add("}}}");
		} else {
			terminators.add("}}");
		}
		AbortFunction abort = PTM.createAbortFunction(terminators);
		
		endIndex = beginIndex;
		if (content.charAt(endIndex) == identifier) { endIndex++; }
		
		boolean endReached = false;
		PTMObject obj;
		while (true) {
			try {
				obj = PTMObjectFactory.buildObject(content, endIndex, this, abort, allowedChildnodes);
				if (obj != null) {
					childTree.add(obj);
					endIndex = obj.endIndex;
					System.out.printf("Argument: Object %s added. Goes from %d to %d with content >>%s<<.\n", obj, obj.beginIndex, obj.endIndex, obj.getRawContent());
				} else {
					break;
				}
			} catch (EndOfExpressionReachedException e) {
				endReached = true;
				break;
			}
		}
		if (!endReached) {
			throw new ObjectNotApplicableException("End of argument list could not be found");
		}
	}

	public List<PTMObjects> getAllowedChildnodes() {
		return allowedChildnodes;
	}
	
	public boolean applies(StringBuffer content, int index) {
		try {
			return identifier == content.charAt(index);
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

}
