package src;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.Args.GetPolicyE;
import src.utilities.StringTools;



/*
 *   Copyright (C) 2007-2009 Simon Eugster <granjow@users.sf.net>

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
 * Prints the help text (argument --help)
 *
 * @author Simon Eugster
 */
public class Help {

	private static String pre;
	private static String part;
	private static int l;
	private static int width = 50;

	private static final String arg(String arg) {
		return StringTools.fillUp('\n' + pre + arg, l) + part;
	}
	private static final String arg(String arg, String parm) {
		return StringTools.fillUp('\n' + pre + arg + ' ' + parm, l) + part;
	}
	private static final String arg(String arg, String arg2, String parm) {
		return StringTools.fillUp('\n' + pre + arg + ' ' + parm + ", " + arg2 + parm, l) + part;

	}
	private static final String args(String... arg) {
		StringBuffer sb = new StringBuffer();
		for (String s : arg)
			sb.append(s + ", ");
		if (sb.length() > 2)
			sb.delete(sb.length() - 2, sb.length());
		return StringTools.fillUp('\n' + pre + sb, l) + part;
	}
	
	private static final String autoWidth(String s) {
        String out = new String();
        
        int last = 0;
        int next = 0;
        int length = s.length();
        wLoop : for (int i = width; i < length; i += width) {
            next = s.indexOf(' ', i)+1;
            if (next <= last) {
            	break wLoop;
            }
            out += s.substring(last,next) + '\n';
            last = next;
        }
        if ((s.length() - last) < 20) {
            // Remove last newline
            out = out.substring(0, out.length()-2);
        }
        out += s.substring(last);
        
        return out;
	}

	public static String getHelpText(int length, String sPre, String sPart) {

		l = length;
		pre = sPre;
		part = sPart;

		I18n i18n = I18nFactory.getI18n(Help.class, "bin.l10n.Messages", src.Globals.getLocale());

		StringBuilder sb = new StringBuilder();
		sb.append(Constants.Wiki2xhtml.progName + " is licensed under the GPL. See <http://www.gnu.org/licenses/>.");

		sb.append(i18n.tr("\nUsage: java -jar wiki2xhtml [arguments] [files]\n       java -jar wiki2xhtml")
				  + i18n.tr("\n\nArguments "));

		sb.append(arg(Constants.Arguments.Special.menufileUpdater + " [menufile]")
				  + i18n.tr("Update the menufile") + " " + i18n.tr("(No furhter arguments)"));

		sb.append(arg(Constants.Arguments.Flags.help, Constants.Arguments.Flags.helpLong)
				  + i18n.tr("Print this help message"));

		sb.append(arg(Constants.Arguments.Flags.updateCheck) + i18n.tr("Check for updates"));

		sb.append(arg(Constants.Arguments.Flags.noupdatecheck) + i18n.tr("Don't check for updates this time"));

		sb.append(arg(Constants.Arguments.Flags.gui)
				  + i18n.tr("Opens the GUI. Giving no arguments has the same effect."));

		sb.append(args(Constants.Arguments.Flags.dead, Constants.Arguments.Flags.debug, Constants.Arguments.Flags.silent, Constants.Arguments.Flags.verbose)
				  + autoWidth(i18n.tr("Dead mode (no information), debug, silent (errors only), verbose (a lot of text)")));

		sb.append(arg(Constants.Arguments.CombinedArg.lang + "LANG")
				  + autoWidth(String.format(i18n.tr("Set the language used in the program. Availible is at the moment: %s"),
								  "en, en_GB, de, de_CH, fr, fr_CH, it, it_CH, ru, es, hr")));


		sb.append(arg(Constants.Arguments.Arg.menu, Constants.Arguments.CombinedArg.menu, "FILE")
				  + i18n.tr("Menu file"));

		sb.append(arg(Constants.Arguments.Arg.common, Constants.Arguments.CombinedArg.common, "FILE")
				  + i18n.tr("File containing the common settings like the header, the homelink, etc."));

		sb.append(arg(Constants.Arguments.Arg.footer, Constants.Arguments.CombinedArg.footer, "FILE")
				  + i18n.tr("Footer file"));

		sb.append(arg(Constants.Arguments.Arg.targetDir, Constants.Arguments.CombinedArg.targetDir, "DIR")
				  + autoWidth(i18n.tr("Set a target directory. All the html files are created there and the css files are copied into this directory.")));

		sb.append(arg(Constants.Arguments.Arg.style, Constants.Arguments.CombinedArg.style, "DIR")
				  + autoWidth(i18n.tr("Set the directory which contains the css files and the reck (default: ./style/*; random style will be selected if none given)")));


		sb.append(arg(Constants.Arguments.Flags.incremental)
				  + ConstantTexts.incrementalMode);

		sb.append(arg(Constants.Arguments.Flags.stdout)
				  + i18n.tr("Print the output here instead of writing it into a file"));

		sb.append(arg(Constants.Arguments.Flags.onlyCode)
				  + autoWidth(i18n.tr("Only write the (X)HTML code without the reck (if you e.g. want to use the code somewhere else; Means you only want to create plain (X)HTML code without the header etc.)")));

		sb.append(arg(Constants.Arguments.Flags.removeLinebreaks)
				  + i18n.tr("Remove all line breaks in the code."));

		sb.append(arg(Constants.Arguments.Flags.standard)
				  + String.format(i18n.tr("The same as %s"), Constants.Arguments.stdArgs.getArgs(GetPolicyE.AllArgs)));

		sb.append("\n\n"
				  + i18n.tr("Files")
				  + autoWidth(i18n.tr("\n All the files which have to be converted. Files ending with .html will be copied."))
				  + "\n"
				  + i18n.tr("\nExamples")
				  + autoWidth(i18n.tr("\n See the help files for examples (add the argument --helpfiles to create them)."))
				  + "\n"
				  + i18n.tr("\nNotes")
				  + autoWidth(String.format(i18n.tr("\n You need at least Java version 1.5 (currently you are using version %s)."),
								  java.lang.System.getProperty("java.version")) + ' '
				  + String.format(i18n.tr("If your version is too old, you can get the newest version from %s:\n "),
								  java.lang.System.getProperty("java.vendor")))
				  + java.lang.System.getProperty("java.vendor.url") + "\n\n"
				  + String.format(i18n.tr("Your current working directory is: %s"),
								  java.lang.System.getProperty("user.dir"))
				  + "\n\n"
				  + i18n.tr("Bugs") + "\n"
				  + autoWidth(String.format(i18n.tr("Please report bugs, feature requests etc. at Sourceforge, %s, or in the wiki2xhtml forum at %s. Thanks!"),
								  "<http://sf.net/projects/wiki2xhtml/>",
								  "<http://forum.granjow.net>"))
				  + "\n");

		return sb.toString();

	}

}
