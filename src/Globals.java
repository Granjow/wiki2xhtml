package src;

import java.util.Locale;
import java.util.ArrayList;

import src.programSettings.ProgramSettings;
import src.utilities.LanguageTools;

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
 * Contains global variables
 *
 * @since wiki2xhtml 3.3
 *
 * @author Simon Eugster
 */
public abstract class Globals {

	/** Program settings */
	public static ProgramSettings programSettings = new ProgramSettings();

	public static GUI guiManager = null;
	public static GUI getGuiManager() throws java.awt.HeadlessException {
		if (guiManager == null)
			guiManager = new GUI();
		return guiManager;
	}

	
	/** Other storage */
	public static ArrayList<String> templateWarnings = new ArrayList<String>();


	/** Language */
	private static Locale language = null;

	/** Change language */
	public static final void setLocale(Locale l) {
		language = l;
		ConstantTexts.updateLocale();
		if (programSettings.readSettings()) {
			programSettings.set(Constants.ProgramSettings.locale, l.getLanguage());
		}
	}

	/** Change language */
	public static final void setLocale(String l) {
		setLocale(LanguageTools.getLocale(l));
	}

	/** @return Current language (or default) */
	public static final Locale getLocale() {

		if (language == null) {
			if (programSettings.readSettings()) {
				setLocale(programSettings.settings.get(Constants.ProgramSettings.locale));
			} else {
				setLocale(Constants.Standards.locale);
			}
		}

		return language;
	}

}
