package unittests;

import java.io.IOException;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class LinkTester extends junit.framework.TestCase {
	

	public void testExternal() throws IOException {
		final TestObject[] tests = new TestObject[] {
			new TestObject("[http://www.example.com]", "<a href=\"http://www.example.com\" class=\"external\">http://www.example.com</a>"),
			new TestObject("Nothing", "Nothing"),
			new TestObject("http://bla.de", "<a href=\"http://bla.de\" class=\"external\">http://bla.de</a>"),
			new TestObject("[http://www.example.com example]", "<a href=\"http://www.example.com\" class=\"external\">example</a>"),
			new TestObject("<ref>Link to http://example.org</ref>", "<ref>Link to <a href=\"http://example.org\" class=\"external\">http://example.org</a></ref>"),
			new TestObject("<ref>Link to www.example.org</ref>", "<ref>Link to <a href=\"http://www.example.org\" class=\"external\">www.example.org</a></ref>")
		};
		
		for (TestObject t : tests) {
			assertEquals(t.correct(), t.real());
		}
	}
	
	public void testNamespace() throws IOException {
		StringBuffer settings = new StringBuffer(
					"{{Namespace:w=http://de.wikipedia.org/wiki/%s}}\n" +
					"{{Namespace:we=http://en.wikipedia.org/wiki/%s}}\n" +
					"{{Namespace:wr=http://ru.wikipedia.org/wiki/%s|cut}}\n"
			);
		final TestObject[] tests = new TestObject[] {
				new TestObject("[[w:Test]]", "<a href=\"http://de.wikipedia.org/wiki/Test\" class=\"external\">w:Test</a>", settings),
				new TestObject("[[we:Test]]", "<a href=\"http://en.wikipedia.org/wiki/Test\" class=\"external\">we:Test</a>", settings),
				new TestObject("[[wr:Test]]", "<a href=\"http://ru.wikipedia.org/wiki/Test\" class=\"external\">Test</a>", settings),
				new TestObject("[[wr:Test Test]]", "<a href=\"http://ru.wikipedia.org/wiki/Test\" class=\"external\">Test</a>", settings),
				new TestObject("[[wr:Test|Test]]", "<a href=\"http://ru.wikipedia.org/wiki/Test\" class=\"external\">Test</a>", settings)
			};

		for (TestObject t : tests) {
			assertEquals(t.correct(), t.real());
		}
	}
	
	public void testArguments() throws IOException {
		StringBuffer settings = new StringBuffer("{{Namespace:w=http://de.wikipedia.org/wiki/%s}}\n");
		final TestObject[] tests = new TestObject[] {
				new TestObject("[[" + TestObject.thisFile() + "?query=bla]]", "<a href=\"this-File.php?query=bla\" class=\"internal\">this-File.php?query=bla</a>", settings),
				new TestObject("[[" + TestObject.thisFile() + "?query=bla|disable]]", "<strong class=\"selflink\">this-File.php?query=bla</strong>", settings)
			};

		for (TestObject t : tests) {
			assertEquals(t.correct(), t.real());
		}
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) throws IOException {
			super(test, result);
		}
		public TestObject(String test, String result, StringBuffer settings) throws IOException {
			super(test, result, settings);
		}
		
		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Links);
		}

	}
	
	
}
