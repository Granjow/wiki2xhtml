package src.ptm;

import java.util.List;
import java.util.regex.Matcher;

import src.ptm.PTM.PTMObjects;
import src.ptm.PTMObject.EndOfExpressionReachedException;
import src.ptm.PTMObject.ObjectNotApplicableException;

public class PTMObjectFactory {


	public static final PTMObject buildObject(StringBuffer content, int index, PTMObject parent) {
		try {
			return buildObject(content, index, parent, PTM.eofAbortFunction);
		} catch (EndOfExpressionReachedException e) {
			return null;
		}
	}
	public static final PTMObject buildObject(StringBuffer content, int index, PTMObject parent, AbortFunction abort) throws EndOfExpressionReachedException {
		return buildObject(content, index, parent, abort, PTM.defaultChildren);
	}
	
	
	
	public static final PTMObject buildObject(StringBuffer content, int index, PTMObject parent, AbortFunction abort, List<PTMObjects> allowedChildnodes) throws EndOfExpressionReachedException {
		if (index >= content.length()) { 
			return null;
		}
		if (abort.abort(content, index)) {
			throw new EndOfExpressionReachedException();
		}

		String indicator = getIndicator(content, index);
		PTMObject obj = null;
		Matcher m;
		
		System.out.println(indicator);
		
		// Parser function: If
		if (allowedChildnodes.contains(PTMObjects.Function) || allowedChildnodes.contains(PTMObjects.FunctionIf)) {
			m = PTMFunctionIf.startPattern.matcher(indicator);
			if (m.find()) {
				try {
					obj = new PTMFunctionIf(content, index, parent);
				} catch (ObjectNotApplicableException e) {
					obj = null;
				}
			}
		}
		
		// Argument
		if (obj == null) {
			if (allowedChildnodes.contains(PTMObjects.Argument)) {
				// {{arg1|arg2}}
				// Inside the {{}} text nodes are not allowed. 
				// If text is allowed, then arguments usually don't make sense because 
				// text leaves should always be children of an Argument.
				if (!allowedChildnodes.contains(PTMObjects.Text)) {
					try {
						obj = new PTMArgumentNode(content, index, parent);
					} catch (ObjectNotApplicableException e) { obj = null; }
				}
			}
		}
		
		// Test for end pattern or create a text node
		if (obj == null) {
			
			if (allowedChildnodes.contains(PTMObjects.Text)) {
				try {
					obj = new PTMTextLeaf(content, index, parent);
					
					
					// Here follows, attention, recursion!
					// 
					PTMObject o2;
					// Re-use the abort function because we just need a text node,
					// and text nodes is where we can abort
					try {
						o2 = buildObject(content, ++index, parent, abort);
						if (o2 instanceof PTMTextLeaf) {
							((PTMTextLeaf) obj).append((PTMTextLeaf) o2);
						}
					} catch (EndOfExpressionReachedException e) {
						// End of expression reached, don't append anymore
					}
					
				} catch (ObjectNotApplicableException e) { }
			}
		}
		
		return obj;
	}
	
	private static final int min(int left, int right) {
		return (left < right) ? left : right;
	}
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("a {{#if: {{{1}}} }} punkṭ̣·");
		
		PTMObject obj;
		for (int i = 0; i < sb.length(); ) {
			obj = buildObject(sb, i, null);
			System.out.printf("«%s» in %s\n", obj.getRawContent(), obj);
			if (obj != null) {
				System.out.println("End index: " + obj.endIndex);
				i = obj.endIndex;
			} else { i++; }
		}
	}
	
	public static final String getIndicator(StringBuffer content, int index) {
		return content.substring(index, min(index+15, content.length()));
	}
	
}
