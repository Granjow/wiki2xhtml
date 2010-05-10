package src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.commentator.CommentAtor;
import src.commentator.Logger;
import src.commentator.CommentAtor.CALevel;
import src.pagesplitter.SplitPageContainer;
import src.pagesplitter.SplitPageLinks;
import src.utilities.IORead_Stats;
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
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 *
 * Replaces tags by various methods
 *
 * @author Simon Eugster
 */
public class ReplaceTags {

	private static I18n i18n = I18nFactory.getI18n(ReplaceTags.class, "bin.l10n.Messages", src.Globals.getLocale());
	private static final CommentAtor ca = CommentAtor.getInstance();

	StringBuffer content = new StringBuffer();
	String toc = new String();

	public ReplaceTags() {	}

	public ReplaceTags(StringBuffer in, String TOC) {
		content = in;
		toc = TOC;
	}

	public void replaceAll() {
		replaceVersion(content);
		replaceTop();
		replaceTOC();
		replacePagename();
		replaceSplitPageLinks();
		replaceWiki2xhtml(content);
	}
	public static StringBuffer replaceVersion(StringBuffer content) {
		return StringTools.replaceAll(content, Constants.Tags.version, Constants.Wiki2xhtml.version);
	}
	public static StringBuffer replaceWiki2xhtml(StringBuffer content) {
		return StringTools.replaceAll(content, Constants.Tags.wiki2xhtml, Constants.Wiki2xhtml.webpage);
	}

	/**
	 * Replaces Top links with either a template or a pre-defined tag
	 */
	private void replaceTop() {
		File t = new File(Container_Files.getInstance().cont.styleDirPath() + File.separatorChar + "Top.txt");
		StringBuffer replace = new StringBuffer();
		if (t.exists() && t.isFile()) {
			try {
				replace = new StringBuffer(IORead_Stats.readSBuffer(t));
			}
			catch (IOException e) {
				Logger.getInstance().log.append(e);
				e.printStackTrace();
			}
		} else
			replace = new StringBuffer("<span class=\"toplink\" ><a href=\"#content\">Top</a></span>\n");
		StringTools.replaceAll(content, Constants.Tags.top, replace.toString());
	}

	/**
	 * Inserts the TOC (only once)
	 * @since 3.3.2
	 */
	private void replaceTOC() {
		java.util.regex.Matcher m = Constants.Tags.regexToc.matcher(content);
		if (m.find()) {
			// Replace TOC Heading
			String s = toc.toString().replace(Constants.Placeholder.toc,
											  (m.group(1) != null && m.group(1).length() > 0 ? m.group(1) : ConstantTexts.toc));
			content.replace(m.start(), m.end(), s);
		}
	}

	private void replacePagename() {
		StringTools.replaceAll(content, Constants.Tags.pagename, Container_Files.getInstance().currentFilename);
	}

	/**
	 * Inserts the navigation between split pages
	 */
	private void replaceSplitPageLinks() {
		if (SplitPageContainer.getInstance().isSplit) {
			StringTools.replaceAll(
				content,
				Constants.Tags.splitPageNav,
				SplitPageLinks.getInstance().getLinkList()
			);
		} else {
			StringTools.replaceAll(content, Constants.Tags.splitPageNav, "");
		}
	}

	/**
	 * Searches for ~~name~~ and replaces it with <a id="name"></a>.
	 */
	public void replaceAnchors() {
		String funcName = this.getClass().getName();
		Matcher m = Pattern.compile(Constants.Tags.anchor).matcher(content.toString());

		if (m.find()) {
			ArrayList<StringBuffer> checklist = new ArrayList<StringBuffer>();
			StringBuffer name = new StringBuffer();
			StringBuffer out = new StringBuffer();
			int last = 0, first;
			do {
				first = m.start();
				out.append(content.subSequence(last, first));
				last = m.end();

				name = new StringBuffer(m.group(1));
				if (checklist.contains(name))
					ca.ol(funcName + String.format(i18n.tr("Warning: Duplicate mark (%s)! Mark removed."), name), CALevel.ERRORS);
				else if (name.indexOf("\"") >= 0) {
					ca.ol(funcName + String.format(i18n.tr("Warning: Mark must not contain such a character (%s)! Mark removed."),
												   "\""), CALevel.MSG);
				} else {
					checklist.add(name);
					out.append("<a id=\"");
					out.append(name);
					out.append("\"></a>");
				}

			} while (m.find());
			out.append(content.subSequence(last, content.length()));
			content = out;
		}
	}

}
