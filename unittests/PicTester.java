package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.project.settings.ImageProperties;
import src.tasks.Tasks.Task;

public class PicTester extends junit.framework.TestCase {
	

	@Test
	public void testSimpleImages() throws IOException {
		assertEquals("<img src=\"img.jpg\"/>", p("[[Image:img.jpg]]"));
		assertEquals("<img src=\"img.jpg\"/>", p("[[Image:img.jpg|Image:img2.jpg]]"));
		assertEquals("a<img src=\"img.jpg\"/>b", p("a[[Image:img.jpg]]b"));
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
		final String template = "{{#ifeq:{{{type}}}|thumb|t=thumb}}{{#if:{{{direct}}}|d={{{direct}}}}}{{#if:{{{thumb|}}}|p={{{thumb}}}}}";
		
		assertEquals("t=thumbp=img.jpg", p("[[Image:img.jpg|thumb]]", template));
		assertEquals("p=/some/where", p("[[Image:img.jpg|thumb=/some/where]]", template));
		assertEquals("t=thumbd=truep=img.jpg", p("[[Image:img.jpg|thumb|direct]]", template));
	}
	
	@Test
	public void testPathPatterns() {
		final String dirPattern = "%n|%f|%d|%b|%e";
		final String samplePath = "thumbs/%d/%f";
		assertEquals("img.jpg|img.jpg||img|.jpg", ImageProperties.getThumbnailSource("img.jpg", dirPattern));
		assertEquals("/my/dir/img.jpg|img.jpg|/my/dir|img|.jpg", ImageProperties.getThumbnailSource("/my/dir/img.jpg", dirPattern));
		assertEquals("thumbs/dir/img.jpg", ImageProperties.getThumbnailSource("dir/img.jpg", samplePath));
	}
	
	@Test
	public void testThumbPath() throws IOException {
		final String template = "{{{thumb|--{{{path}}}}}}";
		assertEquals("--img.jpg", ps("[[Image:img.jpg]]", template));
		assertEquals("--img.jpg", ps("{{Thumbnails:thumbs/%f}}[[Image:img.jpg]]", template));
		assertEquals("img.jpg", ps("[[Image:img.jpg|thumb]]", template));
		assertEquals("imgThumb.jpg", ps("[[Image:img.jpg|thumb=imgThumb.jpg]]", template));
		assertEquals("thumbs/img.jpg", ps("{{Thumbnails:thumbs/%f}}[[Image:img.jpg|thumb]]", template));
		assertEquals("imgThumb.jpg", ps("{{Thumbnails:thumbs/%f}}[[Image:img.jpg|thumb=imgThumb.jpg]]", template));
	}
	
	@Test
	public void testImagePath() throws IOException {
		final String template = "{{{thumb|--{{{path}}}}}}";
		assertEquals("--images/img.jpg", ps("{{DirImages:images}}[[Image:img.jpg]]", template));
		assertEquals("--./img.jpg", ps("{{DirImages:images}}[[Image:./img.jpg]]", template));
	}
	
	@Test
	public void testLink() throws IOException {
		final String template = "{{#if:{{{link|}}}|l={{{link}}}}}";
		assertEquals("l=linked", p("[[Image:img.jpg|link=linked]]", template));
		assertEquals("l=imagePages/this-File.php_to_img.jpg.html", p("[[Image:img.jpg]]", template));
		assertEquals("l=img.jpg", p("[[Image:img.jpg|direct]]", template));
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
		
		final String template2 = "<a href=\"{{{path}}}\"{{#if:{{{linkArg|}}}| {{{linkArg}}}}}>img</a>";
		assertEquals("<a href=\"img.jpg\" rel=\"shadowbox[abc]\">img</a>", p("[[Image:img.jpg|linkArg=rel=\"shadowbox[abc]\"]]", template2));
	}
	
	@Test
	public void testText() throws IOException {
		final String template = "{{#if:{{{text|}}}|text={{{text}}}}}";
		assertEquals("text=this is text.", p("[[Image:img.jpg|this is text.]]", template));
		assertEquals("text=more&#124;text", p("[[Image:img.jpg|more|text]]", template));
	}
	
	@Test
	public void testLinks() throws IOException {
		final String template = "{{#if:{{{text|}}}|text={{{text}}}}}";
		assertEquals("text=this is a [[link]]", p("[[Image:img.jpg|this is a [[link]]]]", template));
		assertEquals("text=The <a href=\"http://vimeo.com/10756036\" class=\"external\">Rusto Fat</a>.", 
				pl("[[Image:rusto-fat.jpg|The <a href=\"http://vimeo.com/10756036\" class=\"external\">Rusto Fat</a>.]]", template));
		
	}
	
	@Test
	public void testClear() throws IOException {
		final String template = "{{#if:{{{clear|}}}|clear:{{{clear}}}}}";
		assertEquals("clear:both", p("[[Image:img.jpg|clear=both]]", template));
		assertEquals("clear:left", p("[[Image:img.jpg|clear=left]]", template));
		assertEquals("clear:right", p("[[Image:img.jpg|clear=right]]", template));
	}
	
	

	
	private static final String ps(String testString, String template) throws IOException {
		TestObject2 to = new TestObject2(testString);
		to.writeFile("tplImage.txt", template);
		return to.real();
	}

	private static final String p(String testString, String template) throws IOException {
		TestObject to = new TestObject(testString, "");
		to.writeFile("tplImage.txt", template);
		return to.real();
	}
	private static final String pl(String testString, String template) throws IOException {
		TestObject to = new TestObject(testString, "", true);
		to.writeFile("tplImage.txt", template);
		return to.real();
	}
	private static final String p(String testString) throws IOException {
		final String template1 = "<img src=\"{{{path}}}\"/>";
		return p(testString, template1);
	}
	
	private static class TestObject extends unittests.TestObject {
		
		private final boolean withLinks;
		public TestObject(String test, String result) throws IOException {
			this(test, result, false);
		}
		public TestObject(String test, String result, boolean withLinks) throws IOException {
			super(test, result);
			this.withLinks = withLinks;
		}

		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Images);
			if (withLinks) {
				vf.addTask(Task.Links);
			}
		}

		public String real() throws IOException {
			return super.real().replace("\n", "");
		}
	}
	private static class TestObject2 extends TestObject {
		public TestObject2(String test) throws IOException {
			super(test, "");
		}
		void fillTasks(VirtualWikiFile vf) {
			super.fillTasks(vf);
			vf.addTask(Task.Settings);
		}
	}
	
}
