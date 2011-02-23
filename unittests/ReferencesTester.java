package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class ReferencesTester extends junit.framework.TestCase {

	
	@Test
	public void testSimpleReferences() throws IOException {
		assertEquals("* ref1", p("<ref>ref1</ref>"));
		assertEquals("* ref1\n* ref2", p("<ref>ref1</ref> and <ref>ref2</ref>"));
	}
	
	@Test
	public void testNamedReferences() throws IOException {
		assertEquals("* ref1", p("<ref name=\"refA\">ref1</ref> and <ref name=\"refA\">unused text here</ref>"));
	}
	
	@Test
	public void testBackReference() throws IOException {
		// Test: Anchor to previous
	}
	
	

	private static final String p(String testString, String template) throws IOException {
		TestObject to = new TestObject(testString, "");
		to.writeFile("tplImage.txt", template);
		return to.real();
	}
	private static final String p(String testString) throws IOException {
		final String template1 = "<img src=\"{{{path}}}\"/>";
		return p(testString, template1);
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) throws IOException {
			super(test, result);
		}

		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.References);
		}

		public String real() throws IOException {
			return super.real().replace("\n", "").replace(" <", "<");
		}
	}
	
}
