package src.pagesplitter;

import java.util.ArrayList;


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
 * Tells wiki2xhtml how to handle some arguments, e.g. after how many images in
 * the gallery a new line has to be inserted
 *
 * @author Simon Eugster
 */
public class SplitPageContainer {

	public ArrayList<String> pages = new ArrayList<String>();

	private StringBuffer source = null;

	// TODO 3 generate navigation from {{H1}}

	final String separator = "\\\\//\n---\n//\\\\";
	final int l = separator.length();

	/** Is the page split? */
	public boolean isSplit = false;
	public byte currentPageNumber = 0;
	public int getPageNumber() {
		return pages.size();
	}

	private SplitPageContainer() {}
	private static SplitPageContainer spc = new SplitPageContainer();

	/** Singleton */
	public static SplitPageContainer getInstance() {
		return spc;
	}

	public int splitPages() {
		pages.clear();
		if (source != null) {
			int last = source.length(), first;
			while ((first = source.lastIndexOf(separator, last - 1)) > 0) {
				pages.add(source.substring(first + l, last));
				last = first;
			}
			pages.add(source.substring(0, last));

		}

		if (pages.size() > 1)
			isSplit = true;

		return 0;
	}

	public int getCharNumber() {
		if (source != null)
			return source.length();
		else
			return 0;
	}

	public void setSource(final StringBuffer sb) {
		source = new StringBuffer(sb);
	}

}
