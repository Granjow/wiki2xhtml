package src.project.settings;

import java.util.regex.Matcher;

import src.resources.RegExpressions;
import src.resources.ResProjectSettings.SettingsLocalE;

public class LocalSettingsReader {

	private final SettingsReader<SettingsLocalE, String> reader;
	
	public LocalSettingsReader(final StringBuffer content, final LocalSettings settings) {
		reader = new SettingsReader<SettingsLocalE, String>(content, settings);
	}
	
	private static final String getRedirect(StringBuffer content, boolean remove) {
		// TODO make reader
		Matcher m = RegExpressions.redirect.matcher(content);
		String redirect = "";

		if (m.find()) {
			redirect = "<meta http-equiv=\"refresh\" " +
					   "content=\"" + m.group(1) + "; " + "url=" + m.group(2) + " \" />";
			if (remove) {
				content.delete(m.start(), m.end());
			}
		}
		return redirect;
	}
	
}
