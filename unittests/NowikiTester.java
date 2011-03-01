package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class NowikiTester extends junit.framework.TestCase {

	
	@Test
	public void testNowiki() throws IOException {
		assertEquals("a", p("<nowiki>a</nowiki>"));
		assertEquals("abc", p("<nowiki>a</nowiki>b<nowiki>c</nowiki>"));
		assertEquals("*", p("<nowiki>*</nowiki>"));
		assertEquals("<nowiki></nowiki>", pr("<nowiki>*</nowiki>"));
	}
	
	

	private static final String pr(String testString) throws IOException {
		VirtualWikiFile vf = new VirtualWikiFile(VirtualWikiFile.createEmptyProject(), "file", false, new StringBuffer(testString));
		vf.removeAllTasks();
		vf.addTask(Task.RemoveNowiki);
		vf.parse();
		return vf.getContent().toString();
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
			vf.addTask(Task.RemoveNowiki);
			vf.addTask(Task.InsertNowiki);
		}

		public String real() throws IOException {
			return super.real().replace("\n", "").replace(" <", "<");
		}
	}
	
	
}
