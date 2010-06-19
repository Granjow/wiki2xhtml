package src.ptm;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PTMFunctionNode extends PTMNode {

	abstract public Pattern startPattern();
	public static final String endExpression = "}}";
	
	public static final AbortFunction abort;
	
	static {
		ArrayList<String> al = new ArrayList<String>();
		al.add("}}");
		abort = PTM.createAbortFunction(al);
	}
	
	public PTMFunctionNode(StringBuffer content, int beginIndex, PTMObject parent) {
		super(content, beginIndex, parent);
	}
	
	public boolean applies(StringBuffer content, int index) {
		try {
			Matcher m = startPattern().matcher(content.substring(index));
			return m.find();
		} catch (StringIndexOutOfBoundsException e) {
			return false;
		}
	}

}
