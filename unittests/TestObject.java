package unittests;


import src.project.file.VirtualWikiFile;

abstract class TestObject {

	public static String thisFile() { return "this-File.php"; }
	public static String a = "b";
	
	private final StringBuffer input;
	private final String output;
	public TestObject(String test, String result) {
		input = new StringBuffer(test);
		output = result;
	}
	public String correct() {
		return output;
	}
	public String real() {
		VirtualWikiFile vf = new VirtualWikiFile(null, thisFile(), false, true, input);
		vf.removeAllTasks();
		fillTasks(vf);
		vf.parse();
		return vf.getContent().toString();
	}
	
	abstract void fillTasks(VirtualWikiFile vf);
}
