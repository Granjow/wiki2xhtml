package unittests;

import org.junit.Test;

import src.project.file.Generators;
import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class HeadingTester extends junit.framework.TestCase {

	@Test
	public void testGen() {
		assertEquals("#<a href=\"#h_hello\">hello</a>", Generators.getTOCItem(1, 1, "hello", "").toString().trim());
		assertEquals("#<a href=\"#myid\">hello</a>", Generators.getTOCItem(1, 1, "hello", "id=\"myid\"").toString().trim());
	}
	
	@Test
	public void testSimpleHeadings() {
		TestObject to;
		to = new TestObject("==Titel==", "<h2 id=\"h_Titel\">Titel</h2>");
		assertEquals(to.correct(), to.real());
		
		to = new TestObject("== Titel ==  ", "<h2 id=\"h_Titel\">Titel</h2>");
		assertEquals(to.correct(), to.real());
		
		to = new TestObject("=== Titel ===  ", "<h3 id=\"h_Titel\">Titel</h3>");
		assertEquals(to.correct(), to.real());
	}
	
	@Test
	public void testIDs() {
		TestObject to;
		to = new TestObject("==Titel==\n==Titel==", "<h2 id=\"h_Titel\">Titel</h2>\n<h2 id=\"h_Titel_\">Titel</h2>");
		System.out.println(to.real());
		assertEquals(to.correct(), to.real());
	}
	
	

	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) {
			super(test, result);
		}
		public TestObject(String test, String result, StringBuffer settings) {
			super(test, result, settings);
		}
		
		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Headings);
		}

	}
	
}
