package unittests;


import java.io.File;
import java.io.IOException;

import src.project.WikiProject;
import src.project.file.VirtualWikiFile;
import src.project.settings.PageSettings;
import src.project.settings.PageSettingsReader;
import src.utilities.IOWrite;

abstract class TestObject {

	public static String thisFile() { return "this-File.php"; }
	public static String a = "b";
	
	public WikiProject project = null;
	
	private final StringBuffer input;
	private final String output;
	private final PageSettings pageSettings = new PageSettings();
	
	public TestObject(String test, String result, StringBuffer pageSettings) throws IOException {
		input = new StringBuffer(test);
		output = result;
		
		if (pageSettings != null) {
			PageSettingsReader r = new PageSettingsReader(pageSettings, this.pageSettings);
			r.readSettings(false);
		}
		project = VirtualWikiFile.createTempProject();
	}
	public TestObject(String test, String result) throws IOException {
		this(test, result, null);
	}
	public String correct() {
		return output;
	}
	public String real() throws IOException {
		VirtualWikiFile vf = new VirtualWikiFile(project, thisFile(), false, input);
		vf.setPageSettings(pageSettings);
		vf.removeAllTasks();
		fillTasks(vf);
		vf.parse();
		return vf.getContent().toString();
	}
	
	public void writeFile(String filename, String content) throws IOException {
		File f = new File(project.projectDirectory().getAbsolutePath() + File.separator + filename);
		f.deleteOnExit();
		IOWrite.writeString(f, content, false);
	}
	
	abstract void fillTasks(VirtualWikiFile vf);
}
