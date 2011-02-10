package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class PicTester extends junit.framework.TestCase {

	public void testArguments() {
	
	}
	
	@Test
	public void testSimpleImages() throws IOException {
		assertEquals("<img src=\"img.jpg\"/>", p("[[Image:img.jpg]]"));
	}
	
	@Test
	public void testImageParameters() throws IOException {
		assertEquals("<img src=\"img.jpg\" width=\"100px\"/>", p("[[Image:img.jpg|100px]]"));
	}
	
	
	private static final String p(String testString) throws IOException {
		TestObject to = new TestObject(testString, "");
		to.writeFile("tplImage.txt", "<img src=\"{{{path}}}\"{{#if:{{{width|}}}| width=\"{{{width}}}\"}}/>");
		return to.real();
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) throws IOException {
			super(test, result);
		}

		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Images);
		}

		public String real() throws IOException {
			return super.real().replace("\n", "").replace(" <", "<");
		}
	}
	
}
