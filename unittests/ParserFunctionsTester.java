package unittests;

import static src.parserFunctions.Parser.parse;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class ParserFunctionsTester extends junit.framework.TestCase {
	
	@Test
	public void testIf() {
		assertEquals("nothing", parse(new StringBuffer("nothing")).toString());
		assertEquals("empty", parse(new StringBuffer("{{#if: ||empty}}")).toString());
		assertEquals("empty", parse(new StringBuffer("{{#if:    \t \t||empty}}")).toString());
		assertEquals("empty", parse(new StringBuffer("{{#if:||empty}}")).toString());
		assertEquals("new\nline", parse(new StringBuffer("{{#if:||new\nline}}")).toString());
		assertEquals("", parse(new StringBuffer("{{#if: | width=\"{{{width|}}}\" }}")).toString());
	}
	
	@Test
	public void testIfeq() {
		assertEquals("equal", parse(new StringBuffer("{{#ifeq:a|a|equal|}}")).toString());
		assertEquals("equal", parse(new StringBuffer("{{#ifeq:\ta \t| a |equal|}}")).toString());
		assertEquals("different", parse(new StringBuffer("{{#ifeq:|a||different}}")).toString());
	}
	
	@Test
	public void testSwitch() {
		assertEquals("correct", parse(new StringBuffer("{{#switch: a| a=correct |b=incorrect}}")).toString());
		assertEquals("correct", parse(new StringBuffer("{{#switch: b| a=incorrect |b=correct}}")).toString());
		assertEquals("correct", parse(new StringBuffer("{{#switch: \t| a=incorrect |=correct}}")).toString());
		assertEquals("correct", parse(new StringBuffer("{{#switch: newline\t\n| a=incorrect \n|b=incorrect\n|newline=correct}}")).toString());
		assertEquals("correct", parse(new StringBuffer("{{#switch: nonexistant| a=incorrect |b=incorrect| #default = correct}}")).toString());
	}
	
	@Test
	public void testWikiParser() {
		TestObject o = new TestObject("{{#if: ||empty}}", "empty");
		assertEquals(o.correct(), o.real());
	}
	
	private class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) {
			super(test, result);
		}
		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.ParserFunctions);
		}
		
	}
	

}
