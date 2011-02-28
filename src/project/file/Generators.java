package src.project.file;

import java.io.IOException;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.Constants;
import src.project.WikiProject;
import src.project.settings.ImageProperties;
import src.resources.ResProjectSettings.EImageProperties;
import src.resources.ResProjectSettings.SettingsE;
import src.resources.ResProjectSettings.SettingsLocalE;
import src.tasks.WikiImages;

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
 *
 */

/**
 * Retrieve information from a WikiFile
 */
public class Generators {
	
	
	public Generators(WikiFile file) {
	}
	
	/**
	 * @return The page title, replacing %s and %p by standard title and page number
	 */
	public String title(final WikiFile file) {
		String title = file.getProperty(SettingsE.title, false);
		if (title == null) {
			title = file.getProperty(SettingsE.h1, true);
		} else {
			title = title(file, title);
		}
		if (title == null) title = "";
		return title;
	}
	
	/**
	 * @param pattern Input title
	 * @return The pattern with %s and %p replaced; see {@link #title()}
	 */
	public String title(final WikiFile file, final String pattern) {
		String title = pattern;
		int pos = title.indexOf(Constants.Tags.Title.titleTag);
		if (pos >= 0) {
			String defaultTitle = file.project.getProperty(SettingsE.title);
			if (defaultTitle == null) defaultTitle = "";
			title = title.substring(0, pos)
					+ defaultTitle
					+ title.substring(pos + 2, title.length());
		}

		pos = title.indexOf(Constants.Tags.Title.pageTag);
		if (pos >= 0) {
			title = title.substring(0, pos)
					+ page(file, DisplayRule.pageXofY)
					+ title.substring(pos + 2, title.length());
		}
		return title;
	}
	
	public static enum DisplayRule {
		pageXofY, pageX, X;
	}
	public String page(final WikiFile file, final DisplayRule displayRule) {
		
		I18n i18n = I18nFactory.getI18n(Generators.class, "bin.l10n.Messages", file.getLocale());
		
		String page;
		String number = file.getProperty(SettingsLocalE.pageNumber);
		String totalPages = file.getProperty(SettingsLocalE.pagesTotal);
		switch (displayRule) {
		case pageXofY:
			page = i18n.tr("Page {0} of {1}", number, totalPages);
			break;
		case pageX:
			page = i18n.tr("Page {0}", number);
			break;
		case X:
		default:
			page = number;
			break;
		}
		
		return page;
	}
	
	public static void main(String[] args) throws IOException {
		WikiProject proj = new WikiProject(".");
		ImageProperties prop = new ImageProperties(new VirtualWikiFile(proj, "name", false, false));
		prop.set_(EImageProperties.text, "Text.");
		prop.set_(EImageProperties.path, "test.jpg");
		StringBuffer out = WikiImages.generateThumbnailEntry(prop);
		System.out.println("Output: >>>\n" + out + "\n<<<");
	}

}
