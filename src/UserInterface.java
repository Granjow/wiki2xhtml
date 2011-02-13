package src;

import java.awt.Point;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Calendar;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.commentator.CommentAtor;
import src.commentator.Logger;
import src.commentator.CommentAtor.CALevel;
import src.pagesplitter.SplitPageContainer;
import src.pagesplitter.SplitPageLinks;
import src.settings.XhtmlSettings;
import src.tools.UpdateMenufile;
import src.utilities.IORead_Stats;
import src.utilities.IOUtils;
import src.utilities.IOWrite_Stats;
import src.utilities.LanguageTools;
import src.utilities.StringTools;


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
 * The main file, starts wiki2xhtml.
 *
 * @author Simon Eugster
 *
 */
public class UserInterface {

	private static I18n i18n = I18nFactory.getI18n(UserInterface.class, "bin.l10n.Messages", src.Globals.getLocale());
	private static CommentAtor ca = CommentAtor.getInstance();

	static short fileNumber, nFiles;
	static int progress = 0;
	
	public static void restartGui() throws java.awt.HeadlessException {
		Point p = Globals.getGuiManager().f.getLocation();

		Globals.getGuiManager().f.setVisible(false);
		Globals.getGuiManager().f.dispose();
		Globals.guiManager = null;
		Globals.guiManager = new GUI();

		runGui(p, true);
		Globals.getGuiManager().updateSettings();
	}

	public static boolean runGui(Point p, boolean wasRestarted) {
		try {
			Globals.getGuiManager().gui(wasRestarted);
			Globals.getGuiManager().f.setVisible(true);
			if (p != null)
				Globals.getGuiManager().f.setLocation(p);
		} catch (java.awt.HeadlessException e) {
			ca.ol("Cannot open GUI. Am I in a tty?", CALevel.ERRORS);
			return false;
		}
		return true;
	}

	public static void main(String cmdLineArgs[]) {
		
		if (Globals.programSettings.readSettings()) {
			src.Globals.setLocale(Globals.programSettings.settings.get(Constants.ProgramSettings.locale));
		}

		/*
		 * Run the menu file updater if wished
		 */
		if (cmdLineArgs.length > 0 && Constants.Arguments.Special.menufileUpdater.equals(cmdLineArgs[0])) {
			if (cmdLineArgs.length > 1) {
				UpdateMenufile.run(cmdLineArgs[1], true);
			} else
				UpdateMenufile.run(null, true);

			System.exit(0);
		}

		/*
		 * Or just run wiki2xhtml
		 */
		UserInterface nonStatic = new UserInterface();

		if (StringTools.stringArrayToSB(cmdLineArgs, " ").indexOf("--dead") < 0 && cmdLineArgs.toString().indexOf("--silent") < 0)
			ca.ol("wiki2xhtml " + Constants.Wiki2xhtml.version + " r" + Constants.Wiki2xhtml.revisionNumber(), CALevel.MSG);

		StringBuffer sb = new StringBuffer();
		for (String s : cmdLineArgs)
			if (s.contains(" "))
				sb.append(" \"" + s + "\"");
			else
				sb.append(' ' + s);

		Args a = new Args(Handler_Arguments.handleReplaceArguments(sb.toString()));

		/*
		 * Standard settings
		 */

		if (Handler_Arguments.handleCommandLineArguments(a, true, true))
			// GUI has not been opened, so run the parser
			nonStatic.make();

	}

	public void make() {

		Container_Files fc = Container_Files.getInstance();

		if (fc.filesNumber() > 0) {

			/* Read the old hashes to search for unchanged files */
			fc.cont.changesMap.init();
			if (fc.sc.incremental) fc.cont.changesMap.buildHashes();

			int files = 0, charsRead = 0, charsWritten = 0;

			Statistics.getInstance().reset();

			Statistics.getInstance().sw.timeOverall.start();
			Statistics.getInstance().sw.timeInitializingProcess.start();
			XhtmlSettings xhs = XhtmlSettings.getInstance();
			/** Will contain the result */
			StringBuffer xhtmlOutput = new StringBuffer();
			/** Contains the menu */
			WikiMenu wm = new WikiMenu();

			Container_Files.WikiFile in = null; // The source file
			File out = null; // The target file


			ca.ol(XHTML.PC(), CALevel.V_MSG);

			/*
			 * DESIGN use random style if none was selected
			 */
			if (!fc.has.style)
				if (XHTML.baseStyleDir.exists() && XHTML.baseStyleDir.isDirectory()) {
					File[] sf = XHTML.baseStyleDir.listFiles(new FilenameFilter() {
						public boolean accept(File arg0, String arg1) {
							return new File(XHTML.baseStyleDir.getPath()
											+ File.separatorChar + arg1).isDirectory()
								   && !".svn".equals( arg1 );
						}
					});
					if (sf.length > 0)
						fc.setStyleDirectory(sf[(int) (Math.random() * sf.length)].getPath());
				}


			/*
			 * Read design specific settings
			 */
			XhtmlSettingsReader.getDesignSettings(Container_Files.getInstance().cont.cssSettings());

			/*
			 * COMMON read common settings
			 */
			if (fc.has.common) {
				try {
					xhs.global = (XhtmlSettings.GlobalSettings) XhtmlSettingsReader.getSettings(IORead_Stats.readSBuffer(fc.cont.commonFile), true);
				} catch (IOException e) {
					CommentAtor.getInstance().ol("Could not write file " + fc.cont.commonFile.getAbsolutePath(), CALevel.ERRORS);
					Logger.getInstance().log.append(e);
					e.printStackTrace();
				}
			}

			/*
			 * FOOTER set the footer
			 */
			if (fc.has.footer) {
				StringBuffer footer = new StringBuffer();
				try {
					footer = IORead_Stats.readSBuffer(fc.cont.footerFile);
				} catch (IOException e) {
					CommentAtor.getInstance().ol("Could not read file " + fc.cont.footerFile.getAbsolutePath(), CALevel.ERRORS);
					Logger.getInstance().log.append(e);
					e.printStackTrace();
				}
				StringTools.replaceAll(footer, "%v", Constants.Wiki2xhtml.versionNumber);
				StringTools.replaceAll(footer, "%d", Calendar.getInstance().getTime().toString());
				xhs.global.footer = footer.toString();
			}

			/*
			 * MENU set the menu
			 */
			if (fc.has.menu) {
				try {
					wm.readNewMenu(fc.cont.menuFile);
				} catch (IOException e) {
					Logger.getInstance().log.append(e);
					e.printStackTrace();
				}
			}

			/*
			 * DESIGN copy the necessary files
			 */
			try {
				IOUtils.copy(fc.cont.styleDir(), fc.cont.targetSDir(), Constants.Filters.designFiles);
			} catch (IOException e) {
				CommentAtor.getInstance().ol(e.getLocalizedMessage(), CALevel.ERRORS);
			}

			/*
			 * PARSE look how many files have to be parsed
			 */
			setNFiles(fc.filesNumber());

			/* ***** ***** ***** ***** *****
			 * Handle the files  ***** *****
			 * ***** ***** ***** ***** *****
			 */

			String filenameNE = "", extension = "";

			boolean nowiki;
			SplitPageContainer spc = SplitPageContainer.getInstance();
			SplitPageLinks spl = SplitPageLinks.getInstance();

			Statistics.getInstance().sw.timeInitializingProcess.stop();

			if (!fc.sc.stdOut && fc.cont.styleDir() != null)
				ca.ol(ConstantTexts.styleDir + ": " + fc.cont.styleDirPath(), CALevel.MSG);

			for (short i = 0; i < nFiles; i++) {
				setFileNumber(i);

				in = fc.file(i);

				if (!Container_Files.getInstance().cont.changesMap.needToWrite(in.f.getPath())) {
					ca.ol(i18n.tr("Skipping {0}.", in.f.getPath()), CALevel.MSG);
					continue;
				}

				// Don't process file if it doesn't need to (.html)
				if (Constants.Filters.copyOnly.accept(in.f) || !in.parse) {
					try {
						File f = new File(Container_Files.getInstance().cont.targetDir().getPath() + File.separatorChar + in.f.getName());
						IOUtils.copy(in.f, f);
						CommentAtor.getInstance().ol("Copied: " + in.f.getPath(), CALevel.V_MSG);
						if (in.sitemap) {
							fc.addSitemapEntry(f);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					continue;
				}

				try {
					spc.setSource(IORead_Stats.readSBuffer(in.f));
				} catch (IOException e) {
					Logger.getInstance().log.append(e);
					e.printStackTrace();
				}
				spc.splitPages();
				charsRead += spc.getCharNumber();

				/*
				 * Set the file extension for the output file
				 */

				nowiki = false;
				if (!fc.sc.stdOut) {
					filenameNE = IOUtils.removeFileExtension(in.f).getName();

					if (Constants.Filters.scriptFile.accept(in.f)) {

						extension = in.f.getName().substring(in.f.getName().lastIndexOf('.'));
						xhs.local.set_(SettingsLocalE.scriptMode, "true");

					} else
						extension = ".html";
				}

				spl.setup(filenameNE, extension);

				for (int p = spc.pages.size() - 1; p >= 0; p--) {
					if (spc.currentPageNumber > 1)
						out = new File(fc.cont.targetDir().getPath() + XHTML.fileSep + filenameNE + spc.currentPageNumber + extension);
					else
						out = new File(fc.cont.targetDir().getPath() + XHTML.fileSep + filenameNE + extension);
					if (in.sitemap) {
						fc.addSitemapEntry(out);
					}

					fc.currentFilename = out.getName();
					fc.currentInFile = in;
					xhs.local.content = new StringBuffer(spc.pages.get(p));
					xhs.local.set_(SettingsLocalE.pageNumber, "" + spc.currentPageNumber);
					xhs.local.set_(SettingsLocalE.pagesTotal, "" + spc.pages.size());

					if (!fc.sc.stdOut)
						ca.o(out.getPath() + " ... ", CALevel.MSG);

					if (xhs.local.content.indexOf("<!--nowiki-->") >= 0)
						nowiki = true;

					/*
					 * SCRIPT Cut the script at the top away, if there is one (php)
					 */

					{
						int end;
						if ((end = xhs.local.content.indexOf("<!--endtop-->")) > 0) {
							xhs.local.set_(SettingsLocalE.script, xhs.local.content.subSequence(0, end).toString());
							xhs.local.content.delete(0, end + "<!--endtop-->".length());
						}
					}

					/*
					 * MENU activates the menu entry
					 */
					wm.openItem(filenameNE + extension, WikiMenu.LINK);
					xhs.local.set_(SettingsLocalE.menu, wm.getMenu().toString());

					if (nowiki)
						xhtmlOutput = xhs.local.content;
					else {

						/*
						 * Convert the file
						 */

						/* HEAD Read the head content if available (CSS etc.) */
						xhs.local.readHtmlHead();

						/* ANCHORS Insert anchors */
						ReplaceTags rt;
						rt = new ReplaceTags();
						rt.content = xhs.local.content;
						rt.replaceAnchors();
						xhs.local.content = rt.content;

						/* WIKI2XHTML Convert to XHTML now */
						xhs.local.content = new StringBuffer(XHTML.wiki2xhtml());

						/* NAVI Add the page navigator, if the page is split */
						spl.setLang(LanguageTools.getLocale(LanguageTools.getLanguage(xhs.local.lang().toString())));
						if (spc.pages.size() > 1)
							//TODO 0 Multipage navigator tag ... {{$SplitPageNav}}
							xhs.local.content.append(spl.getLinkList());



						/* TAGS Replace tags like {{TOC}} */
						rt = new ReplaceTags(xhs.local.content, xhs.local.toc());
						rt.replaceAll();
						xhs.local.content = rt.content;

						/* REFERENCES Build references (footnotes) */
						WikiReferences refs = new WikiReferences();
						refs.content = xhs.local.content;
						refs.makeReferences();
						if (refs.refPlaceFound)
							xhs.local.content = refs.content;

						/* WRITE Write the file here, if desired */
						if (!fc.sc.onlyCode)
							try {
								xhtmlOutput = new StringBuffer(XHTML.xhtmlToFile());
							} catch (IOException e) {
								Logger.getInstance().log.append(e);
								e.printStackTrace();
								xhtmlOutput = xhs.local.content;
							}
						else
							xhtmlOutput = xhs.local.content;
					}

					/*
					 * STATS Print some statistics
					 */
					if (fc.sc.stdOut)
						ca.ol(xhtmlOutput, CALevel.ANY);
					else {
						files++;
						charsWritten += out.length();
						try {
							IOWrite_Stats.writeString(out, xhtmlOutput.toString(), false);
						} catch (IOException e) {
							Logger.getInstance().log.append(e);
							e.printStackTrace();
						}
						ca.ol(i18n.tr("done"), CALevel.MSG);

						ca.ol("\n---------------\n" + in.f.getPath() + " > " + out.getPath() + '\n'
							  + String.format(i18n.tr("Title: %s"), xhs.local.title())
							  + "\n---------------\n" + '\n', CALevel.V_MSG);
					}
					spc.currentPageNumber++;

				}
				xhs.init();
			}

			if (!fc.sc.stdOut) {
				if (fc.sc.incremental)
					fc.cont.changesMap.writeHashes();
			}
			
			fc.writeSitemap();

			Statistics.getInstance().sw.timeOverall.stop();


			ca.ol(String.format(i18n.tr("Files written: %d; characters read/written: %d / %d (%s %%).\nTime taken: %s."),
								files, charsRead, charsWritten, ( charsRead > 0 ? (100 * charsWritten / charsRead) + "" : "\u2014" ),
								Statistics.getInstance().sw.timeOverall.getStoppedTimeString()), CALevel.C_MSGS);

			CommentAtor.getInstance().ol(Statistics.getInstance().generateStats(), CALevel.MSG);

			if (Statistics.getInstance().sw.temp.getStoppedTime() > 0) {
				System.err.println(Statistics.getInstance().sw.temp.getStoppedTimeString());
			}
			if (Statistics.getInstance().sw.temp2.getStoppedTime() > 0) {
				System.err.println(Statistics.getInstance().sw.temp2.getStoppedTimeString());

			}

		}

	}

	public static void setFileNumber(short i) {
		fileNumber = (short) (i + 1);
	}

	public static void setNFiles(short i) {
		nFiles = i;
	}

	public static void setProgress(int i) {
		if (nFiles != 0) {
			float p = (float) (fileNumber - 1) / nFiles * 100 + (i / nFiles);
			progress = Math.round(p);
			try {
				Globals.getGuiManager().setProgress(progress);
				Globals.getGuiManager().setTime(Statistics.getInstance().sw.timeOverall.getPassedTime());
			} catch (NullPointerException e) {}
			catch (java.awt.HeadlessException e) {}
		}
	}
}