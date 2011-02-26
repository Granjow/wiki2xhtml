package unittests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Tester extends TestCase {
	
    static {
    	// Enables assertions
//        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

	public static TestSuite suite() {
		TestSuite result = new TestSuite();
		result.addTest(new TestSuite(ArgumentReaderTester.class));
		result.addTest(new TestSuite(CleanupTester.class));
		result.addTest(new TestSuite(FormattingsTester.class));
		result.addTest(new TestSuite(GalleryTester.class));
		result.addTest(new TestSuite(HeadingTester.class));
		result.addTest(new TestSuite(LinkTester.class));
		result.addTest(new TestSuite(ListTester.class));
		result.addTest(new TestSuite(PageTemplateTester.class));
		result.addTest(new TestSuite(ParagraphTester.class));
		result.addTest(new TestSuite(ParserFunctionsTester.class));
		result.addTest(new TestSuite(PicTester.class));
		result.addTest(new TestSuite(PlaceholderTester.class));
		result.addTest(new TestSuite(ReferencesTester.class));
		result.addTest(new TestSuite(RegexTester.class));
		result.addTest(new TestSuite(SettingsTester.class));
		result.addTest(new TestSuite(TablesTester.class));
		result.addTest(new TestSuite(TemplateTester.class));
		result.addTest(new TestSuite(WikiSettingsTester.class));
		result.addTest(new TestSuite(XMLNamesTester.class));
		return result;
	}
	
}
