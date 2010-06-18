package src.ptm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PTMFunctionNode extends PTMNode {

	abstract public Pattern startPattern();
	
	public PTMFunctionNode(StringBuffer content, int beginIndex) {
		super(content, beginIndex);
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
