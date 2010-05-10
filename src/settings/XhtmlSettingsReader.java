package src.settings;

import java.io.File;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Constants;
import src.Statistics;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.resources.RegExpressions;
import src.resources.ResProjectSettings.SettingsE;
import src.utilities.IORead_Stats;



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
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 *
 * Reads settings from the commons file.
 *
 * @since wiki2xhtml 3.3 2&#x2a2f; as fast as before (since wiki2xhtml 3.3)
 *
 * @author Simon Eugster
 *
 */
public class XhtmlSettingsReader {

	private static final XhtmlSettings xhs = XhtmlSettings.getInstance();

	/**
	 * These settings are read automatically from documents loaded.
	 */
	private static final SettingsE[] autoReadableSettings = {
		SettingsE.author, SettingsE.desc, SettingsE.galleryImagesPerLine, SettingsE.galleryThumbWidth, 
		SettingsE.imagesDir, SettingsE.imagepageImgWidth, SettingsE.imagepagesDir,
		SettingsE.thumbsDir, SettingsE.homelink, SettingsE.icon,
		SettingsE.keywords, SettingsE.lang, SettingsE.meta, SettingsE.namespace, SettingsE.reckAlternative,
		SettingsE.textHeader, SettingsE.title, SettingsE.titleRule, SettingsE.thumbWidth };
	

	/** Reads design specific settings */
	public static void getDesignSettings(File f) {
		try {

			StringBuffer content = IORead_Stats.readSBuffer(f);
			String value;

			//TODO 0 rename to same names
			if ((value = getProperty(SettingsE.galleryImagesPerLine, content, false)) != null)
				Constants.Standards.galleryImagesPerLine = value;
			if ((value = getProperty(SettingsE.imagepageImgWidth, content, false)) != null)
				Constants.Standards.widthImgImagepages = value;
			if ((value = getProperty(SettingsE.thumbWidth, content, false)) != null)
				Constants.Standards.widthThumbs = value;
			if ((value = getProperty(SettingsE.galleryThumbWidth, content, false)) != null)
				Constants.Standards.galleryWidthThumbs = value;


		} catch (IOException e) {
			CommentAtor.getInstance().ol(
				String.format("I tried to read the file \"%s\", but it was not available for reading.", f.getAbsolutePath()),
				CALevel.MSG);
		} catch (NullPointerException e) {
			CommentAtor.getInstance().ol("It seems like if the file css-settings.txt didn't exist. Never mind.", CALevel.MSG);
		}
	}

	/** Reads the common file. */
	public static XhtmlSettings.Settings getSettings(StringBuffer content, boolean global) {

		XhtmlSettings.Settings s = global? new XhtmlSettings.GlobalSettings() : new XhtmlSettings.LocalSettings();
		 
		for (SettingsE p : autoReadableSettings) {
			s.set_(p, getProperty(p, content, false));
		}
		s.set_(SettingsE.defaultTitle, getProperty(SettingsE.defaultTitle, content, false));
		s.set_(SettingsE.descForCaption, useImageDescAsCaption(content, false, false));
		s.set_(SettingsE.nameForCaption, useImagenameAsCaption(content, false, false));

		return s;
	}

	/**
	 * Reads local settings.
	 * @param content
	 * @return
	 */
	public static StringBuffer getXhtml(StringBuffer content) {
		Statistics.getInstance().sw.timeReadingLocalSettings.continueTime();

		for (SettingsE p : autoReadableSettings) {
			xhs.local.set_(p, getProperty(p, content, true));
		}

		xhs.local.set_(SettingsE.h1, getProperty(SettingsE.h1, content, true));
		xhs.local.set_(SettingsLocalE.redirect, getRedirect(content, false));

		xhs.local.set_(SettingsE.descForCaption, useImageDescAsCaption(content, true, true));
		xhs.local.set_(SettingsE.nameForCaption, useImagenameAsCaption(content, true, true));

		Statistics.getInstance().sw.timeReadingLocalSettings.stop();

		return content;
	}

	/**
	 * @param pattern Pattern to search for
	 * @param content Content to parse
	 * @param remove Remove found groups in <code>content</code> leave it unchanged?
	 * @return <code>null</code>, if not found, and the content otherwise.
	 */
	private static final String getArg(final Pattern pattern, StringBuffer content, final boolean remove, Pos start) {
		Matcher m = pattern.matcher(content);
		String group = null;

		if (m.find(start.pos())) {
			group = m.group(1);
			if (remove) {
				content.delete(m.start(), m.end());
				start.setPos(m.start());
			} else {
				start.setPos(m.end());
			}
		}

		return group;
	}

	/**
	 * 
	 * @param remove Remove occurrences?
	 * @return <strong>null</strong>, if <code>property</code> could not be found or has length 0, 
	 * 		and the found content otherwise.
	 */
	private static final String getProperty(SettingsE property, StringBuffer content, final boolean remove) {
		String value = null;
		String temp;
		if (property.loop()) {
			// Property may occur multiple times; get them all
			boolean first = true;
			StringBuilder sb = new StringBuilder();
			Pos pos = new Pos(0);
			while ((temp = getArg(property.regex(), content, remove, pos)) != null) {
				if (first) {
					first = false;
				} else {
					sb.append(property.separator());
				}
				sb.append(temp);
			}
			if (sb.length() > 0) value = sb.toString();
		} else {
			// Property only occuring once
			temp = getArg(property.regex(), content, remove, new Pos(0));
			if (temp != null && temp.length() > 0) value = temp;
		}
		
		return value;
	}

	private static final String getRedirect(StringBuffer content, boolean remove) {
		Matcher m = RegExpressions.redirect.matcher(content);
		String redirect = "";

		if (m.find()) {
			redirect = "<meta http-equiv=\"refresh\" " +
					   "content=\"" + m.group(1) + "; " + "url=" + m.group(2) + " \" />";
			if (remove) {
				content.delete(m.start(), m.end());
			}
		}
		return redirect;
	}

	private static String useImageDescAsCaption(StringBuffer in, boolean remove, boolean useObject) {

		String tag = "{{" + SettingsE.descForCaption.keyword() + "}}";
		int pos;
		if ((pos = in.indexOf(tag)) > 0) {
			if (remove) {
				in.delete(pos, pos + tag.length());
			}
			return "true";
		}
		tag = "{{" + SettingsE.descForCaption.keyword() + Constants.Settings.Args.argFalse + "}}";
		if ((pos = in.indexOf(tag)) > 0) {
			if (remove) {
				in.delete(pos, pos + tag.length());
			}
			return "false";
		}
		return useObject ? null : Constants.Standards.useImageDescAsCaption;
	}

	private static String useImagenameAsCaption(StringBuffer in, boolean remove, boolean useObject) {

		String tag = "{{" + SettingsE.nameForCaption.keyword() + "}}";
		int pos;
		if ((pos = in.indexOf(tag)) > 0) {
			if (remove) {
				in.delete(pos, pos + tag.length());
			}
			return "true";
		}
		tag = "{{" + SettingsE.nameForCaption.keyword() + Constants.Settings.Args.argFalse + "}}";
		if ((pos = in.indexOf(tag)) > 0) {
			if (remove) {
				in.delete(pos, pos + tag.length());
			}
			return "false";
		}
		return useObject ? null : Constants.Standards.useImagenameAsCaption;
	}


	private static class Pos {
		private int pos;
		Pos(int pos) { this.pos = pos; }
		public int pos() { return pos; }
		public void setPos(int pos) { this.pos = pos; }
	}

}
