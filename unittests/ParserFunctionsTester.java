package unittests;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import src.ptm.PTMObject.RecursionException;
import src.ptm.PTMRootNode;

public class ParserFunctionsTester extends junit.framework.TestCase {
	
	@Test
	public void testIf() {
		assertEquals("nothing", p("nothing"));
		assertEquals("empty", p("{{#if: ||empty}}"));
		assertEquals("empty", p("{{#if:    \t \t||empty}}"));
		assertEquals("empty", p("{{#if:||empty}}"));
		assertEquals("new\nline", p("{{#if:||new\nline}}"));
		assertEquals("{{#if:tooShort}}", p("{{#if:tooShort}}"));
		assertEquals("", p("{{#if:|endingWithBar|}}"));
		assertEquals("width=100px", p("{{#if:a|width=100px}}"));
	}
	
	@Test
	public void testIfTemplated() throws FileNotFoundException {
		assertEquals("", p("{{#if: | width=\"{{{width|}}}\" }}"));
	}
	
	@Test
	public void testIfNested() {
		assertEquals("", p("{{#if: |{{#if:a|a|b}}|{{#if:|c|}}}}"));
	}
	
	@Test
	public void testIfeq() {
		assertEquals("equal", p("{{#ifeq:a|a|equal|}}"));
		assertEquals("equal", p("{{#ifeq:\ta \t| a |equal|}}"));
		assertEquals("different", p("{{#ifeq:|a||different}}"));
		assertEquals("equal", p("{{#ifeq:||equal}}"));
		assertEquals(" equal ", p("{{#ifeq: 	|  | equal }}"));
		assertEquals("e=qual", p("{{#ifeq:a=b|a=b|e=qual}}"));
	}
	
	@Test
	public void testIfvalexists() throws IOException, RecursionException {
		assertEquals(" no ", p("{{#ifvalexists:a | yes | no }}"));
		assertEquals("yes", TemplateTester.p("{{#ifvalexists:thumb|yes|no}}", "{{:%s|some|args|thumb}}"));
	}
	
	@Test
	public void testSwitch() {
		assertEquals("correct ", p("{{#switch: a| a=correct |b=incorrect}}"));
		assertEquals("correct", p("{{#switch: b| a=incorrect |b=correct}}"));
		assertEquals("correct", p("{{#switch: \t| a=incorrect |=correct}}"));
		assertEquals("correct", p("{{#switch: newline\t\n| a=incorrect \n|b=incorrect\n|newline=correct}}"));
		assertEquals(" correct", p("{{#switch: nonexistant| a=incorrect |b=incorrect| #default = correct}}"));
	}
	
	@Test
	public void testCDATA() {
		assertEquals("<![CDATA[", p("<![CDATA["));
		assertEquals("", p("<![CDATA[]]>"));
		assertEquals("ABC", p("A<![CDATA[B]]>C"));
		assertEquals("A|C", p("A<![CDATA[|]]>C"));
		assertEquals("AB]]>C", p("A<![CDATA[B]]>]]>C"));
		assertEquals("|", p("{{#if:a|<![CDATA[|]]>|b}}"));
	}
	
	@Test
	public void testAppending() {
		assertEquals("abc", p("a{{#if:a|b|c}}c"));
	}
	
	
	public static final String p(String s) {
		try {
			return new PTMRootNode(new StringBuffer(s), null).evaluate();
		} catch (RecursionException e) {
			e.printStackTrace();
			return "FAILED";
		}
	}
	

}
