package unittests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Tester extends TestCase {

	public static TestSuite suite() {
		TestSuite result = new TestSuite();
		result.addTest(new TestSuite(ArgumentReaderTester.class));
		result.addTest(new TestSuite(ParserFunctionsTester.class));
		result.addTest(new TestSuite(TablesTester.class));
		result.addTest(new TestSuite(LinkTester.class));
		result.addTest(new TestSuite(FormattingsTester.class));
		result.addTest(new TestSuite(PicTester.class));
		result.addTest(new TestSuite(XhtmlSettingsTester.class));
		return result;
	}
	
}