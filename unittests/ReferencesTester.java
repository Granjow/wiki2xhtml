package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class ReferencesTester extends junit.framework.TestCase {

	
	@Test
	public void testSimpleReferences() throws IOException {
		assertEquals("<ref>ref1</ref>", p("<ref>ref1</ref>")); // <references/> tag missing
		assertEquals("(1:ref1)", p("<ref>ref1</ref><references/>")); // tag with or without space
		assertEquals("(1:ref1)", p("<ref>ref1</ref><references />"));
		assertEquals("(1:ref1) and (2:ref2)", p("<ref>ref1</ref> and <ref>ref2</ref><references/>")); // multiple references
	}
	
	@Test
	public void testNamedReferences() throws IOException {
		assertEquals("(1:ref1) and (1:ref1)", p("<ref name=\"refA\">ref1</ref> and <ref name=\"refA\">unused text here</ref><references/>"));
		
		String template1 = "({{{number}}})";
		String template2 = "{{#if:{{{isContainer|}}}|{{{text}}}|[{{{number}}}:{{{text}}}]}}";
		assertEquals("(1)[1:text1]", p("<ref>text1</ref><references/>", template1, template2));
		assertEquals("(1)(1)[1:text1]", p("<ref name=\"a\">text1</ref><ref name=\"a\">text2</ref><references/>", template1, template2));
		assertEquals("(1)(1)[1:text1]", p("<ref name=\"a\">text1</ref><ref name=\"a\"></ref><references/>", template1, template2));
		assertEquals("(1)(2)(1)[1:text1][2:text2]", p("<ref name=\"a\">text1</ref><ref>text2</ref><ref name=\"a\">text2</ref><references/>", template1, template2));
	}
	
	@Test
	public void testBackReference() throws IOException {
		final String template1 = "(->{{{citeNoteID}}}:{{{citeRefID}}};;{{{number}}})";
		final String template2 = "{{#if:{{{isContainer|}}}|{{{text}}}|[<-{{{citeRefID}}}:{{{citeNoteID}}};;{{{number}}}:{{{text}}}]}}";
		assertEquals(
				"(->cite_1-note:cite_1-ref;;1)[<-cite_1-ref:cite_1-note;;1:text1]", 
				p("<ref>text1</ref><references/>", template1, template2));
		assertEquals( // Custom named references
				"(->a-note:a-ref;;1)(->a-note:a-ref;;1)[<-a-ref:a-note;;1:text1]", 
				p("<ref name=\"a\">text1</ref><ref name=\"a\">text2</ref><references/>", template1, template2));
		assertEquals( // Mixed references
				"(->a-note:a-ref;;1)(->cite_2-note:cite_2-ref;;2)(->a-note:a-ref;;1)[<-a-ref:a-note;;1:text1][<-cite_2-ref:cite_2-note;;2:text2]", 
				p("<ref name=\"a\">text1</ref><ref>text2</ref><ref name=\"a\">text2</ref><references/>", template1, template2));
	}
	
	

	private static final String p(String testString, String templateLink, String templateEntry) throws IOException {
		TestObject to = new TestObject(testString, "");
		to.writeFile("tplCiteRef.txt", templateLink);
		to.writeFile("tplCiteNote.txt", templateEntry);
		return to.real();
	}
	private static final String p(String testString) throws IOException {
		final String template1 = "({{{number}}}:{{{text}}})";
		final String template2 = "";
		return p(testString, template1, template2);
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
