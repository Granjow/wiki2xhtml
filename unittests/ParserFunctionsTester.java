package unittests;

import static src.parserFunctions.Parser.parse;

import java.io.FileNotFoundException;
import org.junit.Test;
import src.ptm.PTMRootNode;

public class ParserFunctionsTester extends junit.framework.TestCase {
	
	public String p(String s) {
		return new PTMRootNode(new StringBuffer(s), null).evaluate();
	}
	
	@Test
	public void testIf() {
		assertEquals("nothing", p("nothing"));
		assertEquals("empty", p("{{#if: ||empty}}"));
		assertEquals("empty", p("{{#if:    \t \t||empty}}"));
		assertEquals("empty", p("{{#if:||empty}}"));
		assertEquals("new\nline", p("{{#if:||new\nline}}"));
	}
	
	@Test
	public void testIfTemplated() throws FileNotFoundException {
		assertEquals("", p("{{#if: | width=\"{{{width|}}}\" }}"));
	}
	
	@Test
	public void testIfNested() {
		assertEquals("", parse("{{#if: |{{#if:a|a|b}}|{{#if:|c|}}}}"));
	}
	
	@Test
	public void testIfeq() {
		assertEquals("equal", p("{{#ifeq:a|a|equal|}}"));
		assertEquals("equal", p("{{#ifeq:\ta \t| a |equal|}}"));
		assertEquals("different", p("{{#ifeq:|a||different}}"));
	}
	
	@Test
	public void testSwitch() {
		assertEquals("correct", p("{{#switch: a| a=correct |b=incorrect}}"));
		assertEquals("correct", p("{{#switch: b| a=incorrect |b=correct}}"));
		assertEquals("correct", p("{{#switch: \t| a=incorrect |=correct}}"));
		assertEquals("correct", p("{{#switch: newline\t\n| a=incorrect \n|b=incorrect\n|newline=correct}}"));
		assertEquals("correct", p("{{#switch: nonexistant| a=incorrect |b=incorrect| #default = correct}}"));
	}
	

}
