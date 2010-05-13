package src;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;

import src.settings.*;
import src.typo.Formattings;
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
 * The main file of wiki2xhtml which parses the files.
 *
 * @author Simon Eugster
 */
public class XHTML {

	public static final String operatingSystem = java.lang.System
			.getProperty("os.name"), fileSep = java.lang.System
											   .getProperty("file.separator"), lineSepOS = java.lang.System.getProperty("line.separator"),
													   lineSep = "\n";

	public static File baseStyleDir = new File(Constants.Directories.workingDir + File.separatorChar + "style");


	public static ArrayList<String> headingIDs = new ArrayList<String>();
	public static void clearHeadings() {
		headingIDs.clear();
	}

	private static Container_Files fc = Container_Files.getInstance();
	private static XhtmlSettings xhs = XhtmlSettings.getInstance();

	public static void updateBaseStyleDir(File f) {
		baseStyleDir = f;
	}


	public static String PC() {
		StringBuffer out = new StringBuffer();
		out.append("\n=====");
		out.append("\nOS: " + java.lang.System.getProperty("os.name") + " (" + java.lang.System.getProperty("os.arch") + ")");
		out.append("\nHi " + java.lang.System.getProperty("user.name") + "!");
		out.append("\nJava Version: " + java.lang.System.getProperty("java.version"));
		out.append("\nPosition: " + java.lang.System.getProperty("user.dir"));
		out.append("\nFile Separator: " + java.lang.System.getProperty("file.separator"));
		if (lineSepOS.equals("\r\n"))
			out.append("\nLine Separator: \\r\\n (Windows)");
		else if (lineSepOS.equals("\n\r"))
			out.append("\nLine Separator: \\n\\r (Mac)");
		else
			out.append("\nLine Separator: \\n (Unix)");

		out.append("\n=====\n");
		return out.toString();
	}

	/**
	 * Insert title, body etc in the template and returns the result.
	 *
	 * @param reck says how and where to fill in the HTML code
	 * @param xhs XHTML Settings
	 * @return Complete (and perhaps correct) XHTML code. The content will have
	 *         been generated by the function wiki2xhtml.
	 */
	public static StringBuffer xhtmlToFile() throws IOException {
		StringBuffer out = new StringBuffer();

		if (xhs.local.contains(SettingsLocalE.script)) {
			out.append(xhs.local.get_(SettingsLocalE.script));
		}
		out.append(Container_Files.getInstance().cont.reck());

		int i, j;
		for (i = 0; (i = out.indexOf(">menu>", i)) < out.length() && i >= 0;) {
			/*
			 * Remove menu tags
			 */
			if ((j = out.indexOf("<menu<", i)) > 0) {
				if (!xhs.local.contains(SettingsLocalE.menu)) {
					// No menu: Delete everything between the two tags
					out.delete(i, j + "<menu<".length());
				} else {
					// Delete tags only
					out.delete(j, j + "<menu<".length());
					out.delete(i, i + ">menu>".length());
				}
			}
		}
		for (i = 0; (i = out.indexOf(">footer>", i)) < out.length() && i >= 0;) {
			/*
			 * Remove footer tags
			 */
			if ((j = out.indexOf("<footer<", i)) > 0) {
				if (xhs.global.footer == null) {
					// No footer: Delete everything between the two tags
					out.delete(i, j + "<footer<".length());
				} else {
					// Delete tags only
					out.delete(j, j + "<footer<".length());
					out.delete(i, i + ">footer>".length());
				}
			}
		}

		out = StringTools.replaceAll(out, Constants.TemplateTags.meta, xhs.local.metadata());
		out = StringTools.replaceAll(out, Constants.TemplateTags.title, xhs.local.title());
		out = replaceHead(out);
		out = StringTools.replaceAll(out, Constants.TemplateTags.textheader, xhs.local.get_(SettingsE.textHeader));
		out = StringTools.replaceAll(out, Constants.TemplateTags.menu, xhs.local.get_(SettingsLocalE.menu));
		out = StringTools.replaceAll(out, Constants.TemplateTags.footer, xhs.global.footer);
		out = StringTools.replaceAll(out, Constants.TemplateTags.h1, xhs.local.h1());
		out = StringTools.replaceAll(out, Constants.TemplateTags.homelink, xhs.homelink());
		out = StringTools.replaceAll(out, Constants.TemplateTags.content, xhs.local.content.toString());

		return out;
	}

	private static StringBuffer replaceHead(StringBuffer in) {

		StringBuffer n = new StringBuffer();
		String head;
		int last = 0;
		Matcher m = Constants.TemplateTags.regexHead.matcher(in.toString());
		while (m.find()) {
			n.append(in.substring(last, m.start()));
			head = xhs.local.head(m.group(1));
			if (head != null) n.append(head);
			last = m.end();
		}
		if (n.length() > 0) {
			n.append(in.substring(last));
			return n;
		}
		return in;
	}

	private static void p(int i) {
		UserInterface.setProgress(i);
	}

	/**
	 * Convert from Wiki to HTML
	 *
	 * @param ims
	 * @return converted HTML file (StringBuffer)
	 */
	public static StringBuffer wiki2xhtml() {
		StringBuffer out = new StringBuffer();
		Container_Settings sc = Container_Files.getInstance().sc;

		/*
		 * Clear the summary and the nowiki content, then parse the source
		 */
//		nowiki.clear();
		out = xhs.local.content;

//		out = makeUnixLines(out);
		p(2);
//		out = XhtmlSettingsReader.getXhtml(out);
		p(4);
//		out = WikiNoWiki.removeNowikiContent(out);
		p(6);
//		out = src.templateHandler.TemplateManager.applyTemplates(out, null, null);
		p(10);
//		out = src.parserFunctions.Parser.parse(out);
		p(17);
		out = WikiTables.makeTables(out);
		p(20);
		out = WikiHeadings.makeHeadings(out);
		p(36);
		if (fc.has.target)
			out = WikiImages.makeImages(out);
		else
			out = WikiImages.makeImages(out);
		p(44);
//		out = WikiLinks.makeLinks(out, fc.currentFilename);
		p(55);
//		out = WikiLists.makeList(out);
		p(69);
		out = WikiImages.makeGallery(out);
		p(76);
		out = makeHLines(out);
		p(79);
		// TODO 9 activate typograph as soon as working
//		Typograph t = new Typograph(); out = t.replace(out, "");
		p(80);
		out = Formattings.format0r(out, fc.currentFilename, Formattings.BOLD | Formattings.ITALIC | Formattings.MANY, true);
		p(86);
		out = Formattings.makeItalicType(out, true);
		p(92);
		StringTools.replaceAll(out, "\\\\ \n", "<br />\n");
		StringTools.replaceAll(out, "\\\\ ", "<br />\n");
		p(94);
		out = WikiParagraphs.makeParagraphs(out);
		p(96);
//		out = WikiNoWiki.insertNowikiContent(out);
		p(98);
		if (sc.removeLineBreaks)
			out = StringTools.removeAllChars(out, "\n");
		if (lineSepOS.equals("\r\n"))
			out = makeWindowsLines(out);
		p(99);

		while (out.length() > 0 && out.charAt(0) == '\n')
			out.deleteCharAt(0);

		p(100);

		return out;
	}

	/**
	 * Remove the \r (Carriage Return) from Windows files and leave only the \n
	 * back
	 *
	 * @param in -
	 *            input
	 * @return input without \r
	 */
	public static StringBuffer makeUnixLines(StringBuffer in) {
		return StringTools.replaceAll(in, "\r", "");
	}

	/**
	 * Make a file with «Windows lines» (with carriage returns)
	 *
	 * @param in
	 * @return input with \r
	 *
	 */
	public static StringBuffer makeWindowsLines(StringBuffer in) {
		// Regular Expression for the Windows Linebreaks:
		// \r\n (Carriage Return + Line Feed)
		return new StringBuffer(in.toString().replaceAll("\\n", "\r\n"));
	}

	public static StringBuffer makeHLines(StringBuffer in) {

		StringBuffer out = new StringBuffer();
		Matcher m = Pattern.compile("(?m)^----\\s*$").matcher(in.toString());

		short counter = 0;

		if (m.find()) {
			int last = 0, first;
			do {
				counter++;
				first = m.start();
				out.append(in.subSequence(last, first));
				last = m.end();
				out.append("<hr />" + lineSep);
			} while (m.find());
			out.append(in.subSequence(last, in.length()));
		} else
			out = in;

		Statistics.getInstance().counter.horizontalLines.increase(counter);

		return out;
	}
}
