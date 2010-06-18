package src.ptm;

import java.util.regex.Matcher;

import src.ptm.PTMObject.ObjectNotApplicableException;

public class PTMObjectFactory {

	public static final PTMObject buildObject(StringBuffer content, int index) {
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
		
		return obj;
	}
	
	private static final int min(int left, int right) {
		return (left < right) ? left : right;
	}
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("a {{#if:");
		System.out.println(buildObject(sb, 0));
		System.out.println(buildObject(sb, 1));
		System.out.println(buildObject(sb, 2));
	}
	
}
