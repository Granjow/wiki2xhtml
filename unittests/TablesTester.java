package unittests;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.WikiTables;
import src.tasks.Tasks.Task;
import src.tasks.WikiTables.ArgTuple;

public class TablesTester extends junit.framework.TestCase {

	@Test
	public void testGetArguments() {
		TestCase[] tests = {
				new TestCase("No argument", "", "No argument"),
				new TestCase("|Empty argument", "",  "Empty argument"),
				new TestCase("arg only|", " arg only", ""),
				new TestCase("arg|text", " arg", "text"),
				new TestCase("arg|text with | pipe", " arg", "text with | pipe")
				};
		ArgTuple out;
		for (TestCase t : tests) {
			out = WikiTables.getArguments(t.orig);
			assertEquals(t.res[0], out.k());
			assertEquals(t.res[1], out.v());
		}
		
	}
	
	@Test
	public void testSimpleTable() {
		TestObject to = new TestObject("{|\n|a||b\n|}", "<table><tr><td>a</td><td>b</td></table>");
		assertEquals(to.correct(), to.real());
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) {
			super(test, result);
		}

		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Tables);
		}

		public String real() {
			return super.real().replace(" ", "").replace("\n", "");
		}
	}

	private class TestCase {

		public final String orig;
		public final String[] res;
		
		public TestCase(String s, String arg, String text) {
			orig = s;
			res = new String[] { arg, text };
		}
	}
}
