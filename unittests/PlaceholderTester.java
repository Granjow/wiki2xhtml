package unittests;

import org.junit.Test;

import src.utilities.Placeholder;

public class PlaceholderTester extends junit.framework.TestCase {

	@Test
	public void testRemovePlaceholder() {
		assertEquals("<ul></ul>", pr("<ul>content</ul>", "ul"));
		assertEquals("a<ul></ul>b", pr("a<ul>content</ul>b", "ul"));
		assertEquals("a<ul></ul>b", pr("a<ul>content<ul>more</ul></ul>b", "ul"));
	}
	
	@Test
	public void testInsertPlaceholder() {
		assertEquals("<ul>content</ul>", p("<ul>content</ul>", "ul"));
		assertEquals("a<ul>content</ul>b", p("a<ul>content</ul>b", "ul"));
		assertEquals("a<ul>content<ul>more</ul></ul>b", p("a<ul>content<ul>more</ul></ul>b", "ul"));
		
	}

	private static final String pr(final String content, final String tag) {
		return new Placeholder("<" + tag + "(?:\\s[^>]*>|>)", "</" + tag + ">").removeContent(new StringBuffer(content)).toString();
	}
	private static final String p(final String content, final String tag) {
		Placeholder p = new Placeholder("<" + tag + "(?:\\s[^>]*>|>)", "</" + tag + ">");
		return p.insertContent(p.removeContent(new StringBuffer(content))).toString();
	}
	
}
