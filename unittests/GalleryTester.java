package unittests;

import java.io.IOException;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.tasks.Tasks.Task;

public class GalleryTester extends junit.framework.TestCase {
	
	private static final String container = ":{{#if:{{{caption|}}}|c={{{caption}}}}}{{#if:{{{widths|}}}|w={{{widths}}}}}" +
			"{{#if:{{{heights|}}}|h={{{heights}}}}}{{#if:{{{perrow|}}}|r={{{perrow}}}}}:{{{content|}}}::";
	private static final String template = "{{#if:{{{path|}}}|{{{path}}}}}{{#ifeq:{{{rowEnd|}}}|true|-}}";

	@Test
	public void testSimpleGallery() throws IOException {
		final String container = ":{{{content|}}}:";
		assertEquals(":img.jpg:", p("<gallery>\nImage:img.jpg\n</gallery>", template, container));
		assertEquals("<gallery>", p("<gallery>", template, container));
	}
	
	@Test
	public void testCaption() throws IOException {
		assertEquals(":c=my caption:img.jpg::", p("<gallery caption=\"my caption\">\nImage:img.jpg\n</gallery>", template, container));
	}
	
	@Test
	public void testWidths() throws IOException {
		assertEquals(":w=300px:img.jpg::", p("<gallery widths=\"300px\">\nImage:img.jpg\n</gallery>", template, container));
		assertEquals("::img.jpg::", p("<gallery widths=\"300\">\nImage:img.jpg\n</gallery>", template, container));
	}
	
	@Test
	public void testHeights() throws IOException {
		assertEquals(":h=200px:img.jpg::", p("<gallery heights=\"200px\">\nImage:img.jpg\n</gallery>", template, container));
	}
	
	@Test
	public void testPerrow() throws IOException {
		final String perrowTemplate = "{{#if:{{{rowStart|}}}|s}}{{#if:{{{rowEnd|}}}|e}}{{#if:{{{rowStart|}}}{{{rowEnd|}}}||-}};";
		assertEquals(":r=2:img.jpg::", p("<gallery perrow=\"2\">\nImage:img.jpg\n</gallery>", template, container));
		assertEquals(":r=2:s;e;s;::", p("<gallery perrow=\"2\">\nImage:img.jpg\nImage:img.jpg\nImage:img.jpg\n</gallery>", perrowTemplate, container));
		assertEquals(":r=1:se;se;se;::", p("<gallery perrow=\"1\">\nImage:img.jpg\nImage:img.jpg\nImage:img.jpg\n</gallery>", perrowTemplate, container));
	}
	
	@Test
	public void testText() throws IOException {
		final String textTemplate = "{{{path|}}};{{#if:{{{text|}}}|t={{{text}}}}}";
		assertEquals("::img.jpg;t=Hello gallery!::", p("<gallery>\nImage:img.jpg|Hello gallery!\n</gallery>", textTemplate, container));
	}
	

	private static final String p(String testString, String templateGallery, String templateContainer) throws IOException {
		TestObject to = new TestObject(testString, "");
		to.writeFile("tplGallery.txt", templateGallery);
		to.writeFile("tplGalleryContainer.txt", templateContainer);
		return to.real();
	}
	
	private static class TestObject extends unittests.TestObject {
		public TestObject(String test, String result) throws IOException {
			super(test, result);
		}

		void fillTasks(VirtualWikiFile vf) {
			vf.addTask(Task.Galleries);
		}

		public String real() throws IOException {
			return super.real().replace("\n", "").replace(" <", "<");
		}
	}
	
}
