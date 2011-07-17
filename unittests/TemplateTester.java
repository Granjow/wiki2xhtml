package unittests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import src.ptm.PTMObject.RecursionException;
import src.ptm.PTMRootNode;

public class TemplateTester extends junit.framework.TestCase {
	
	@Test
	public void testPTMSimpleReplace() throws IOException, RecursionException {
		assertEquals("I am a template.", p("I am a template.", "{{:%s}}"));
	}

	@Test
	public void testPTMArguments() throws IOException, RecursionException {
		assertEquals("Arguments <one> <two>.", p("Arguments <{{{1}}}> <{{{2}}}>.", "{{:%s|one|two}}"));
		assertEquals("Argument <two>.", p("Argument <{{{3|{{{2|}}}}}}>.", "{{:%s|one|two}}"));
		assertEquals("Argument <\ntwo>.", p("Argument <{{{3|{{{2|}}}}}}>.", "{{:%s|one|\ntwo}}"));
		String s = "\n&#123;{Meta:name=content}&#125; (name/content combinations)\\\\ \n" +
		"&#123;{Meta:anything you like}&#125; (any other combination)\n";

		assertEquals("", p("{{{1}}}", "{{:%s|" + s + "}}"));
		assertEquals(s, p("{{{1}}}", "{{:%s|1=" + s + "}}"));
	}
	
	@Test
	public void testResolving() throws Exception {
		assertEquals("one", p("{{{1}}}", "{{:%s|one}}"));
//		assertEquals("{{{1}}}", p("{{{1}}}", "{{:%s}}")); // Really desired?
	}
	
	@Test
	public void testPTMCdata() throws Exception {
		assertEquals("Arguments <o|ne> <two>.", p("Arguments <{{{1}}}> <{{{2}}}>.", "{{:%s|o<![CDATA[|]]>ne|two}}"));
	}
	
	@Test
	public void testForwardState() throws Exception {
		assertEquals(".:#", p2(":{{:%s}}", "#", ".{{:%s}}"));
		assertEquals(".", p2("{{:%s|FORWARD_STATE}}", "{{{1}}}", "{{:%s|.}}"));
		assertEquals(".nr2", p2("{{:%s|FORWARD_STATE|nr2}}", "{{{1}}}{{{2}}}", "{{:%s|.}}"));
		assertEquals("argument a", p2("{{:%s|FORWARD_STATE|nr2}}", "{{{a}}}", "{{:%s|.|a=argument a}}"));
		assertEquals("argument b", p2("{{:%s|FORWARD_STATE|a=argument b}}", "{{{a}}}", "{{:%s|.|a=argument a}}"));
	}
	
	@Test
	public void testPTMRecursion1() throws Exception {
		File f = File.createTempFile("tpl", ".txt");
		File f2 = File.createTempFile("tpl", ".txt");
		f.deleteOnExit();
		f2.deleteOnExit();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(String.format("First: {{:%s|{{{1}}}}}", f2.getAbsolutePath()));
		bw.close();
		bw = new BufferedWriter(new FileWriter(f2));
		bw.write(">>>{{{1}}}<<<");
		bw.close();

		StringBuffer sb = new StringBuffer(String.format("Recursion: {{:%s|yes}}", f.getAbsolutePath()));
		assertEquals("Recursion: First: >>>yes<<<", new PTMRootNode(sb, null).evaluate());
	}
	
	@Test
	public void testPTMDeepValues() throws Exception {
		File f = File.createTempFile("tpl", ".txt");
		File f2 = File.createTempFile("tpl", ".txt");
		f.deleteOnExit();
		f2.deleteOnExit();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(String.format("{{:%s|val{{{1}}}val}}", f2.getAbsolutePath()));
		bw.close();
		bw = new BufferedWriter(new FileWriter(f2));
		bw.write("{{{1}}}");
		bw.close();

		StringBuffer sb = new StringBuffer(String.format("{{:%s|VAL}}", f.getAbsolutePath()));
		assertEquals("valVALval", new PTMRootNode(sb, null).evaluate());
	}
	
	@Test
	public void testPTMRecursion2() throws Exception {
		File f = File.createTempFile("tpl", ".txt");
		File f2 = File.createTempFile("tpl", ".txt");
		f.deleteOnExit();
		f2.deleteOnExit();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(String.format("a{{:%s|{{{1}}}}}", f2.getAbsolutePath()));
		bw.close();
		bw = new BufferedWriter(new FileWriter(f2));
		bw.write(String.format("{{{1}}}{{:%s|{{{1}}}}}", f.getAbsolutePath()));
		bw.close();

		StringBuffer sb = new StringBuffer(String.format("{{:%s|b}}", f.getAbsolutePath()));
		new PTMRootNode(sb, null).evaluate();
		// No assertion required. If recursion is not detected, this method will just fail.
	}
	
	@Test
	public void testDebugOutput() throws Exception {
		// This is a simple check that merely tests if the debug function has been processed.
		assertTrue(p("{{#debug:list}}", "{{:%s|one|two}}").contains("Debug information"));
	}
	
	
	static final String p(String tpl, String txt) throws IOException, RecursionException {
		File f = File.createTempFile("tpl", ".txt");
		f.deleteOnExit();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(tpl);
		bw.close();
		
		StringBuffer sb = new StringBuffer(String.format(txt, f.getAbsolutePath()));
		return new PTMRootNode(sb, null).evaluate();
	}
	
	private static final String p2(String tplDepth1, String tplDepth2, String txt) throws IOException, RecursionException {
		
		BufferedWriter bw;
		
		File f2 = File.createTempFile("tpl", ".txt");
		f2.deleteOnExit();
		bw = new BufferedWriter(new FileWriter(f2));
		bw.write(tplDepth2);
		bw.close();
		
		File f1 = File.createTempFile("tpl", ".txt");
		f1.deleteOnExit();
		bw = new BufferedWriter(new FileWriter(f1));
		bw.write(String.format(tplDepth1, f2.getAbsolutePath()));
		bw.close();
		
		
		StringBuffer sb = new StringBuffer(String.format(txt, f1.getAbsolutePath(), f2.getAbsolutePath()));
		return new PTMRootNode(sb, null).evaluate();
		
	}
	
	
}
