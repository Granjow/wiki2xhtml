package unittests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import src.ptm.PTMRootNode;

public class TemplateTester extends junit.framework.TestCase {
	
	@Test
	public void testPTMSimpleReplace() throws IOException {
		assertEquals("I am a template.", p("I am a template.", "{{:%s}}"));
	}

	@Test
	public void testPTMArguments() throws IOException {
		assertEquals("Arguments <one> <two>.", p("Arguments <{{{1}}}> <{{{2}}}>.", "{{:%s|one|two}}"));
		assertEquals("Argument <two>.", p("Argument <{{{3|{{{2|}}}}}}>.", "{{:%s|one|two}}"));
	}
	
	@Test
	public void testPTMCdata() throws Exception {
		assertEquals("Arguments <o|ne> <two>.", p("Arguments <{{{1}}}> <{{{2}}}>.", "{{:%s|o<![CDATA[|]]>ne|two}}"));
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
	
	
	public static final String p(String tpl, String txt) throws IOException {
		File f = File.createTempFile("tpl", ".txt");
		f.deleteOnExit();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(tpl);
		bw.close();
		
		StringBuffer sb = new StringBuffer(String.format(txt, f.getAbsolutePath()));
		return new PTMRootNode(sb, null).evaluate();
	}
	
	
}
