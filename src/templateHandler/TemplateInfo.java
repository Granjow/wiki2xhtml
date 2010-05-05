package src.templateHandler;

import java.util.regex.Matcher;

import src.Resources;

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
 * Provides informations about a template
 *
 * @author Simon Eugster
 */
public class TemplateInfo {

	/**
	 * @param templateArgs
	 * @return The first argument of pipe-separated arguments,
	 * which should usually be the template name
	 */
	public static String getTemplateName(String templateArgs) {
		String name = "";

		Matcher m = Resources.Regex.argument.matcher(templateArgs);

		if (m.find())
			name = m.group(1);

		return name;
	}

}
