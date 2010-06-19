package src.ptm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.ptm.PTM.PTMObjects;

public class PTMFunctionIf extends PTMFunctionNode {
	
	public static final Pattern startPattern = Pattern.compile("^\\{\\{\\s*#if\\s*:");
	
	private static final List<PTMObjects> allowedChildnodes;
	
	static {
		allowedChildnodes = new ArrayList<PTMObjects>();
		allowedChildnodes.add(PTMObjects.Argument);
	}
	
	public final List<PTMObjects> getAllowedChildnodes() { return allowedChildnodes; }
	public final Pattern startPattern() { return startPattern; }

	public PTMFunctionIf(StringBuffer content, int beginIndex, PTMObject parent) throws ObjectNotApplicableException {
		super(content, beginIndex, parent);
		
		// Find the end of the starting {{#if: expression (may contain spaces) to set the endIndex appropriately.
		// Then try to add more objects until the end of the function is reached. 
		
		Matcher m = startPattern.matcher(PTMObjectFactory.getIndicator(content, beginIndex));
		if (m.find()) {
			endIndex = beginIndex + m.end();
		} else {
			throw new ObjectNotApplicableException("Lost position of the start pattern!");
		}
		
		boolean functionEndReached = false;
		PTMObject obj; 
		do {
			try {
				obj = PTMObjectFactory.buildObject(content, endIndex, this, PTMFunctionNode.abort, allowedChildnodes);
				if (obj != null) {
					childTree.add(obj);
					endIndex = obj.endIndex;
					System.out.printf("If: Object %s added. Goes from %d to %d with content >>%s<<.\n", obj, obj.beginIndex, obj.endIndex, obj.getRawContent());
				}
			} catch (EndOfExpressionReachedException e) {
				// While parsing we reached the end of the if function.
				// This is a «good» exception as it tells us that the function can indeed end correctly.
				// As this still includes the case that we just reached the end of the file, we need to
				// consider this case below.
				functionEndReached = true;
				break;
			}
		} while (obj != null);
		
		if (!functionEndReached) {
			throw new ObjectNotApplicableException("End of the expression could not be found.");
		}
		
		// Check that we're not at the EOF but really at the end of a function
		// by looking for the terminating }}.
		try {
			if (PTMFunctionNode.endExpression.equals(content.substring(endIndex, endIndex+PTMFunctionNode.endExpression.length()))) {
				functionEndReached = true;
				endIndex += PTMFunctionNode.endExpression.length();
			}
		} catch (StringIndexOutOfBoundsException e) {}
		if (!functionEndReached) {
			throw new ObjectNotApplicableException("End of the If expression could not be located.");
		}
	}
	
	
	public static void main(String[] args) throws ObjectNotApplicableException {
		StringBuffer sb = new StringBuffer("Hallo {{#if: .");
		PTMFunctionIf f;
		for (int i = 0; i <= sb.length()+1; i++) {
			try {
				f = new PTMFunctionIf(sb, i, null);
				System.out.printf("Applies at %d! --->%s<---\n", i, f.getRawContent());
			} catch (ObjectNotApplicableException e) {}
		}
	}
}
