package unittests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import src.resources.RegExpressions;

public class RegexTester extends junit.framework.TestCase {

	
	@Test public void testNumber() {
		Pattern p = RegExpressions.num;
		Matcher m;
		m = p.matcher("abc");
		assertFalse(m.find());
		m = p.matcher("1abc");
		assertTrue(m.find());
		assertEquals("1", m.group(0));
		m = p.matcher("abc1");
		assertTrue(m.find());
		assertEquals("1", m.group(0));
		m = p.matcher("123abc1");
		assertTrue(m.find());
		assertEquals("123", m.group(0));
	}
	
}
