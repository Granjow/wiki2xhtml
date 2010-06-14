package unittests;

import java.io.File;

import org.junit.Test;

import src.project.settings.PageSettings;
import src.resources.ResProjectSettings.SettingsE;

public class WikiSettingsTester extends junit.framework.TestCase {

	@Test
	public void testPreparser() {
		PageSettings ps = new PageSettings();
		ps.set_(SettingsE.imagepagesDir, "images");
		assertEquals("images" + File.separator, ps.get_(SettingsE.imagepagesDir));
	}
	
}
