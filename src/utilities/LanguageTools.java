package src.utilities;

import java.util.Locale;

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
 * Some tools for Locales.
 *
 * @author Simon Eugster
 * ,		hb9eia
 */
public class LanguageTools {

	/**
	 * @param s
	 * @return The first language of a comma-separated language list
	 */
	public static String getLanguage(String s) {
		int i;
		String lang;

		if ((i = s.indexOf(',')) > 0)
			lang = s.substring(0, i);
		else
			lang = s;

		return lang;
	}

	/**
	 * @param l
	 * @return The Locale to the string
	 * @throws NullPointerException
	 */
	public static Locale getLocale(String l) throws NullPointerException {
		if (l == null)
			throw new NullPointerException();

		l = l.toLowerCase();
		Locale loc;

		if (l.equalsIgnoreCase("de"))
			loc = Locale.GERMAN;

		else if (l.equalsIgnoreCase("de_ch"))
			loc = new Locale("de", "CH");

		else if (l.equalsIgnoreCase("en"))
			loc = Locale.ENGLISH;

		else if (l.equalsIgnoreCase("en_gb"))
			loc = new Locale("en", "GB");

		else if (l.equalsIgnoreCase("fr"))
			loc = Locale.FRENCH;

		else if (l.equalsIgnoreCase("fr_ch"))
			loc = new Locale("fr", "CH");

		else if (l.equalsIgnoreCase("it"))
			loc = Locale.ITALIAN;

		else if (l.equalsIgnoreCase("it_ch"))
			loc = new Locale("it", "CH");

		else if (l.equalsIgnoreCase("ru"))
			loc = new Locale("ru");

		else if (l.equalsIgnoreCase("es"))
			loc = new Locale("es");

		else if (l.equalsIgnoreCase("hr"))
			loc = new Locale("hr");

		else
			loc = Locale.ENGLISH;

		return loc;

	}

}
