package unittests;

import src.project.file.VirtualWikiFile;
import src.settings.XhtmlSettings;
import src.tasks.Tasks.Task;

public class LinkTester extends junit.framework.TestCase {
	

	public void testExternal() {
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
	
	public void testNamespace() {
		src.settings.XhtmlSettings.getInstance().global = (XhtmlSettings.GlobalSettings) 
			src.settings.XhtmlSettingsReader.getSettings(new StringBuffer(
					"{{Namespace:w=http://de.wikipedia.org/wiki/%s}}\n" +
					"{{Namespace:we=http://en.wikipedia.org/wiki/%s}}\n" +
					"{{Namespace:wr=http://ru.wikipedia.org/wiki/%s|cut}}\n"
			), true);
		final TestObject[] tests = new TestObject[] {
				new TestObject("[[w:Test]]", "<a href=\"http://de.wikipedia.org/wiki/Test\" class=\"external\">w:Test</a>"),
				new TestObject("[[we:Test]]", "<a href=\"http://en.wikipedia.org/wiki/Test\" class=\"external\">we:Test</a>"),
				new TestObject("[[wr:Test]]", "<a href=\"http://ru.wikipedia.org/wiki/Test\" class=\"external\">Test</a>"),
				new TestObject("[[wr:Test Test]]", "<a href=\"http://ru.wikipedia.org/wiki/Test\" class=\"external\">Test</a>"),
				new TestObject("[[wr:Test|Test]]", "<a href=\"http://ru.wikipedia.org/wiki/Test\" class=\"external\">Test</a>")
			};

		for (TestObject t : tests) {
			assertEquals(t.correct(), t.real());
		}
	}
	
	public void testArguments() {
		src.settings.XhtmlSettings.getInstance().global = (XhtmlSettings.GlobalSettings) 
			src.settings.XhtmlSettingsReader.getSettings(new StringBuffer(
					"{{Namespace:w=http://de.wikipedia.org/wiki/%s}}\n"
			), true);
		final TestObject[] tests = new TestObject[] {
				new TestObject("[[" + TestObject.thisFile() + "?query=bla]]", "<a href=\"this-File.php?query=bla\" class=\"internal\">this-File.php?query=bla</a>"),
				new TestObject("[[" + TestObject.thisFile() + "?query=bla|disable]]", "<strong class=\"selflink\">this-File.php?query=bla</strong>")
			};

		for (TestObject t : tests) {
			assertEquals(t.correct(), t.real());
		}
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) {
			super(test, result);
		}
		
		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Links);
		}

	}
	
	
}
