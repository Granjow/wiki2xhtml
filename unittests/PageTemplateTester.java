package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;


public class PageTemplateTester extends junit.framework.TestCase {

	@Test
	public void testManualBindings() throws IOException {
		assertEquals("custom argument;", p("{{Bind:myArg=custom argument}}"));
		assertEquals("custom argument;Nobody", p("{{Bind:myArg=custom argument}}\n{{Author:Nobody}}"));
		assertEquals("1 and 2", p("{{Bind:arg1=1}}\n{{Bind:arg2=2}}", "{{{arg1}}} and {{{arg2}}}"));
	}
	
	@Test
	public void testManualBindingsOverwriting() throws IOException {
		assertEquals("custom argument;Simon", p("{{Bind:myArg=custom argument}}\n{{Author:Nobody}}\n{{Bind:author=Simon}}"));
	}
	
	private static final String p(String testString, String template) throws IOException {
		TestObject to = new TestObject(testString, "");
		to.writeFile("tplPage.txt", template);
		return to.real();
	}
	private static final String p(String testString) throws IOException {
		final String template1 = "{{{myArg|}}};{{{author|}}}";
		return p(testString, template1);
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) throws IOException {
			super(test, result);
		}

		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Settings);
			vf.addTask(Task.PageTemplate);
		}

		public String real() throws IOException {
			return super.real().replace("\n", "").replace(" <", "<");
		}
	}
	
}
