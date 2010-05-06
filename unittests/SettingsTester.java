package unittests;

import org.junit.Test;

import src.project.settings.Settings;
import src.project.settings.Settings.Checker;

public class SettingsTester extends junit.framework.TestCase {
	
	/** @return true if starts with "a" */
	private final Checker<String> c = new Checker<String>() {
		public boolean check(String value) {
			return value.startsWith("a");
		}
	};
	
	public enum Tst {
		a,b,c;
	}
	
	@Test public void testChecker() {
		Settings<Tst, String> stgs = new Settings<Tst,String>() {
			public String nullValue() {
				return null;
			}
		};
		stgs.addChecker(c, Tst.a);

		assertFalse(stgs.set_(Tst.a, "cabc"));
		assertTrue(stgs.set_(Tst.a, "abc"));
		assertTrue(stgs.set_(Tst.b, "cabc"));
	}
	
	@Test public void testSetter() {
		Settings<Tst, String> stgs = new Settings<Tst,String>() {
			public String nullValue() {
				return null;
			}
		};
		stgs.addChecker(c, Tst.a);
		
		assertFalse(stgs.set_(Tst.a, "cabc"));
		assertEquals(null, stgs.get_(Tst.a));
		assertTrue(stgs.set_(Tst.a, "abc"));
		assertEquals("abc", stgs.get_(Tst.a));
		assertFalse(stgs.set_(Tst.a, "cabc"));
		assertEquals("abc", stgs.get_(Tst.a));
		assertTrue(stgs.set_(Tst.b, "hello"));
		assertEquals("hello", stgs.get_(Tst.b));
		assertTrue(stgs.set_(Tst.b, null));
		assertEquals(null, stgs.get_(Tst.b));
	}
	
	@Test public void testGetter() {
		final String mynull = "nothing!";
		Settings<Tst, String> stgs = new Settings<Tst,String>() {
			public String nullValue() {
				return mynull;
			}
		};
		
		assertNull(stgs.get_(Tst.a));
		stgs.set_(Tst.a, "hallo");
		assertEquals("hallo", stgs.get_(Tst.a));
		assertTrue(stgs.set_(Tst.a, mynull));
		assertNull(stgs.get_(Tst.a));
	}
}
