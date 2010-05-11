package unittests;


import src.project.file.VirtualWikiFile;
import src.project.settings.PageSettings;
import src.project.settings.PageSettingsReader;

abstract class TestObject {

	public static String thisFile() { return "this-File.php"; }
	public static String a = "b";
	
	private final StringBuffer input;
	private final String output;
	private final PageSettings pageSettings = new PageSettings();
	public TestObject(String test, String result, StringBuffer pageSettings) {
		input = new StringBuffer(test);
		output = result;
		PageSettingsReader r = new PageSettingsReader(pageSettings, this.pageSettings);
		r.readSettings(false);
	}
	public TestObject(String test, String result) {
		input = new StringBuffer(test);
		output = result;
	}
	public String correct() {
		return output;
	}
	public String real() {
		VirtualWikiFile vf = new VirtualWikiFile(null, thisFile(), false, true, input);
		vf.setPageSettings(pageSettings);
		vf.removeAllTasks();
		fillTasks(vf);
		vf.parse();
		return vf.getContent().toString();
	}
	
	abstract void fillTasks(VirtualWikiFile vf);
}
