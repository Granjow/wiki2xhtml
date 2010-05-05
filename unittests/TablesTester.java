package unittests;

public class TablesTester extends junit.framework.TestCase {

	public void testGetArguments() {
		TestObject[] tests = {
				new TestObject("No argument", "", "No argument"),
				new TestObject("|Empty argument", "",  "Empty argument"),
				new TestObject("arg only|", " arg only", ""),
				new TestObject("arg|text", " arg", "text"),
				new TestObject("arg|text with | pipe", " arg", "text with | pipe")
				};
		String[] out;
		for (TestObject t : tests) {
			out = src.WikiTables.getArguments(t.orig);
			assertEquals(t.res[0], out[0]);
			assertEquals(t.res[1], out[1]);
		}
		
	}

	private class TestObject {

		public final String orig;
		public final String[] res;
		
		public TestObject(String s, String arg, String text) {
			orig = s;
			res = new String[] { arg, text };
		}
	}
}
