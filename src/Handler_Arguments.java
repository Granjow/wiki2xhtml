package src;

import java.io.File;
import java.util.regex.Matcher;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.Args.SearchPolicyE;
import src.argsFilesReader.ArgsFilesReader;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.resources.RegExpressions;
import src.update.UpdateChecker;
import src.utilities.StringTools;

import static src.Args.GetPolicyE;



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
 * Handles arguments from the command line
 *
 * TODO 2 start GUI when a) no files have to be parsed and b) no text output (like --www) required
 *
 * @author Simon Eugster
 */
public class Handler_Arguments {

	private static final I18n i18n = I18nFactory.getI18n(Handler_Arguments.class, "bin.l10n.Messages", src.Globals.getLocale());

	public static StringBuffer completeSummary = new StringBuffer();

	final static String info = i18n.tr("Info: ");

	final static String warn = i18n.tr("WARNING: ");

	public static void printHelp() {
		CommentAtor.getInstance().ol(Help.getHelpText(0, "\n   ", "\n"), CALevel.ANY);
	}

	private static void noteEq(short i, String arg) {
		CommentAtor.getInstance().ol(warn + String.format(i18n.tr("No file given for the argument Nr. %d (%s). "), i, arg)
									 + i18n.tr("Maybe you set a space before the filename?"), CALevel.MSG);
	}

	private static void noteEqD(short i, String arg) {
		CommentAtor.getInstance().ol(warn + String.format(i18n.tr("No directory given for the argument Nr. %d (%s). "), i, arg)
									 + i18n.tr("Maybe you set a space before the filename?"), CALevel.MSG);
	}

	private static void noteForgot(short i, String arg, String what) {
		CommentAtor.getInstance().ol(warn + String.format(i18n.tr("You forgot the %s to the argument Nr. %d (%s). "), what, i, arg), CALevel.MSG);
	}

	private static final void obsolete(String s) {
		CommentAtor.getInstance().ol(String.format(i18n.tr("The argument \u201c%s\u201d is obsolete and no longer supported."), s) + '\n'
									 + ConstantTexts.seeHelp, CALevel.ANY);
	}

	public static String handleReplaceArguments(String arguments) {

		/*
		 * *********************************************************************
		 * **** Handle the arguments --helpfile(s) and --standard
		 * *********************************************************************
		 */

		if (arguments.contains("--helpfiles-de")) {

			StringBuffer sb = new StringBuffer(
				Constants.Arguments.stdArgsHelpDe.getArgs(GetPolicyE.AllArgs).append(arguments)
				+ " ");

			StringTools.replaceAll(sb, "--helpfiles-de", "");
			StringTools.replaceAll(sb, "  ", " ");

			arguments = sb.toString();
		}
		if (arguments.contains("--helpfiles")) {

			StringBuffer sb = new StringBuffer(
				Constants.Arguments.stdArgsHelpEn.getArgs(GetPolicyE.AllArgs).append(arguments)
				+ " ");

			StringTools.replaceAll(sb, "--helpfiles", "");
			StringTools.replaceAll(sb, "  ", " ");

			arguments = sb.toString();
		}
		if (arguments.contains("--standard")) {

			StringBuffer sb = StringTools.replaceAll(
								  new StringBuffer(Constants.Arguments.stdArgs.getArgs(GetPolicyE.AllArgs) + arguments + " "),
								  "--standard", "");
			StringTools.replaceAll(sb, "  ", " ");

			arguments = sb.toString();
		}
		Matcher m = RegExpressions.argsFile.matcher(arguments);
		if (m.find()) {
			arguments = arguments.substring(0, m.start()) + arguments.substring(m.end());
			ArgsFilesReader afr = new ArgsFilesReader();
			arguments += afr.read(m.group(1));
		}
		return arguments;
	}

	public static boolean handleCommandLineArguments(Args args, boolean execute) {
		return handleCommandLineArguments(args, execute, false);
	}
	/**
	 * Sets up the CommentAtor and the File
	 * @param args
	 * @param execute
	 * @return true, if wiki2xhtml has to be run (GUI not started)
	 */
	public static boolean handleCommandLineArguments(Args args, boolean execute, boolean noclear) {

		Container_Files fc = Container_Files.getInstance();
		if (!noclear) { 
			fc.clear();	// Reset all settings
		}

		if (args.size() == 0) {
			if (execute) {
				/*
				 * No arguments given, so try to open GUI
				 */
				CommentAtor.getInstance().ol(i18n.tr("Opening GUI."), CALevel.MSG);

				UserInterface.runGui(null, false);

				return false;
			}
		} else {

			String arg;
			int l = args.size();
			boolean gui = false;


			/*
			 * Pre-processing
			 */
			int pos;
			pos = args.pos(Constants.Arguments.Arg.sourceDir, SearchPolicyE.Equals);
			if (pos >= 0 && (pos+1) < l) {
				arg = args.get(pos+1, GetPolicyE.AllArgs);
				boolean ok = fc.setSourceDirectory(arg);
				if (!ok) {
					CommentAtor.getInstance().ol("Could not find " + arg.substring(arg.indexOf('=')+1), CALevel.ERRORS);
				} else {
					args.setHandled(pos, true);
					args.setHandled(pos+1, true);
				}
			}
			pos = args.pos(Constants.Arguments.CombinedArg.sourceDir, SearchPolicyE.StartsWith);
			if (pos >= 0) {
				arg = args.get(pos, GetPolicyE.AllArgs);
				boolean ok = fc.setSourceDirectory(arg.substring(arg.indexOf('=')+1));
				if (!ok) {
					CommentAtor.getInstance().ol("Could not find " + arg.substring(arg.indexOf('=')+1), CALevel.ERRORS);
				} else {
					args.setHandled(pos, true);
				}
			}


			boolean ok;
			/*
			 * Processing
			 */
			for (short i = 0; i < args.size(); i++) {

				arg = args.getPlain(i, Args.GetPolicyE.UnhandledOnly);

				if (arg.length() == 0) { // Argument has already been handled, e.g. because it was a file.
					continue;
				}

				File f;
				if ((f = fc.fileExists(arg, true)) != null) {
					ok = fc.addFile(arg);
					if (!ok) {
						CommentAtor.getInstance().ol(i18n.tr("Could not find file %1!", f.getAbsolutePath()), CALevel.ERRORS);
					}
					continue;
				}

				/* Flags */

				if (arg.equals(Constants.Arguments.Flags.dead)) {
					CommentAtor.getInstance().setDead(true);
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.debug)) {
					CommentAtor.getInstance().setDebug();
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.gui)) {
					gui = true;
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.help)) {
					printHelp();
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.helpfiles))
					continue;	// handled by handleReplaceArguments()

				if (arg.equals(Constants.Arguments.Flags.helpfilesDe))
					continue;

				if (arg.equals(Constants.Arguments.Flags.helpLong)) {
					printHelp();
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.incremental)) {
					fc.sc.incremental = true;
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.noupdatecheck)) {
					fc.sc.noUpdateCheck = true;
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.onlyCode)) {
					fc.sc.onlyCode = true;
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.removeLinebreaks)) {
					fc.sc.removeLineBreaks = true;
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.silent)) {
					CommentAtor.getInstance().setSilent();
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.standard))
					continue;

				if (arg.equals(Constants.Arguments.Flags.stdout)) {
					fc.sc.stdOut = true;
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.typos)) {
					CommentAtor.getInstance().ol(Constants.Arguments.Flags.typos + ": " + ConstantTexts.notYetSupported, CALevel.MSG);
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.updateCheck)) {
					System.out.println();
					System.out.println(UpdateChecker.getUpdateMessage());
					continue;
				}

				if (arg.equals(Constants.Arguments.Flags.verbose)) {
					CommentAtor.getInstance().setVerbose();
					continue;
				}


				/* Args */

				if (arg.equals(Constants.Arguments.Arg.common)) {
					if (i+1 < l) {
						fc.setCommonFile(args.getPlain(i+1, GetPolicyE.AllArgs));
						args.setHandled(i+1, true);
					} else {
						noteForgot(i, arg, ConstantTexts.NounCap.commonFile);
					}
					continue;
				}

				if (arg.equals(Constants.Arguments.Arg.footer)) {
					if (i+1 < l) {
						fc.setFooterFile(args.getPlain(i+1, GetPolicyE.AllArgs));
						args.setHandled(i+1, true);
					} else {
						noteForgot(i, arg, ConstantTexts.NounCap.footerFile);
					}
					continue;
				}

				if (arg.equals(Constants.Arguments.Arg.menu)) {
					if (i+1 < l) {
						fc.setMenuFile(args.getPlain(i+1, GetPolicyE.AllArgs));
						args.setHandled(i+1, true);
					} else {
						noteForgot(i, arg, ConstantTexts.NounCap.menuFile);
					}
					continue;
				}

				if (arg.equals(Constants.Arguments.Arg.style)) {
					if (i+1 < l) {
						fc.setStyleDirectory(args.getPlain(i+1, GetPolicyE.AllArgs));
						args.setHandled(i+1, true);
					} else {
						noteForgot(i, arg, ConstantTexts.styleDir);
					}
					continue;
				}

				if (arg.equals(Constants.Arguments.Arg.targetDir)) {
					if (i+1 < l) {
						fc.setTargetDirectory(args.get(i+1, GetPolicyE.AllArgs));
						args.setHandled(i+1, true);
					} else {
						noteForgot(i, arg, ConstantTexts.targetDir);
					}
					continue;
				}


				/* Combined Args */

				if (arg.startsWith(Constants.Arguments.CombinedArg.footer)) {
					if (arg.length() > Constants.Arguments.CombinedArg.footer.length()) {
						fc.setFooterFile(arg.substring(arg.indexOf('=')+1));
					} else {
						noteEq(i, args.get(i, GetPolicyE.AllArgs));
					}
					continue;
				}

				if (arg.startsWith(Constants.Arguments.CombinedArg.lang)) {
					if (arg.length() > Constants.Arguments.CombinedArg.lang.length()) {
						src.Globals.setLocale(arg.substring(arg.indexOf('=')+1));
						CommentAtor.getInstance().ol(String.format(i18n.tr("Language is now %s"), src.Globals.getLocale().toString()), CALevel.DEBUG);
					} else {
						noteEq(i, args.get(i, GetPolicyE.AllArgs));
					}
					continue;
				}

				if (arg.startsWith(Constants.Arguments.CombinedArg.menu)) {
					if (arg.length() > Constants.Arguments.CombinedArg.menu.length()) {
						fc.setMenuFile(arg.substring(arg.indexOf('=')+1));
					} else {
						noteEq(i, args.get(i, GetPolicyE.AllArgs));
					}
					continue;
				}
				
				if (arg.startsWith(Constants.Arguments.CombinedArg.sitemap)) {
					if (arg.length() > Constants.Arguments.CombinedArg.sitemap.length()) {
						fc.setSitemap(arg.substring(arg.indexOf('=')+1));
					} else {
						noteEq(i, args.get(i, GetPolicyE.AllArgs));
					}
					continue;
				}

				if (arg.startsWith(Constants.Arguments.CombinedArg.style)) {
					if (arg.length() > Constants.Arguments.CombinedArg.style.length()) {
						fc.setStyleDirectory(arg.substring(arg.indexOf('=')+1));
					} else {
						noteEqD(i, args.get(i, GetPolicyE.AllArgs));
					}
					continue;
				}

				if (arg.startsWith(Constants.Arguments.CombinedArg.targetDir)) {
					if (arg.length() > Constants.Arguments.CombinedArg.targetDir.length()) {
						fc.setTargetDirectory(arg.substring(arg.indexOf('=')+1));
					} else {
						noteEqD(i, args.get(i, GetPolicyE.AllArgs));
					}
					continue;
				}


				/* Obsolete */

				boolean obsolete = false;
				for (String s : Constants.Arguments.obsoleteFlags) {
					if (arg.equals(s)) {
						obsolete = true;
						obsolete(args.get(i, GetPolicyE.AllArgs));
						break;
					}
				}
				if (obsolete) continue;

				for (String s : Constants.Arguments.obsoleteCombined) {
					if (arg.equals(s)) {
						obsolete = true;
						obsolete(args.get(i, GetPolicyE.AllArgs));
						break;
					}
				}
				if (obsolete) continue;

				for (String s : Constants.Arguments.obsoleteArgs) {
					if (arg.equals(s)) {
						obsolete = true;
						obsolete(args.get(i, GetPolicyE.AllArgs));
						args.setHandled(i+1, true);
						break;
					}
				}
				if (obsolete) continue;

				CommentAtor.getInstance().ol(warn + String.format(i18n.tr("The argument %s could not be recognized. "), arg) + ConstantTexts.seeHelp, CALevel.ERRORS);

			}

			StringBuffer fileList;
			if ((fileList = fc.fileList().getArgs(GetPolicyE.AllArgs)).length() > 0) {
				CommentAtor.getInstance().ol(ConstantTexts.files + ": " + fileList, CALevel.MSG);
			}

			if (gui && execute) {
				UserInterface.runGui(null, false);
				return false;
			}

		}

		return true;
	}
}
