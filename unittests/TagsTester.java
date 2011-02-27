package unittests;

import java.io.IOException;

import org.junit.Test;

import src.Constants;
import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class TagsTester extends junit.framework.TestCase {

	
	
	@Test
	public void testVersion() throws IOException {
		assertEquals(Constants.Wiki2xhtml.versionNumber, p("{{$Version}}"));
	}
	
	@Test
	public void testNewline() throws IOException {
		assertEquals("a<br/>b", p("a\\\\ b"));
	}
	
	@Test
	public void testHR() throws IOException {
		assertEquals("a----", p("a----"));
		assertEquals("----b", p("----b"));
		assertEquals("A----a", p("A\n----a"));
		assertEquals("<hr/>", p("----"));
		assertEquals("<hr/>", p("\n----"));
		assertEquals("A<hr/>a", p("A\n----\na"));
	}
	

	private static final String p(String testString) throws IOException {
		TestObject to = new TestObject(testString, "");
		return to.real();
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) throws IOException {
			super(test, result);
		}

		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Tags);
		}

		public String real() throws IOException {
			return super.real().replace("\n", "").replace(" <", "<");
		}
	}
	
}
