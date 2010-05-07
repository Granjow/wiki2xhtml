package unittests;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class ListTester extends junit.framework.TestCase {

	@Test public void testUl() {
		TestObject t = new TestObject("*bla", "\n<ul>\n <li>bla</li>\n</ul>\n");
		assertEquals(t.correct(), t.real());
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) {
			super(test, result);
		}

		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Lists);
		}
	}
	
}
