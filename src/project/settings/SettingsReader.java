package src.project.settings;

import java.util.ArrayList;

import src.Constants;

public class SettingsReader {

	// Search for settings (XhtmlSettings)
	
	private static final ArrayList<SettingReader<Constants.SettingsE, String>> pageSettingsReader;
	
	static {
		pageSettingsReader = new ArrayList<SettingReader<Constants.SettingsE, String>>();
	}
	
	// TODO read all page settings
	public void readPageSettings() {
		
	}
	
	// TODO read all local settings
	public void readLocalSettings() {
		
	}

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("a");
		StringBuffer b = sb;
		b.setLength(0);
		b.append("b");
		System.err.println(sb);
	}
	
}
