package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class HeadingTester extends junit.framework.TestCase {
	
	@Test
	public void testSimpleHeadings() throws IOException {
		assertEquals("<h2 id=\"h2_Titel\">Titel</h2>", p("==Titel=="));
		assertEquals("<h2 id=\"h2_Titel\">Titel</h2>", p("== Titel ==  "));
		assertEquals("<h3 id=\"h3_Titel\">Titel</h3>", p("=== Titel ===  "));
		assertEquals("<h2 id=\"h2_H2\">H2</h2>\n<h3 id=\"h3_H3\">H3</h3>", p("== H2 ==\n=== H3 ==="));
	}
	
	@Test
	public void testIDs() throws IOException {
		assertEquals("<h2 id=\"h2_Titel\">Titel</h2>\n<h2 id=\"h2_Titel_\">Titel</h2>", p("==Titel==\n==Titel=="));
	}
	
	@Test
	public void testAppending() throws IOException {
		assertEquals("a<h2 id=\"h2_Titel\">Titel</h2>b", p("a\n==Titel==\nb").replace("\n", ""));
	}
	
	

	

	private static final String p(String testString) throws IOException {
		TestObject to = new TestObject(testString, "");
		return to.real();
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) throws IOException {
			super(test, result);
		}
		public TestObject(String test, String result, StringBuffer settings) throws IOException {
			super(test, result, settings);
		}
		
		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Headings);
		}

	}
	
}
