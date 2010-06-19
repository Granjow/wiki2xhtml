package src.ptm;

import java.util.regex.Matcher;

import src.ptm.PTMObject.EndOfExpressionReachedException;
import src.ptm.PTMObject.ObjectNotApplicableException;

public class PTMObjectFactory {


	public static final PTMObject buildObject(StringBuffer content, int index) {
		try {
			return buildObject(content, index, null);
		} catch (EndOfExpressionReachedException e) {
			return null;
		}
	}
	
	public static final PTMObject buildObject(StringBuffer content, int index, String endPattern) throws EndOfExpressionReachedException {
		if (index >= content.length()) { 
			return null;
		}

		String indicator = content.substring(index, min(index+15, content.length()));
		PTMObject obj = null;
		Matcher m;
		
		System.out.println(indicator);
		m = PTMFunctionIf.startPattern.matcher(indicator);
		if (m.find()) {
			try {
				obj = new PTMFunctionIf(content, index);
			} catch (ObjectNotApplicableException e) {
				obj = null;
			}
		}
		
		if (obj == null) {
			if (endPattern != null) {
				try {
					if (endPattern.equals(content.substring(index, index+endPattern.length()))) {
						throw new EndOfExpressionReachedException();
					}
				} catch (StringIndexOutOfBoundsException e) {}
			}
			
			try {
				obj = new PTMTextLeaf(content, index);
				PTMObject o2;
				do {
					o2 = buildObject(content, ++index, endPattern);
					if (o2 instanceof PTMTextLeaf) {
						((PTMTextLeaf) obj).append((PTMTextLeaf) o2);
					} else {
						break;
					}
				} while (o2 != null && o2 instanceof PTMTextLeaf);
				
			} catch (ObjectNotApplicableException e) { }
		}
		
		return obj;
	}
	
	private static final int min(int left, int right) {
		return (left < right) ? left : right;
	}
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("a {{#if: {{{1}}} }} punkṭ̣·");
		
		PTMObject obj;
		for (int i = 0; i < 3; i++) {
			obj = buildObject(sb, i);
			System.out.printf("«%s» in %s\n", obj.getRawContent(), obj);
		}
	}
	
}
