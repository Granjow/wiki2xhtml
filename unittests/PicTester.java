package unittests;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class PicTester extends junit.framework.TestCase {

	public void testArguments() {
	
	}
	
	@Test
	public void testImages() {
		assertEquals("<img src=\"img.jpg\"/>", p("[[Image:img.jpg]]"));
	}
	
	
	private static final String p(String testString) {
		TestObject to = new TestObject(testString, "");
		return to.real();
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) {
			super(test, result);
		}

		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Images);
		}

		public String real() {
			return super.real().replace("\n", "").replace(" <", "<");
		}
	}
	
}
