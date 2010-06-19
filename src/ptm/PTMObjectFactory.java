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
	
	
	/**
	 * <p>Tries to build a new {@link PTMObject} at the given index.</p>
	 * <p>This method is called recursively. The steps are as follows:</p>
	 * <ol>
	 * <li>Check whether we are at the end of the file or whether the parent expression ends here. In the latter case, throw an exception to notify the parent.</li>
	 * <li>Try to create a new object of some (given) types. If it can be created, the object itself calls this method again to find further objects.</li>
	 * </ol> 
	 * @param parent The parent object of the child that will/might be created.
	 * @param abort Function handle that checks when to abort because the end of the parent expression has been reached (see point 1).
	 * @param allowedChildnodes Allowed child types. Only these will be tried to create (see point 2).
	 * @return A new object or null, if no suitable found (either due to object restrictions by <code>allowedChildnodes</code> or because the End of File has been reached).
	 * @throws EndOfExpressionReachedException If the end of the parent expression has been reached (see point 1). This is checked here and not in the parent
	 * to abbreviate recognition of plaintext.
	 */
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
		StringBuffer sb = new StringBuffer("{{#if: a|b}}");
		
		PTMObject obj;
		for (int i = 0; i < sb.length(); ) {
			obj = buildObject(sb, i, null);
			System.out.printf("Found: «%s» in %s\n", obj.getRawContent(), obj);
			if (obj != null) {
				System.out.println("End index: " + obj.endIndex);
				i = obj.endIndex;
			} else { i++; }
		}
	}
	
	/**
	 * Returns a short substring to work with regular expressions on.
	 * They are needed because e.g. <code>{{#if:</code> could also be written with whitespaces as <code>{{ #if :</code>.
	 * This short indicator takes just a reasonable number of characters to keep things efficient.
	 */
	public static final String getIndicator(StringBuffer content, int index) {
		return content.substring(index, min(index+15, content.length()));
	}
	
}
