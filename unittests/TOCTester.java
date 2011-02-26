package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class TOCTester extends junit.framework.TestCase  {
	
	
	@Test
	public void testTOC() throws IOException {
		String template = "{{#if:{{{isBlock}}}|{{{text}}}|{{{ul}}} {{{text}}}}}";
		assertEquals("* Heading== Heading ==", p("{{:tplTOC.txt}}\n== Heading ==", template));
	}
	
	@Test
	public void testIDs() throws IOException {
		String template = "{{#if:{{{isBlock}}}|{{{text}}}|{{{id}}};}}";
		assertEquals("h2_Heading;== Heading ==", p("{{:tplTOC.txt}}\n== Heading ==", template));
		assertEquals("h2_Heading;h2_Heading_;== Heading ====Heading==", p("{{:tplTOC.txt}}\n== Heading ==\n==Heading==", template));
	}
	
	@Test
	public void testParams() throws IOException {
		String template = "{{#if:{{{isBlock}}}|{{{text}}}|{{{a}}} {{{text}}}}}";
		assertEquals("AA Heading== Heading ==", p("{{:tplTOC.txt|a=AA}}\n== Heading ==", template));
	}
	

	private static final String p(String testString, String template) throws IOException {
		TestObject to = new TestObject(testString, "");
		to.writeFile("tplTOC.txt", template);
		return to.real();
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) throws IOException {
			super(test, result);
		}

		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.TOC);
		}

		public String real() throws IOException {
			return super.real().replace("\n", "").replace(" <", "<");
		}
	}

}
