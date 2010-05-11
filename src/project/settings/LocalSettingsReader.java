package src.project.settings;

import java.util.regex.Matcher;

import src.resources.RegExpressions;
import src.resources.ResProjectSettings.SettingsLocalE;

public class LocalSettingsReader {

	private final SettingsReader<SettingsLocalE, String> reader;
	
	public LocalSettingsReader(final StringBuffer content, final LocalSettings settings) {
		reader = new SettingsReader<SettingsLocalE, String>(content, settings);
		reader.attachReader(rRedirect);
	}
	
	public final void read(boolean remove) {
		reader.readSettings(remove);
	}
	
	private static final SettingReader<SettingsLocalE, String> rRedirect = new SettingReader<SettingsLocalE, String>() {
		public boolean read(Settings<SettingsLocalE, String> settings, StringBuffer in, boolean remove) {
			Matcher m = RegExpressions.redirect.matcher(in);
			String redirect = null;

			if (m.find()) {
				redirect = "<meta http-equiv=\"refresh\" " +
						   "content=\"" + m.group(1) + "; " + "url=" + m.group(2) + " \" />";
				if (remove) {
					in.delete(m.start(), m.end());
				}
				settings.set_(SettingsLocalE.redirect, redirect);
			}
			return redirect != null;
		}
		
		public String getID() {
			return "Redirect";
		}
	};
	
}
