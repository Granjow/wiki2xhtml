package src.project.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.Constants;
import src.Statistics;
import src.project.WikiProject;
import src.project.settings.ImageProperties;
import src.resources.ResProjectSettings.EImageProperties;
import src.resources.ResProjectSettings.SettingsE;
import src.resources.ResProjectSettings.SettingsLocalE;
import src.tasks.WikiHeadings;
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
	
	private static I18n i18n = I18nFactory.getI18n(Generators.class, "bin.l10n.Messages", src.Globals.getLocale());
	
	private final WikiFile file;
	
	public Generators(WikiFile file) {
		this.file = file;
	}

	/**
	 * Creates a TOC from an input file. The <hx> have to be at the beginning of
	 * the line!
	 */	
	public StringBuffer getTOC() {
		StringBuffer toc = new StringBuffer();

		BufferedReader b = new BufferedReader(new StringReader(file.getContent().toString()));
		byte counter = 0;
		byte level = 0;

		try {
			Pattern pat = Pattern.compile("^<h[2-6]");

			for (String line = b.readLine(); line != null; line = b.readLine()) {
				if (line.length() > 0 && pat.matcher(line).find()) {
					try {
						level = Byte.parseByte(line.charAt(2) + "");
						level--;	// Headings start with level 2
					} catch (NumberFormatException e) {
						e.getStackTrace();
					}
					toc.append(getTOCItem(counter, level, 
							line.subSequence(line.indexOf(">") + 1, line.lastIndexOf("<")).toString(), 
							line));
					counter++;
				}
			}

		} catch (IOException e)  {
			e.printStackTrace();
		}

		/* Statistics */
		Statistics.getInstance().counter.TOC.increase(counter);
		return toc;
	}


	/**
	 * Creates an item (ordered list entry, can finally be converted with 
	 * WikiLists) for the TOC with the generated ID
	 *
	 * @param n The current number of the heading
	 * @param level The heading's level
	 * @param name The content of the heading. Tags will be removed.
	 * @param line Line containing the heading entry, to search for an ID if existing 
	 * @return Item with the generated ID
	 */
	public static final StringBuffer getTOCItem(int n, int level, String name, String line) {
		StringBuffer item = new StringBuffer();
		Pattern pat = Pattern.compile("(?<=id=\").*?(?=\")");
		Matcher m = pat.matcher(line);

		name = name.replaceAll("<.*>", "");

		item.append("\n");
		for (byte i = 0; i < level; i++)
			item.append("#");

		item.append("<a href=\"#");

		if (m.find())
			item.append(m.group());
		else
			item.append(WikiHeadings.getIDtoHeading(name, null));

		item.append("\">" + name + "</a>");

		return item;
	}
	
	/**
	 * @return The page title, replacing %s and %p by standard title and page number
	 */
	public String title() {
		String title = file.getProperty(SettingsE.title, false);
		if (title == null) {
			title = file.getProperty(SettingsE.h1, true);
		} else {
			title = title(title);
		}
		if (title == null) title = "";
		return title;
	}
	
	/**
	 * @param pattern Input title
	 * @return The pattern with %s and %p replaced; see {@link #title()}
	 */
	public String title(String pattern) {
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
					+ page(DisplayRule.pageXofY)
					+ title.substring(pos + 2, title.length());
		}
		return title;
	}
	
	public static enum DisplayRule {
		pageXofY, pageX, X;
	}
	public String page(final DisplayRule displayRule) {
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
