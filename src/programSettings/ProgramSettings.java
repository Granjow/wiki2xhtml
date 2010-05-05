package src.programSettings;

import java.util.HashMap;

import src.Constants;


/*
 *   Copyright (C) 2007-2009 Simon Eugster <granjow@users.sf.net>

 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * Handles program settings, like «check for updates on startup»
 *
 * @author Simon Eugster
 *
 * @since wiki2xhtml 3.3
 *
 */

public class ProgramSettings {

	/**
	 * Contains all settings. Putting a new entry into a HashMap
	 * will replace an existing one if the key already exists.
	 */
	public HashMap<String, String> settings = new HashMap<String, String>();

	public ProgramSettings() {
		initSettings();

	}

	/**
	 * Reads settings from the settings file
	 * @return success
	 */
	public boolean readSettings() {

		initSettings();
		return ProgramSettingsWriter.readSettings(this);

	}

	/**
	 * Writes settings to the settings file
	 * @return success
	 */
	public boolean writeSettings() {

		return ProgramSettingsWriter.writeSettings(this);

	}

	/**
	 * Initializes all settings
	 */
	public void initSettings() {
		settings.clear();
		settings.put(Constants.ProgramSettings.autoUpdate, "false");
		settings.put(Constants.ProgramSettings.lastArgs, "");
		settings.put(Constants.ProgramSettings.locale, Constants.Standards.locale.getLanguage());
		settings.put(Constants.ProgramSettings.ignoreNewVersion, "");
	}

	/**
	 * Checks whether the setting <em>key</em> is true
	 * @param key Setting name
	 */
	public boolean isTrue(String key) {
		return settings.get(key).equals("true");
	}

	/**
	 * Sets <em>key</em> to <em>value</em>.
	 * @param key
	 * @param value
	 */
	public void set(String key, boolean value) {

		if (value)
			settings.put(key, "true");
		else
			settings.put(key, "false");

		writeSettings();

	}

	/**
	 * Sets <em>key</em> to <em>value</em>.
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {

		settings.put(key, value);
		writeSettings();

	}

	public String get(String key) {
		return settings.get(key);
	}

}
