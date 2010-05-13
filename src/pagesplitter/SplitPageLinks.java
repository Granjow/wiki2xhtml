package src.pagesplitter;

import java.util.Locale;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
 * Handles linking between split pages
 *
 * @author Simon Eugster
 */
public class SplitPageLinks {

	private static final I18n i18nP = I18nFactory.getI18n(SplitPageLinks.class, "bin.l10n.Messages", src.Globals.getLocale());

	String name = null, ext = null;

	private SplitPageLinks() { }
	private static SplitPageLinks spl = new SplitPageLinks();

	public static SplitPageLinks getInstance() {
		return spl;
	}

	public void setup(String name, String ext) {
		SplitPageContainer.getInstance().currentPageNumber = 1;
		this.name = name;
		this.ext = ext;
	}

	public void setLang(Locale l) {
		i18nP.setLocale(l);
	}

	public String getLink(int i, int currentPage) {
		if (i == currentPage)
			return "#";
		else {
			if (i <= 1 || i > SplitPageContainer.getInstance().getPageNumber())
				return name + ext;
			else
				return name + i + ext;
		}
	}
	public String getLink(int i) {
		if (i <= 1 || i > SplitPageContainer.getInstance().getPageNumber())
			return name + ext;
		else
			return name + i + ext;
	}

	public String getLinkList() {
		if (name == null || ext == null)
			return "";
		else {
			String links = "<div class=\"multipage\"><ul><li>" + i18nP.tr("Go to page") + ":</li>";
			for (int i = 0; i < SplitPageContainer.getInstance().getPageNumber(); i++) {
				if ((i+1) == SplitPageContainer.getInstance().currentPageNumber)
					links += "<li><b>[" + (i+1) + "]</b></li> ";
				else
					links += "<li><a href=\"" + getLink(i+1) + "\">[" + (i+1) + "]</a></li> ";
			}
			links += "</ul></div>";

			return links;
		}
	}

}
