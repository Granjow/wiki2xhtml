package src.project.settings;

import src.resources.ResProjectSettings.SettingsE;

/*
 *   Copyright (C) 2007-2010 Simon Eugster <granjow@users.sf.net>

 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this http://www.gnu.org/licenses/.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

/**
 * Contains global settings that are valid for all pages.
 */
public class PageSettings extends StringSettings<SettingsE> {
	
	public PageSettings() {
		// Add checkers to validate input
		for (SettingsE p : SettingsE.values()) {
			if (p.checker != null) { addChecker(p.checker, p); }
			if (p.preparser != null) { addPreparser(p.preparser, p); }
		}
	}
	
	public String nullValue() {
		return "null";
	}

}
