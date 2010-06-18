package src.ptm;

import java.util.regex.Pattern;

public class PTMFunctionIf extends PTMFunctionNode {
	
	public static final Pattern startPattern = Pattern.compile("^\\{\\{\\s*#if\\s*:");
	
	public final Pattern startPattern() { return startPattern; }

	public PTMFunctionIf(StringBuffer content, int beginIndex) throws ObjectNotApplicableException {
		super(content, beginIndex);
	}
	
	
	public static void main(String[] args) throws ObjectNotApplicableException {
		StringBuffer sb = new StringBuffer("Hallo {{#if: .");
		PTMFunctionIf f = new PTMFunctionIf(sb, 0);
		for (int i = 0; i <= sb.length()+1; i++) {
			if (f.applies(sb, i)) {
				System.out.printf("Applies at %d! --->%s<---\n", i, sb.substring(i));
			}
		}
	}
}
