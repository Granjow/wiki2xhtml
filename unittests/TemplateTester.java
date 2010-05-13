package unittests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class TemplateTester extends junit.framework.TestCase {

	@Test
	public void testRecursion1() throws Exception {
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
		
		VirtualWikiFile vf = new VirtualWikiFile(null, "b", false, true, sb);
		vf.removeAllTasks();
		vf.addTask(Task.Templates);
		vf.parse();
		System.out.println(vf.getContent());
	}
	
	@Test
	public void testSimpleArgs() throws Exception {
		assertEquals("Arguments <one> <two>.", parseTemplate("Arguments <{{{1}}}> <{{{2}}}>.", "{{:%s|one|two}}"));
	}
	
	@Test
	public void testCdata() throws Exception {
		assertEquals("Arguments <o|ne> <two>.", parseTemplate("Arguments <{{{1}}}> <{{{2}}}>.", "{{:%s|o<![CDATA[|]]>ne|two}}"));
	}
	
	public static String parseTemplate(String tpl, String arg) throws IOException {
		File f = File.createTempFile("tpl", ".txt");
		f.deleteOnExit();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(tpl);
		bw.close();
		
		StringBuffer sb = new StringBuffer(String.format(arg, f.getAbsolutePath()));
		
		VirtualWikiFile vf = new VirtualWikiFile(null, "b", false, true, sb);
		vf.removeAllTasks();
		vf.addTask(Task.Templates);
		vf.parse();
		return vf.getContent().toString();
	}
	
}
