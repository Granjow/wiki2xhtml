package src.project.settings;

import src.project.file.WikiFile;
import src.resources.ResProjectSettings.SettingsE;
import src.utilities.StringTools;

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
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Page specific information storage for images
 */
public class ImageSettings {

	private int galleryCounter;
	private final WikiFile parentFile;
	
	public ImageSettings(final WikiFile parent) {
		this.parentFile = parent;
		galleryCounter = 0;
	}
	
	public String getBacklinkDir() {
		String dirImagepages = parentFile.getProperty(SettingsE.imagepagesDir, true);

		int n = StringTools.countString(dirImagepages, "/");

		if (dirImagepages.startsWith("./")) n--;
		if (!dirImagepages.endsWith("/")) n++;

		StringBuffer backlink = new StringBuffer();

		for (int i = 0; i < n; i++) {
			backlink.append("../");
		}

		return backlink.toString();
	}
	
}
