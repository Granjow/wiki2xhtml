package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class PicTester extends junit.framework.TestCase {
	

	@Test
	public void testSimpleImages() throws IOException {
		assertEquals("<img src=\"img.jpg\"/>", p("[[Image:img.jpg]]"));
		assertEquals("<img src=\"img.jpg\"/>", p("[[Image:img.jpg|Image:img2.jpg]]"));
	}
	
	@Test
	public void testImageSize() throws IOException {
		final String template = "{{#if:{{{width|}}}|w={{{width}}}}}{{#if:{{{height|}}}|h={{{height}}}}}";
		assertEquals("w=100px", p("[[Image:img.jpg|100px]]", template));
		assertEquals("h=100px", p("[[Image:img.jpg|x100px]]", template));
		assertEquals("w=10pxh=100px", p("[[Image:img.jpg|10x100px]]", template));
		assertEquals("", p("[[Image:img.jpg|px|%]]", template));
	}
	
	@Test
	public void testThumb() throws IOException {
		final String template = "{{#ifeq:{{{type}}}|thumb|t=thumb}}{{#ifeq:{{{type}}}|direct|t=direct}}{{#if:{{{thumb|}}}|p={{{thumb}}}}}";
		
		assertEquals("t=thumb", p("[[Image:img.jpg|thumb]]", template));
		assertEquals("t=thumbp=/some/where", p("[[Image:img.jpg|thumb=/some/where]]", template));
		assertEquals("t=direct", p("[[Image:img.jpg|thumb|direct]]", template));
	}
	
	@Test
	public void testPos() throws IOException {
		final String template = "{{#if:{{{location|}}}|h={{{location}}}}}";
		assertEquals("h=left", p("[[Image:img.jpg|left]]", template));
		assertEquals("h=center", p("[[Image:img.jpg|center]]", template));
		assertEquals("h=right", p("[[Image:img.jpg|right]]", template));
	}
	
	@Test
	public void testUserVar() throws IOException {
		final String template1 = "{{#if:{{{varUser1|}}}|u1={{{varUser1}}}}}";
		assertEquals("u1=hello wiki!", p("[[Image:img.jpg|varUser1=hello wiki!]]", template1));
		
		final String template2 = "<a href=\"{{{path}}}\"{{#if:{{{varLinkArg|}}}| {{{varLinkArg}}}}}>img</a>";
		assertEquals("<a href=\"img.jpg\" rel=\"shadowbox[abc]\">img</a>", p("[[Image:img.jpg|varLinkArg=rel=\"shadowbox[abc]\"]]", template2));
	}
	
	@Test
	public void testText() throws IOException {
		final String template = "{{#if:{{{text|}}}|text={{{text}}}}}";
		assertEquals("text=this is text.", p("[[Image:img.jpg|this is text.]]", template));
		assertEquals("text=more&#124;text", p("[[Image:img.jpg|more|text]]", template));
	}
	
	@Test
	public void testLink() throws IOException {
		final String template = "{{#if:{{{link|}}}|l={{{link}}}}}";
		assertEquals("l=linked", p("[[Image:img.jpg|link=linked]]", template));
	}
	
	@Test
	public void testAlt() throws IOException {
		final String template = "{{#if:{{{alt|}}}|alt={{{alt}}}}}";
		assertEquals("alt=nothing", p("[[Image:img.jpg|alt=nothing]]", template));
	}
	
	@Test
	public void testSmall() throws IOException {
		final String template = "{{#if:{{{noscale|}}}|true|false}}";
		assertEquals("true", p("[[Image:img.jpg|noscale]]", template));
		assertEquals("false", p("[[Image:img.jpg]]", template));
	}
	
	@Test
	public void testLongdesc() throws IOException {
		final String template = "{{#if:{{{longdesc|}}}|ld:{{{longdesc}}}}}";
		assertEquals("ld:Long description.", p("[[Image:img.jpg|ld=Long description.]]", template));
	}
	
	@Test
	public void testClear() throws IOException {
		final String template = "{{#if:{{{clear|}}}|clear:{{{clear}}}}}";
		assertEquals("clear:both", p("[[Image:img.jpg|clear]]", template));
		assertEquals("clear:left", p("[[Image:img.jpg|clear=left]]", template));
		assertEquals("clear:right", p("[[Image:img.jpg|clear=right]]", template));
	}
	
	@Test
	public void testCaption() throws IOException {
		final String template = "{{#if:{{{caption|}}}|cap={{{caption}}}}}";
		assertEquals("cap=image title", p("[[Image:img.jpg|caption=image title]]", template));
		assertEquals("cap=image title", p("[[Image:img.jpg|c=image title]]", template));
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
			vf.addTask(Task.Images);
		}

		public String real() throws IOException {
			return super.real().replace("\n", "").replace(" <", "<");
		}
	}
	
}
