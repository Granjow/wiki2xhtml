package unittests;

import java.io.IOException;

import junit.framework.TestCase;
import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class FormattingsTester extends TestCase {

	public void testBoldItalic() throws IOException {
		assertEquals("<strong>just bold.</strong>", p("'''just bold.'''"));
		assertEquals("<strong>'ts bold too</strong>", p("''''ts bold too'''"));
		assertEquals("<strong>not 'new'.</strong>", p("'''not 'new'.'''"));
		assertEquals("<strong>bold.</strong>no more'''", p("'''bold.'''no more'''"));
		assertEquals("<strong><em>b/i</em>b</strong>", p("'''''b/i''b'''"));
		assertEquals("<strong>b<em>b/i</em></strong>", p("'''b''b/i'' '''"));
		assertEquals("<em>italic</em>", p("''italic''"));
		assertEquals("<em>'ts italic too</em>", p("'''ts italic too''"));
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
			vf.addTask(Task.Formattings);
		}

		public String real() throws IOException {
			return super.real().replace("\n", "").replace(" <", "<");
		}
	}
	
}
