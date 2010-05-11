package unittests;

import org.junit.Test;

import src.project.file.VirtualWikiFile;
import src.project.settings.LocalSettings;
import src.project.settings.LocalSettingsReader;
import src.project.settings.Settings;
import src.project.settings.Settings.Checker;
import src.resources.ResProjectSettings.SettingsLocalE;
import src.tasks.Tasks.Task;

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
	
	@Test public void testRedirect() {
		StringBuffer sb = new StringBuffer("#REDIRECT 1 nowhere.html\nHallo");
		LocalSettings settings = new LocalSettings();
		LocalSettingsReader reader = new LocalSettingsReader(sb, settings);
		reader.read(true);
		assertEquals("Hallo", sb.toString().trim());
		assertEquals("<meta http-equiv=\"refresh\" content=\"1; url=nowhere.html \" />", 
				settings.get_(SettingsLocalE.redirect));
		System.out.println(settings.get_(SettingsLocalE.redirect));
	}
	
	@Test public void testRecognition() {
		StringBuffer sb = new StringBuffer("{{Namespace:a=b}}\nhall{{Author:him}}o");
		
		VirtualWikiFile vf = new VirtualWikiFile(null, "a.html", false, true, sb);
		vf.removeAllTasks();
		vf.addTask(Task.Settings);
		vf.parse();
		
		assertEquals("hallo", vf.getContent().toString().trim());
	}
	
	@Test public void testChecker() {
		Settings<Tst, String> stgs = new Settings<Tst,String>() {
			public String nullValue() {
				return null;
			}
			protected String concatenate(String left, String right) {
				return left+right;
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
			protected String concatenate(String left, String right) {
				return left+right;
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
			protected String concatenate(String left, String right) {
				return left+right;
			}
		};
		
		assertNull(stgs.get_(Tst.a));
		stgs.set_(Tst.a, "hallo");
		assertEquals("hallo", stgs.get_(Tst.a));
		assertTrue(stgs.set_(Tst.a, mynull));
		assertNull(stgs.get_(Tst.a));
	}
}
