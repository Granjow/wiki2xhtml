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
	 * <p>Builds all galleries.</p>
	 * <ul>
	 * <li>Image entries start with <code>Image:</code>.</li>
	 * <li>Text entries start with text only</li>
	 * <li>Comments start with <code>//</code> (will not be shown on the page)</li>
	 * </ul>
	 * @param in File input
	 * @return
	 */
	public static StringBuffer makeGallery(StringBuffer in) {
		Statistics.getInstance().sw.timeInsertingImages.continueTime();

		ArrayList<NavLinks> entries = new ArrayList<NavLinks>();

		BufferedReader b = new BufferedReader(new StringReader(in.toString()));
		StringBuffer out = new StringBuffer();

		Template tp = new Template(
			new File(Container_Files.getInstance().cont.styleDirAbsolutePath() + File.separatorChar + "galleryContainer.html"),
			Container_Resources.readResource(Container_Resources.sgalleryContainer)
		);

		byte counter = 0;

		try {
			String line;
			String caption = new String();

			Image entry;

			Pattern pImageLine = Pattern.compile("(?:Image|Bild):(.*)");
			Matcher mImagesLine;

			Pattern pCaption = Pattern.compile("caption=\"([^\"]*)\"");
			Matcher mCaption;

			boolean open = false;

			for (line = b.readLine(); line != null; line = b.readLine()) {

				if (open) {
					if (line.startsWith("</gallery>")) {
						// Add all entries, generate image pages

						open = false;

						tp.clear();

						// Linking items
						for (int i = 0; i < entries.size(); i++) {

							try {
								entries.get(i).setPrevNL(entries.get(i-1));
							}
							catch (IndexOutOfBoundsException e) { }

							try {
								entries.get(i).setNextNL(entries.get(i+1));
							}
							catch (IndexOutOfBoundsException e) { }
						}

						// Set up links and generate image pages and thumbnail entries
						byte gipl = XhtmlSettings.getInstance().local.galleryImagesPerLine();
						for (int i = 0; i < entries.size(); i++) {

							is.image = entries.get(i).item;

							entries.get(i).item.set_(SettingsImgE.linkNext, entries.get(i).getLinkToOther(NavLinks.DirectionE.NEXT));
							entries.get(i).item.set_(SettingsImgE.linkPrev, entries.get(i).getLinkToOther(NavLinks.DirectionE.PREV));

							if (!entries.get(i).item.contains(SettingsImgE.galleryText) && !is.image.contains(SettingsImgE.linkDirect)) {
								generateImagepage();
							}

							tp.content.append(generateThumbnailEntry());

							// Add new image line if wished
							if (gipl != 0
									&& i > 1
									&& (i+1) % gipl == 0)
								tp.content.append("\n<br style=\"clear: both;\" />");

						}

						tp.replace(Constants.TemplateTags.caption, "<h3>" + caption + "</h3>");
						tp.replace(Constants.TemplateTags.id, is.page.nextGalleryID());
						tp.replaceContent();

						out.append(tp.getOutput());
						entries.clear();	// Remove all entries
						is.page.counterGalleries++;

					} else {
						
						if (line.startsWith("//")) {
							// Comment. Do not add image.
							continue;
						}

						// Add item

						entry = new Image();

						mImagesLine = pImageLine.matcher(line);
						if (mImagesLine.find()) {
							entry.readArguments(mImagesLine.group(1), false);
						} else {
							entry.readArguments(line, true);
						}

						entry.set_(SettingsImgE.galleryEnabled, "true");

						entries.add(new NavLinks(entry));
					}
				} else {
					if (line.startsWith("<gallery ") || line.startsWith("<gallery>")) {
						// add gallery start
						open = true;
						mCaption = pCaption.matcher(line);
						if (mCaption.find()) {
							caption = mCaption.group(1);
						} else
							caption = "";

					} else {
						// add line
						out.append(line + '\n');
					}
				}


			}

		} catch (IOException e) {
			e.printStackTrace();
			CommentAtor.getInstance().ol(
				String.format("IO Exception while trying to read content in file %s!", Container_Files.getInstance().currentFilename),
				CALevel.ERRORS);
			return in;
		}

		/* Statistics */
		Statistics.getInstance().counter.galleries.increase(counter);
		Statistics.getInstance().sw.timeInsertingImages.stop();

		return out;
	}

	

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
