package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Constants.SettingsE;
import src.Constants.SettingsImgE;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
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
 *
 * Generates html code for images
 *
 * @author Simon Eugster
 *
 * TODO 2 style: breite von thumbnails in css dynamisch
 */
public class WikiImages {

	private static final CommentAtor ca = CommentAtor.getInstance();

	private static final src.settings.ImageSettings is = src.settings.ImageSettings.getInstance();

	/**
	 * Generates a thumbnail entry.
	 * @return Thumbnail code
	 * @throws IOException
	 */
	public static StringBuffer generateThumbnailEntry() throws IOException {
		String link;

		if (is.image.contains(SettingsImgE.imagePageWasCreated)) {
			link = is.image.getImagepagePath(false, false);
		} else {
			link = is.image.getImagePathHtml();
		}

		String desc = is.image.contains(SettingsImgE.galleryText) ? is.image.get_(SettingsImgE.galleryText) : is.image.get_(SettingsImgE.imageDesc);
		if (desc == null) desc = "";
		Template tp = is.image.getTemplate();
		tp.clear();
		tp.replaceAll(Constants.TemplateTags.alt,
					  src.utilities.WikiStringTools.disableWikilinks(
						  desc.replaceAll(Resources.Regex.tagContent.pattern(), "$1"))
					  .replaceAll("\"", "&quot;")
					 );
		tp.replaceAll(Constants.TemplateTags.pos, is.image.contains(SettingsImgE.thumbPosition) ? is.image.get_(SettingsImgE.thumbPosition) : "");
		tp.replaceAll(Constants.TemplateTags.link, link);
		tp.replaceAll(Constants.TemplateTags.thumb, is.image.getThumbSrc());
		tp.replaceAll(Constants.TemplateTags.imagePath, is.image.getImagePathHtml());
		tp.replaceAll(Constants.TemplateTags.width, is.image.getWidthThumbAttrib());
		tp.replaceAll(Constants.TemplateTags.desc, desc);
		tp.replaceAll(Constants.TemplateTags.descXmlName, XMLTools.getXmlNameChar(desc, "\\s"));
		tp.replaceAll(Constants.TemplateTags.id, is.image.getID());
		tp.replaceAll(Constants.TemplateTags.args, is.image.contains(SettingsImgE.args) ? is.image.get_(SettingsImgE.args) : "");
		tp.replaceAll(Constants.TemplateTags.argsLink, is.image.contains(SettingsImgE.argsLink) ? is.image.get_(SettingsImgE.argsLink) : "");
		StringBuffer output = new StringBuffer();
		if (is.image.contains(SettingsImgE.clearBefore)) {
			output.append("<p style=\"clear: both; margin: 0pt; padding: 0pt; height: 0pt;;\"></p>\n");
		}
		output.append(tp.getOutput());
		if (is.image.contains(SettingsImgE.clearAfter)) {
			output.append("<p style=\"clear: both; margin: 0pt; padding: 0pt; height: 0pt;;\"></p>\n");
		}
		return output;
	}

	/**
	 * Make the [[Image:xx]] to &lt;img src="xx" /&gt; tags.
	 * <p>Rewritten: August 2008</p>
	 *
	 * @param in The input file
	 * @return input file with replaced image tags
	 */
	public static StringBuffer makeImages(StringBuffer in) {
		Statistics.getInstance().sw.timeInsertingImages.continueTime();

		Container_Files fc = Container_Files.getInstance();

		StringBuffer out = new StringBuffer();
		short counter = 0;

		Matcher m;

		is.nextPage(fc.currentFilename);

		m = Resources.Regex.images.matcher(in);
		int last = 0;

		while (m.find()) {
			out.append(in.substring(last, m.start()));
			Statistics.getInstance().counter.imagesTotal.increase();

			// Read the image's arguments
			is.nextItem();
			is.image.readArguments(m.group(1), false);
			is.image.set_(SettingsImgE.galleryEnabled, is.image.nullValue());

			// Generate an image page if they are to be generated, a isThumbDesired has to be inserted and it's no direct link.
			if (is.image.contains(SettingsImgE.thumbEnabled) && !is.image.contains(SettingsImgE.linkDirect)) {
				generateImagepage();
			}

			try {
				out.append(generateThumbnailEntry());
			} catch (FileNotFoundException e) {
				CommentAtor.getInstance().ol("A file could not be found. Don't know why. Error message: \n" + e.getMessage(), CALevel.MSG);
			} catch (IOException e) {
				CommentAtor.getInstance().ol("An IO Error occurred. Don't know why. Error message: \n" + e.getMessage(), CALevel.MSG);
			}


			last = m.end();
		}
		out.append(in.substring(last, in.length()));

		/* Statistics */
		Statistics.getInstance().counter.imagesTotal.increase(counter);
		Statistics.getInstance().sw.timeInsertingImages.stop();

		return out;
	}

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
						tp.replace(Constants.TemplateTags.id, is.page.getIdGallery());
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
	 * Generates an image page
	 * @since wiki2xhtml 3.4: Titles for image pages
	 */
	public static void generateImagepage() {

		try {
			StringBuffer sb = IORead_Stats.readSBuffer(Container_Files.getInstance().cont.imageTemplate());
			XhtmlSettings xhs = XhtmlSettings.getInstance();

			String caption;
			String desc = is.image.get_(SettingsImgE.imageDesc);
			if (desc == null) desc = "";
			if (is.image.contains(SettingsImgE.imageCaption)) {
				caption = is.image.get_(SettingsImgE.imageCaption);
			} else {
				if ("true".equals(xhs.local.getLocalOrGlobal_(SettingsE.descForCaption))) {
					caption = desc;
					desc = "";
				} else if ("true".equals(xhs.local.getLocalOrGlobal_(SettingsE.nameForCaption))) {
					String path = is.image.get_(SettingsImgE.imagePath);
					caption = path.substring(path.lastIndexOf('/'));
				} else
					caption = "";
			}
			if (caption == null) caption = "";
			
			String title = xhs.local.getLocalOrGlobal_(SettingsE.titleRule);
			if (title == null) {
				title = "%caption %s";
			}
			title = title.replaceAll("%caption", caption);
			title = title.replaceAll("%path", is.image.getImagePathHtml());
			title = title.replaceAll("%name", is.image.getImageFilenameHtml());
			title = xhs.local.title(title);

			StringTools.replaceAll(sb, Constants.TemplateTags.imagePath, is.image.getImagePathHtml());
			StringTools.replaceAll(sb, Constants.TemplateTags.imageName, is.image.getImageFilenameHtml());
			StringTools.replaceAll(sb, Constants.TemplateTags.imageCaption, caption);
			StringTools.replaceAll(sb, Constants.TemplateTags.alt, caption.replaceAll(Resources.Regex.tagContent.pattern(), "$1"));
			StringTools.replaceAll(sb, Constants.TemplateTags.pre, is.page.getBacklinkDir());
			StringTools.replaceAll(sb, Constants.TemplateTags.back, is.image.getBacklinkPage());
			StringTools.replaceAll(sb, Constants.TemplateTags.title, title);
			StringTools.replaceAll(sb, Constants.TemplateTags.meta, xhs.local.metadata());
			StringTools.replaceAll(sb, Constants.TemplateTags.width, is.image.contains(SettingsImgE.imageSmall) ? "" : is.image.getWidthPageAttrib());
			ReplaceTags.replaceWiki2xhtml(sb);
			ReplaceTags.replaceVersion(sb);
			StringTools.replaceAll(sb, Constants.TemplateTags.nextImage, is.image.contains(SettingsImgE.linkNext) ?
								   is.image.get_(SettingsImgE.linkNext)
								   : is.image.getBacklinkPage());
			StringTools.replaceAll(sb, Constants.TemplateTags.prevImage, is.image.contains(SettingsImgE.linkPrev) ?
								   is.image.get_(SettingsImgE.linkPrev)
								   : is.image.getBacklinkPage());
			StringTools.replaceAll(sb, Constants.TemplateTags.desc,
								   Formattings.format0r(new StringBuffer(is.image.contains(SettingsImgE.imageLongdesc) ? is.image.get_(SettingsImgE.imageLongdesc) : desc), "",
														Formattings.BOLD | Formattings.ITALIC | Formattings.LINKS, false).toString());

			/*
			 * Insert the description
			 * 080726 Fixed: &lt;desc&lt; and &gt;desc&gt; not correctly removed
			 */
			int descStart, descEnd;



			if ((descStart = sb.indexOf(">desc>")) >= 0 && (descEnd = sb.indexOf("<desc<")) >= 0) {

				/* Remove the block if not needed, otherwise remove position marks */
				if (is.image.contains(SettingsImgE.imageLongdesc) || is.image.contains(SettingsImgE.imageDesc)) {
					sb.delete(descEnd, descEnd + "<desc<".length());
					sb.delete(descStart, descStart + ">desc>".length());

				} else { /* No description given, remove everything between &lt;desc&lt; and &gt;desc&gt; */
					sb.delete(descStart, descEnd + "<desc<".length());
				}

			}

			/*
			 * Remove the navigation part if not necessary
			 */
			int navStart, navEnd;
			while ((navStart = sb.indexOf(">nav>")) >= 0 && (navEnd = sb.indexOf("<nav<")) >= 0) {
				if (is.image.contains(SettingsImgE.galleryEnabled)) {
					sb.delete(navEnd, navEnd + "<nav<".length());
					sb.delete(navStart, navStart + ">nav>".length());
				} else {
					sb.delete(navStart, navEnd + "<nav<".length());
				}
			}

			File f = new File(is.image.getImagepagePath(true, false));
			IOWrite_Stats.writeString(f, sb.toString(), false);
			if (Container_Files.getInstance().currentInFile.sitemap) {
				Container_Files.getInstance().addSitemapEntry(f);
			}

			is.image.set_(SettingsImgE.imagePageWasCreated, "true");

		} catch (IOException e) {
			ca.ol("Error: Image page " + is.image.getImagepagePath(true, false) + " couldn't be created!\n", CALevel.ERRORS);
			e.printStackTrace();

			is.image.set_(SettingsImgE.imagePageWasCreated, is.image.nullValue());
		} catch (NullPointerException e) {
			if (Globals.getGuiManager().f != null && Globals.getGuiManager().f.isVisible()) {
				/* GUI mode */
			} else {
				e.printStackTrace();
			}
		}

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
