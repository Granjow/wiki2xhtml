package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class LinkTester extends junit.framework.TestCase {
	

	@Test
	public void testExternal() throws IOException {
		assertEquals("<a href=\"http://www.example.com\" class=\"external\">http://www.example.com</a>", p("[http://www.example.com]"));
		assertEquals("Nothing", p("Nothing"));
		assertEquals("<a href=\"http://bla.de\" class=\"external\">http://bla.de</a>", p("http://bla.de"));
		assertEquals("<a href=\"http://www.example.com\" class=\"external\">example</a>", p("[http://www.example.com example]"));
		assertEquals("<ref>Link to <a href=\"http://example.org\" class=\"external\">http://example.org</a></ref>", p("<ref>Link to http://example.org</ref>"));
		assertEquals("<ref>Link to <a href=\"http://www.example.org\" class=\"external\">www.example.org</a></ref>", p("<ref>Link to www.example.org</ref>"));
//		assertEquals(, p());
	}
	
	@Test
	public void testNamespace() throws IOException {
		assertEquals("<a href=\"http://de.wikipedia.org/wiki/Test\" class=\"external\">w:Test</a>", p("[[w:Test]]"));
		assertEquals("<a href=\"http://en.wikipedia.org/wiki/Test\" class=\"external\">we:Test</a>", p("[[we:Test]]"));
		assertEquals("<a href=\"http://ru.wikipedia.org/wiki/Test\" class=\"external\">Test</a>", p("[[wr:Test]]"));
		assertEquals("<a href=\"http://ru.wikipedia.org/wiki/Test\" class=\"external\">Test</a>", p("[[wr:Test Test]]"));
		assertEquals("<a href=\"http://ru.wikipedia.org/wiki/Test\" class=\"external\">Test</a>", p("[[wr:Test|Test]]"));
	}
	
	@Test
	public void testArguments() throws IOException {
		assertEquals("<a href=\"this-File.php?query=bla\" class=\"internal\">this-File.php?query=bla</a>", p("[[" + TestObject.thisFile() + "?query=bla]]"));
		assertEquals("<strong class=\"selflink\">this-File.php?query=bla</strong>", p("[[" + TestObject.thisFile() + "?query=bla|disable]]"));
	}
	
	@Test
	public void testAnchors() throws IOException {
		assertEquals("<a id=\"myID\"></a>", p("~~myID~~"));
		assertEquals("<a id=\"myID\"></a> ", p("~~myID~~ ~~myID~~")); // No duplicates allowed
	}
	
	@Test
	public void testAppending() throws IOException {
		assertEquals("a <a href=\"Target\" class=\"internal\">Target</a> here", p("a [[Target]] here"));
	}

	private static final String p(String testString) throws IOException {
		TestObject to = new TestObject(testString, "");
		return to.real();
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) throws IOException {
			super(test, result, new StringBuffer(
					"{{Namespace:w=http://de.wikipedia.org/wiki/%s}}\n" +
					"{{Namespace:we=http://en.wikipedia.org/wiki/%s}}\n" +
					"{{Namespace:wr=http://ru.wikipedia.org/wiki/%s|cut}}\n"
			));
		}
		
		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Links);
		}

	}
	
	
}
