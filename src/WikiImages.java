package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.resources.RegExpressions;
import src.resources.ResProjectSettings.SettingsE;
import src.settings.ImageSettings.Image;
import src.settings.XhtmlSettings;
import src.typo.Formattings;
import src.utilities.IORead_Stats;
import src.utilities.IOWrite_Stats;
import src.utilities.StringTools;
import src.utilities.XMLTools;


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
 * TODO 2 style: breite von thumbnails in css dynamisch
 */
public class WikiImages {

	

	/**
	 * Links gallery images amongst each other
	 */
	private static class NavLinks {

		NavLinks(Image item) {
			this.item = item;
		}

		public Image item;
		
		public static enum DirectionE {
			PREV, NEXT;
		}

		public boolean isTextOnly() {
			if (item == null || !item.contains(SettingsImgE.galleryText)) return false;
			return true;
		}

		private NavLinks prevNL = null;
		private NavLinks nextNL = null;

		public void setNextNL(NavLinks nextNL) {
			this.nextNL = nextNL;
		}
		public void setPrevNL(NavLinks prevNL) {
			this.prevNL = prevNL;
		}

		/**
		 * @param prev Previous (true) or next (false)
		 * @return Link to previous or next image page.
		 */
		public String getLinkToOther(final DirectionE direction) {
			NavLinks link = null;
			switch (direction) {
			case PREV:
				link = prevNL;
				break;
			case NEXT:
				link = nextNL;
				break;
			}
			if (link == null) return item.getBacklinkPage();
			
			return link.getLinkToMyself(direction);
		}

		/**
		 * @param direction
		 * @return Path to this or (if current item is only text or a direct link) previous/next image page
		 * @since 3.3.2 direct linking
		 */
		public String getLinkToMyself(final DirectionE direction) {
			String link = null;
			if (isTextOnly() || item.contains(SettingsImgE.linkDirect)) {
				// Cannot be linked because consists of text only or is linked directly (no image page) 
				switch (direction) {
				case PREV:
					if (prevNL == null) {
						link = item.getBacklinkPage();
					} else {
						link = prevNL.getLinkToMyself(direction);
					}
					break;
				case NEXT:
					if (nextNL == null) {
						link = item.getBacklinkPage();
					} else {
						link = nextNL.getLinkToMyself(direction);
					}
					break;
				}
			} else {
				link = item.getImagepagePath(false, true);
			}
			return link;
		}

	}

}
