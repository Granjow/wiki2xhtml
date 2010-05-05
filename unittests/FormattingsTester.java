package unittests;

import junit.framework.TestCase;
import src.typo.Formattings;

public class FormattingsTester extends TestCase {

	public void testBold() {
		String[][] testCases = {
				{ "'''just bold.'''", "<strong>just bold.</strong>" },
				{ "''''ts bold too'''", "<strong>'ts bold too</strong>" },
				{ "'''not 'new'.'''", "<strong>not 'new'.</strong>" },
				{ "'''bold.'''no more'''", "<strong>bold.</strong>no more'''" },
				{ "'''''b/i''b'''", "<strong>''b/i''b</strong>" },
				{ "'''b''b/i'' '''", "<strong>b''b/i'' </strong>" }
		};
		for (String[] s : testCases) {
			assertEquals(s[1], Formattings.makeBoldType(new StringBuffer(s[0]), false).toString());
		}

	}
	
	public void testItalic() {
		String[][] testCases = {
				{ "''italic''", "<em>italic</em>" },
				{ "'''ts italic too''", "<em>'ts italic too</em>" }
		};
		for (String[] s : testCases) {
			assertEquals(s[1], Formattings.makeItalicType(new StringBuffer(s[0]), false).toString());
		}

			
	}
	
}
