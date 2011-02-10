package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class XMLNamesTester extends junit.framework.TestCase {

	@Test
	public void testXMLNames() throws IOException {
		TestObject to;
		to = new TestObject("<xmlname>hallo\"</xmlname>", "hallo");
		assertEquals(to.correct(), to.real());
		to = new TestObject("<xmlname>[[#hallo|\"]]</xmlname>", "hallo");
		assertEquals(to.correct(), to.real());
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) throws IOException {
			super(test, result);
		}

		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.XMLNames);
		}
	}
	
}
