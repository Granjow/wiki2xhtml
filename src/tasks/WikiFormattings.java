/*
 *   Copyright (C) 2010-2011 Simon A. Eugster <simon.eu@gmail.com>

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
 */


package src.tasks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Constants.Template_Blocks;
import src.Constants;
import src.Constants.Templates;
import src.Statistics;
import src.project.FallbackFile;
import src.project.file.WikiFile;
import src.ptm.PTMObject.RecursionException;
import src.ptm.PTMRootNode;
import src.ptm.PTMState;
import src.resources.RegExpressions;
import src.tasks.Tasks.Task;

/**
 * <p>Handles {@code ''italic text''}, {@code '''bold text'''}, and {@code $$code$$}.</p>
 * <h3>Code</h3>
 * <p>The template defined in {@link Templates} is used for this code block (and can be overridden,
 * as always, by putting your own file with this name in your base or style directory).</p>
 * <p>This is a simplified version of a real template and therefore less powerful with argument passing
 * regarding nested arguments. Arguments are separated by a <code>|</code>. Always. Here is a usage example
 * for some inline code:</p>
 * <p>{@code $$((figcaption=An HTML5 caption|id=sampleBlock))text$$}</p>
 * <p>or, as code block (this will set the {@link Template_Blocks#isBlock} argument to "true"):</p>
 * <p><code> $$((figcaption=An HTML5 caption|id=sampleBlock))<br/>
 * text<br/>
 * $$</code></p>
 * <p>The HTML5 template might look like this:</p>
 * <p><code>&lt;figure class="code{{#if:{{{isBlock|}}}| block}}"{{#if:{{{id|}}}| id="{{{id}}}"}}&gt;<br/>
 * &lt;figcaption&gt;{{{figcaption|}}}&lt;/figcaption&gt;<br/>
 * {{{text}}}<br/>
 * &lt;/figure&gt;</code></p>
 */
public class WikiFormattings extends WikiTask {


	public WikiTask nextTask() {
		return new WikiParagraphs();
	}

	public Task desc() {
		return Task.Formattings;
	}

	public void parse(WikiFile file) {
		file.setContent(makeCode(file, makeItalicType(makeBoldType(file.getContent()))));
	}
	
	

	/**
	 * @return Input where '''text marked as bold''' is replaced with <strong>text marked as bold</strong>
	 */
	public static final StringBuffer makeBoldType(final StringBuffer in) {

		StringBuffer out = new StringBuffer();
		Matcher m = Pattern.compile("'''(.*?)'''").matcher(in.toString());;

		if (m.find()) {

			Statistics.getInstance().sw.timeFormattingCode.continueTime();
			int first, last = 0;
			short counter = 0;

			do {
				first = m.start();
				out.append(in.subSequence(last, first));
				out.append("<strong>");
				last = m.end();
				out.append(m.group(1));
				out.append("</strong>");
				counter++;
			} while (m.find());
			out.append(in.subSequence(last, in.length()));


			/* Statistics */
			Statistics.getInstance().counter.boldType.increase(counter);
			Statistics.getInstance().sw.timeFormattingCode.stop();

		} else {
			out = in;
		}

		return out;
	}

	/**
	 * @return  Input where ''text marked as italic'' is replaced with <em>text marked as italic</em>
	 */
	public static final StringBuffer makeItalicType(final StringBuffer in) {

		StringBuffer out = new StringBuffer();
		Matcher m = Pattern.compile("''(.*?)''").matcher(in.toString());

		if (m.find()) {
			Statistics.getInstance().sw.timeFormattingCode.continueTime();
			int first, last = 0;
			short counter = 0;

			do {
				first = m.start();
				out.append(in.subSequence(last, first));
				out.append("<em>");
				last = m.end();
				out.append(m.group(1));
				out.append("</em>");
				counter++;
			} while (m.find());
			out.append(in.subSequence(last, in.length()));

			/* Statistics */
			Statistics.getInstance().sw.timeFormattingCode.stop();
			Statistics.getInstance().counter.italicType.increase(counter);

		} else {
			out = in;
		}

		return out;
	}
	
	public static final StringBuffer makeCode(final WikiFile file, final StringBuffer input) {

		Statistics.getInstance().sw.timeFormattingCode.continueTime();
		
		StringBuffer template;
		try {
			template = new FallbackFile(Constants.Templates.sTplCode, file.project).getContent();
		} catch (Exception e) {
			e.printStackTrace();
			template = new StringBuffer(e.getMessage());
		}
		assert template.length() > 0;
		
		StringBuffer in = input;
		StringBuffer out = new StringBuffer();
		String s;
		String args;

		PTMState sigma;
		
		Pattern p;
		Matcher m;
		int last;
		
		p = RegExpressions.textCode;
		m = p.matcher(in);

		if (m.find()) {
			m.reset();
			last = 0;
			
			while (m.find()) {
				out.append(in.substring(last, m.start()));
				
				args = (m.group(2) != null ? m.group(2).trim() : "");

				sigma = new PTMState()
					.b(Template_Blocks.text, m.group(3));
				
				if (args.length() > 0) {
					sigma.bindValuesFromList(args.split("\\|"));
				}
				
				out.append(m.group(1));
				
				try {
					s = new PTMRootNode(template, sigma).evaluate();
				} catch (RecursionException e) {
					e.printStackTrace();
					s = e.getMessage();
				}
				out.append(s);

				last = m.end();
			}
			out.append(in.substring(last));
			
		} else {
			out = in;
		}

		
		
		in = out;
		out = new StringBuffer();

		p = RegExpressions.textCodeBlock;
		m = p.matcher(in);

		if (m.find()) {
			m.reset();
			last = 0;
			
			while (m.find()) {
				out.append(in.substring(last, m.start()));
				
				args = (m.group(1) != null ? m.group(1).trim() : "");
				
				sigma = new PTMState()
					.b(Template_Blocks.text, m.group(2))
					.b(Template_Blocks.isBlock, "true");
				
				if (args.length() > 0) {
					sigma.bindValuesFromList(args.split("\\|"));
				}
				
				try {
					s = new PTMRootNode(template, sigma).evaluate();
				} catch (RecursionException e) {
					e.printStackTrace();
					s = e.getMessage();
				}
				out.append(s);
				
				last = m.end();
			}
			out.append(in.substring(last));
		} else {
			out = in;
		}


		Statistics.getInstance().sw.timeFormattingCode.stop();

		return out;
	}

}
