package src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.changesObserver.ChangesMap;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.settings.Container_Settings;
import src.settings.XhtmlSettings;
import src.utilities.IORead_Stats;
import src.utilities.IOUtils;
import src.utilities.IOWrite_Stats;


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
 * Contains all necessary information about a project which has to be parsed.
 *
 * @author Simon Eugster
 */
public class Container_Files {

	private static final I18n i18n = I18nFactory.getI18n(Container_Files.class, "bin.l10n.Messages", src.Globals.getLocale());
	private static final CommentAtor ca = CommentAtor.getInstance();

	public Container_Settings sc = new Container_Settings();
	
	public class WikiFile {
		public final File f;
		public final boolean parse;
		
		/** Needs to be overridden for comparing. */
		public boolean equals(Object obj) {
			if (this == obj) { return true; }
			if (obj instanceof WikiFile) {
				WikiFile wf = (WikiFile) obj;
				if (f.equals(wf.f)) {
					return true;
				}
			}
			return false;
		}
		
		WikiFile(File f, boolean sitemap, boolean parse) {
			this.f = f;
			this.parse = parse;
		}
	}

	/**
	 * Can be {@link #clear()}ed.
	 */
	public static class Container {

		/** @since wiki2xhtml 3.4 */
		public ChangesMap changesMap = new ChangesMap();

		public ArrayList<WikiFile> files = new ArrayList<WikiFile>(); // All used files

		public File commonFile; // File containing the default settings
		public File footerFile; // File containing the footer
		public File menuFile; // Menu file containing the menu
		public File hashFile = new File(".wiki2xhtml-hashes");
		
		/** Folder containing the style sheets and the reck */
		private File styleDir;
		public File styleDir() {
			return styleDir;
		}
		public String styleDirPath() {
			return (styleDir == null ? "" : styleDir.getPath());
		}
		public String styleDirAbsolutePath() {
			return (styleDir == null ? "" : styleDir.getAbsolutePath());
		}

		private File sourceDir = null; // Source directory
		public String sourceDir() {
			String srcDir = "";
			if (sourceDir != null) {
				srcDir = sourceDir.getAbsolutePath() + File.separator;
			}
			return srcDir;
		}

		private File targetDir; // Target directory
		public File targetDir() {
			if (targetDir == null)
				Container_Files.getInstance().setTargetDirectory(Constants.Directories.target);
			return targetDir;
		}

		private File targetSDir; // Target style directory
		public File targetSDir() {
			targetDir();	// Set up target directory if necessary; targetSDir will be created too
			return targetSDir;
		}

		String pastedTextCode = new String(); // Code from the pasted text
		String pastedText; // Pasted Text in the code window

		/** @return Template for the pages */
		public StringBuffer reck() {
			StringBuffer sb = new StringBuffer();
			File f = new File(styleDirPath() + File.separatorChar + XhtmlSettings.getInstance().local.reck());
			if (f.exists())
				try {
					sb = IORead_Stats.readSBuffer(f);
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			else {
				sb = IORead_Stats.readFromJar(Container_Resources.sreck);
			}

			return sb;
		}

		/** @return Template for the image page */
		public File imageTemplate() {
			return new File(styleDir.getPath() + File.separatorChar + Constants.Files.imageTemplate);
		}

		public File cssSettings() {
			return (styleDir != null ? new File(styleDir.getPath() + File.separatorChar + Constants.Files.cssSettings) : null);
		}



	}



	public static class Has {
		/** Is there pasted text? */
		boolean paste = false;
		/** Is the menu file defined? */
		boolean menu = false;
		/** Is a style directory given? */
		boolean style = false;
		/** Is a source directory set? */
		boolean sourceDir = false;
		/** Is a target directory given? */
		boolean target = false;
		/** Is a target style dir given? */
		boolean targetS = false;
		/** Is a footer file given? */
		boolean footer = false;
		/** Don't correct typos? */
		boolean noTypo = false;
		/** Is a common file set? */
		boolean common = false;
	}

	/** Updated by UserInterface */
	public String currentFilename;
	/** Updated by UserInterface */
	public WikiFile currentInFile;
	public Container cont = new Container();
	public Has has = new Has();

	public void clear() {
		cont = new Container();
		has = new Has();
	}

	/**
	 * Checks whether filename exists. If not, try to prepend
	 * the source directory if exists.
	 * @param filename
	 * @param srcDirOnly File must be in the source directory.
	 * @return null, if no file found, or the existing file otherwise.
	 */
	public File fileExists(String filename, boolean srcDirOnly) {
		File f = new File(filename);
		if (!f.exists() || srcDirOnly) {
			f = new File(cont.sourceDir() + filename);
			if (!f.exists()) f = null;
		}
		return f;
	}

	/**
	 * Add an menu file (containing the menu) and test whether it exists
	 *
	 * @param filename
	 *            name of the file
	 * @return true, if file successfully loaded
	 */
	public boolean addMenuFile(String filename) {
		// Prepend source directory
		filename = cont.sourceDir() + filename;

		has.menu = false;
		cont.menuFile = new File(filename);

		if (cont.menuFile.exists() && cont.menuFile != null) {
			has.menu = true;
			return true;
		}
		ca.ol(i18n.tr(String.format("Error: Menu file %s not found or file empty", filename)), CALevel.ERRORS);
		return false;
	}

	/** @see {@link #addFile(String, boolean)} */
	public boolean addFile(String filename) {
		return addFile(filename, true, true);
	}
	/**
	 * Add a file that has to be converted to the list.
	 *
	 * @param filename Name of the file
	 * @param sitemap Show on sitemap?
	 * @return true, if success
	 */
	public boolean addFile(String filename, boolean sitemap, boolean parse) {
		// Prepend source directory
		filename = cont.sourceDir() + filename;

		File f = null;
		if (!fulfilsTextFileQualifications(filename))
			return false;
		filename = IOUtils.getShortPath(new File(filename), Constants.Directories.workingDir2);

		/*
		 * Try to open file, only return true if it has been done
		 * successfully.
		 *
		 * File is first created separately, not in the list.
		 */
		f = new File(filename);

		if (f != null && f.exists()) {
			/*
			 * Add the file to the list, if it does not exist already
			 */
			WikiFile wf = new WikiFile(f, sitemap, parse);
			if (!cont.files.contains(wf))
				cont.files.add(wf);
			return true;
		}

		ca.ol(i18n.tr(String.format("Error: File %s not found or empty", filename)), CALevel.ERRORS);
		return false;

	}

	public WikiFile file(int position) {
		return cont.files.get(position);
	}

	/** @return The number of files which are «saved» in the Array */
	public short filesNumber() {
		return (short) cont.files.size();
	}

	public WikiFile lastFile() {
		return cont.files.get(cont.files.size() - 1);
	}


	/**
	 * @return The absolute path of the style directory if available
	 * or an emtpy string otherwise
	 */
	public String styleDirAbsolutePath() {
		return cont.styleDir == null ? ""
			   : cont.styleDir.getAbsolutePath();
	}

	/**
	 * Sets the pasted text in the code window
	 * @param s
	 */
	public void setPastedText(String s) {
		if (s == null || s.equals( "" )) {
			has.paste = false;
			cont.pastedText = "";
		} else {
			has.paste = true;
			cont.pastedText = s;
			XhtmlSettings xhs = XhtmlSettings.getInstance();
			xhs.local.content = new StringBuffer(s);
			cont.pastedTextCode = XHTML.wiki2xhtml().toString();
		}
	}

	public boolean setCommonFile(String filename) {
		// Prepend source directory
		filename = cont.sourceDir() + filename;

		boolean ok = fulfilsTextFileQualifications(filename);
		if (ok) {
			filename = IOUtils.getShortPath(new File(filename), Constants.Directories.workingDir2);
			cont.commonFile = new File(filename);
			has.common = true;
		}
		return ok;
	}

	public boolean setFooterFile(String filename) {
		// Prepend source directory
		filename = cont.sourceDir() + filename;

		boolean ok = fulfilsTextFileQualifications(filename);
		if (ok) {
			filename = IOUtils.getShortPath(new File(filename), Constants.Directories.workingDir2);
			cont.footerFile = new File(filename);
			has.footer = true;
		} else {
			has.footer = false;
			cont.footerFile = null;
		}
		return ok;
	}

	public boolean setMenuFile(String filename) {
		// Prepend source directory
		filename = cont.sourceDir() + filename;

		boolean ok = fulfilsTextFileQualifications(filename);
		if (ok) {
			filename = IOUtils.getShortPath(new File(filename), Constants.Directories.workingDir2);
			cont.menuFile = new File(filename);
			has.menu = true;
		} else {
			has.menu = false;
		}
		return ok;
	}

	public void setNoTypo(boolean b) {
		has.noTypo = b;
	}
	
	/**
	 * Will try to use name directly.
	 * If fails: Prepend standard directory name.
	 * Example: name = "hi-you"
	 * 1st test: Does "hi-you" exist? If yes, take it.
	 * 2nd test: Does "style/hi-you" exist? If yes, take it.
	 * @param name
	 * @return
	 */
	public boolean setStyleDirectory(String name) {
		boolean ok = fulfilsDirectoryQualifications(name);
		if (!ok) {
			name = Constants.Directories.style + File.separatorChar + name;
			ok = fulfilsDirectoryQualifications(name);
		}
		if (ok) {
			cont.styleDir = new File(name);
			has.style = true;
		}
		// Update target style directory!
		if (has.target) {
			setTargetDirectory(cont.targetDir.getPath());
		}
		return ok;
	}

	public boolean setSourceDirectory(String name) {
		boolean ok = false;
		File f = new File(name);
		if (f.exists() && f.isDirectory()) {
			cont.sourceDir = f;
			has.sourceDir = true;
			System.err.println(f.getAbsolutePath() + " set as source directory.");
			ok = true;
		}
		else {
			System.err.println(f.getAbsolutePath() + " couldn't be set.");
		};
		return ok;
	}

	public boolean setTargetDirectory(String name) {
		File f = new File(name);
		if (!f.exists()) {
			f.mkdirs();
		}
		boolean ok = f.isDirectory();
		if (!ok) {
			StringBuffer fn = new StringBuffer(name);
			int start;
			while ((start = fn.indexOf(File.separator)) >= 0 && !ok) {
				fn.delete(start, fn.length());
				if (!new File(fn.toString()).isFile())
					ok = true;
			}
		}
		if (ok) {
			cont.targetDir = new File(name);
			has.target = true;

			if (has.style) {
				cont.targetSDir = new File(cont.targetDir.getPath() + File.separatorChar + Constants.Directories.style);
				has.targetS = true;
			}
		}
		return ok;
	}

	public void setOnlyCode(boolean b) {
		sc.onlyCode = b;
	}

	public void setStdOut(boolean b) {
		sc.stdOut = b;
	}

	public void setNoLinebreaks(boolean b) {
		sc.removeLineBreaks = b;
	}

	public void setIncremental(boolean b) {
		sc.incremental = b;
	}

	public Args fileList() {
		Args a = new Args();
		for (int i = 0; i < cont.files.size(); i++)
			a.add(cont.files.get(i).f.getPath());
		return a;
	}

	public File[] fileArray() {
		File[] fa = new File[cont.files.size()];
		for (short i = 0; i < cont.files.size(); i++) {
			fa[i] = cont.files.get(i).f;
		}
		return fa;
	}

	/**
	 *
	 * @return String with all arguments except the files to convert, the
	 *         --stdout, the ones I forgot and the --help/verbose/... for
	 *         cmdline-Output.
	 */
	public Args allArguments() {
		Args a = new Args();

//		a.add("--lang=" + src.Globals.getLocale());

		if (has.menu) {
			a.add(Constants.Arguments.Arg.menu + " ");
			a.add(cont.menuFile.getPath());
		}

		if (has.style) {
			a.add(Constants.Arguments.Arg.style + " ");
			a.add(cont.styleDir.getPath());
		}

		if (has.common) {
			a.add(Constants.Arguments.Arg.common + " ");
			a.add(cont.commonFile.getPath());
		}

		if (has.footer) {
			a.add(Constants.Arguments.Arg.footer + " ");
			a.add(cont.footerFile.getPath());
		}

		if (sc.onlyCode) {
			a.add(Constants.Arguments.Flags.onlyCode);
		}

		if (has.noTypo) {
			a.add("--remove-linebreaks");
		}

		if (has.target) {
			a.add(Constants.Arguments.Arg.targetDir + " ");
			a.add(cont.targetDir.getPath());
		}

		if (sc.incremental) {
			a.add(Constants.Arguments.Flags.incremental);
		}

		return a;
	}

	private boolean fulfilsTextFileQualifications(String filename) {
		boolean ok = true;

		File f = new File(filename);
		if (!f.exists()) {
			ca.ol("File does not exist: " + filename, CALevel.V_MSG);
			ok = false;
		} else if (!f.isFile()) {
			ca.ol("Is not a file (directory!): " + filename, CALevel.V_MSG);
			ok = false;
		} else if (IOUtils.binaryCheck(f)) {
			ca.ol("Is not a text file: " + filename, CALevel.V_MSG);
			ok = false;
		}

		return ok;
	}

	private boolean fulfilsDirectoryQualifications(String name) {
		boolean ok = true;

		File f = new File(name);
		if (!f.exists()) {
			ca.ol("Directory does not exist: " + name, CALevel.V_MSG);
			ok = false;
		} else if (!f.isDirectory()) {
			ca.ol("Is not a directory (file!): " + name, CALevel.V_MSG);
			ok = false;
		}

		return ok;
	}

	public void removeFooter() {
		has.footer = false;
		cont.footerFile = null;
	}
	public void removeMenu() {
		has.menu = false;
		cont.menuFile = null;
	}
	public void removeTargetDir() {
		has.target = false;
		cont.targetDir = null;
	}
	public void removeCommon() {
		has.common = false;
		cont.commonFile = null;
	}
	
}
