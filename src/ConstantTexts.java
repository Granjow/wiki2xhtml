package src;

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
 * Contains translated standard texts.
 *
 * @since wiki2xhtml 3.3
 *
 * @author Simon Eugster
 */
public abstract class ConstantTexts {

	private static final I18n i18n = I18nFactory.getI18n(ConstantTexts.class, "bin.l10n.Messages", src.Globals.getLocale());
	public static final void updateLocale() {
		i18n.setLocale(Globals.getLocale());
	}

	public static final String files = i18n.tr("Files");
	public static final String added = i18n.tr("added");

	public static final String notYetSupported = i18n.tr("Not yet supported");
	public static final String setVerb = i18n.trc("set (verb, past)", "set");
	public static final String styleDir = i18n.tr("Style directory");
	public static final String targetDir = i18n.tr("Target directory");

	public static final String langIsNow = i18n.tr("Language is now");
	public static final String seeHelp = String.format(i18n.tr("See \u201c%s\u201d for help."), "java -jar wiki2xhtml.jar -h");

	public static final String wilCome = i18n.tr("Will come");

	public static final String newestDev = i18n.tr("Newest dev version");
	public static final String changesDev = i18n.tr("Changes in dev version");
	public static final String usingNow = i18n.tr("You\u2019re currently using");

	public static final String noConnection = i18n.tr("Cannot connect to host. Check internet connection.");
	public static final String fileNotFound = i18n.tr("File not found");

	public static final String incrementalMode = i18n.tr("Incremental mode; Only changed files will be written.");

	public static final String toc = "Table of Contents";

	public static abstract class Verb {

		public static final String found = i18n.tr("found");

	}

	public static abstract class VerbCap {

		public static final String see = i18n.tr("See");
		public static final String skipping = i18n.tr("Skipping");

	}

	public static abstract class Noun {

		public static final String commonFile = i18n.tr("common file");
		public static final String footerFile = i18n.tr("footer file");
		public static final String menuFileSmall = i18n.tr("menu file");

	}

	public static abstract class NounCap {

		public static final String date = i18n.tr("Date");
		public static final String error = i18n.tr("Error");
		public static final String notes = i18n.tr("Notes");

		public static final String commonFile = i18n.tr("Common file");
		public static final String footerFile = i18n.tr("Footer file");
		public static final String menuFile = i18n.tr("Menu file");
		public static final String reckFile = i18n.tr("Reck file");
		public static final String templateFile = i18n.tr("Template file");

	}

}
