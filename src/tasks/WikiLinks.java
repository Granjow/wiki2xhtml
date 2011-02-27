package src.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Statistics;
import src.argumentHandler.*;
import src.project.file.WikiFile;
import src.resources.ResProjectSettings.Settings;
import src.tasks.Tasks.Task;

import static src.Constants.Links.LinksE;
import static src.resources.RegExpressions.RELink;


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
 * Inserts links (external and internal ones).
 */
public class WikiLinks extends WikiTask {
	
	public Task desc() {
		return Task.Links;
	}
	
	public WikiTask nextTask() {
		return new WikiLists();
	}
	
	public void parse(WikiFile file) {
		StringBuffer in;
		StringBuffer out;
		Matcher m;
		int last;
		short counter = 0;

		for (Pattern p : new Pattern[] {RELink.linkExternalWikitype, RELink.linkExternalUri, RELink.linkExternalUriShortened, 
				RELink.linkInternalShort, RELink.linkInternalDescPipe, RELink.linkInternalDescSpace}) {
			in = file.getContent();
			m = p.matcher(in.toString());
			last = 0;
			out = new StringBuffer();
			while (m.find()) {
				out.append(in.subSequence(last, m.start()));
				last = m.end();
				out.append(link(m.group(1), m.group(2), file.name, file.getNamespaces()));
				counter++;
			}
			if (last > 0) {
				// Content has changed; append rest
				out.append(in.substring(last));
				file.setContent(out);
			}
		}
		replaceAnchors(file);
	}

	/** 
	 * @param ignoreQueryFragment Is page#heading or page?a=b to be treated as selflink? See: {@link http://tools.ietf.org/html/rfc3986#section-3}
	 * @return <code>true</code> if the link is leading to the current page.
	 */
	private static final boolean selflink(final String uri, final String pagename, final boolean ignoreQueryFragment) {
		boolean selflink = false;
		
		if (uri.equals(pagename)) {
			selflink = true;
		} else if (ignoreQueryFragment) {
			int pos = uri.indexOf(pagename);
			if (pos >= 0) {
				try {
					char c = uri.charAt(pos + pagename.length());
					if (c == '#' || c == '?') {
						// Query (?a=b) or fragment (#title) following
						selflink = true;
					}
				} catch (IndexOutOfBoundsException e) {
					// Simpler than checking length
				}
			}
		}
		
		return selflink;
	}
	
	private static final String link(final String uri, final String args, 
			final String pagename, final ArrayList<NamespaceObject> linkNamespaces) {
		StringBuilder sLink = new StringBuilder();
		StringBuilder arguments = new StringBuilder();
		LinkObject link = new LinkObject(uri, "");
		
		
		if (uri.startsWith("www.")) link.uri = "http://" + uri;
		else if (uri.startsWith("ftp.")) link.uri = "ftp://" + uri;
		else {
			applyNamespace(link, linkNamespaces);
		}
		
		final boolean external = RELink.linkExternalIndicator.matcher(link.uri).find();
		boolean ignoreQueryFragment = false;
		boolean selflink;

		Iterator<ArgumentItem> it = new ArgumentReaderObject(args).argsList.iterator();
		ArgumentItem ai;
		while (it.hasNext()) {
			ai = it.next();
			
			if (LinksE.disable.arg().equals(ai.fullArg)) {
				ignoreQueryFragment = true;
				continue;
			}
			
			if (LinksE.args.arg().equals(ai.name)) {
				arguments.append(" ");
				arguments.append(ai.argument);
				continue;
			}
			
			if (link.name.length() > 0) link.name += "|";
			link.name += ai.fullArg;
		}
		
		selflink = selflink(uri, pagename, ignoreQueryFragment);
		
		// Append arguments
		if (selflink) {
			arguments.append(" class=\"selflink\"");
		} else if (external) {
			arguments.append(" class=\"external\"");
			Statistics.getInstance().counter.linksExt.increase();
		} else {
			arguments.append(" class=\"internal\"");
			Statistics.getInstance().counter.linksInt.increase();
		}
		
		// Merge arguments (like class argument occurring twice)
		arguments = new StringBuilder(src.utilities.XMLTools.mergeArguments(arguments.toString(), " ", " ", true));
		
		if (selflink) {
			sLink.append("<strong");
			sLink.append(arguments);
			sLink.append(">");
			sLink.append(link.name());
			sLink.append("</strong>");
		} else {
			sLink.append("<a href=\"");
			sLink.append(link.uri);
			sLink.append("\"");
			
			sLink.append(arguments);
		
			sLink.append(">");
			sLink.append(link.name());
			sLink.append("</a>");
		}
		
		return sLink.toString();
	}
	
	

	

	
	private static final Pattern pAnchor = Pattern.compile("~~(.+?)~~");
	/**
	 * Searches for ~~name~~ and replaces it with <a id="name"></a>.
	 */
	public void replaceAnchors(WikiFile file) {
		
		StringBuffer in = file.getContent();
		Matcher m = pAnchor.matcher(in);
		StringBuffer out = null;
		
		if (m.find()) {
			
			ArrayList<String> checklist = new ArrayList<String>();
			String name;
			out = new StringBuffer();
			
			int last = 0, first;
			do {
				first = m.start();
				out.append(in.subSequence(last, first));
				last = m.end();

				name = m.group(1);
				if (checklist.contains(name)) {
					System.err.printf("Warning: Duplicate mark (%s) in %s! Mark removed.\n", name, file.name);
				} else if (name.indexOf("\"") >= 0) {
					System.err.printf("Warning: Mark must not contain such a character (%s) in %s! Mark removed.\n", "\"", file.name);
				} else {
					checklist.add(name);
					out.append("<a id=\"");
					out.append(name);
					out.append("\"></a>");
				}

			} while (m.find());
			
			out.append(in.subSequence(last, in.length()));
			file.setContent(out);
			
		}
	}
	

	
	/** Applies a namespace to a LinkObject */
	public static final LinkObject applyNamespace(LinkObject lo, ArrayList<NamespaceObject> linkNamespaces) {
		if (lo.uri.indexOf(':') < 0) {
			return lo;
		}
		
		for (NamespaceObject no : linkNamespaces) {
			if (no.canReplace(lo)) {
				lo = no.replace(lo);
				break;
			}
		}
		
		return lo;
	}
	

	
	/**
	 * Represents a namespace that can be applied to links.
	 * @since wiki2xhtml 3.4
	 */
	public static final class NamespaceObject {
		
		private static final char sep = ':';
		
		private String key;
		private String value;
		private boolean cut = false;
		
		public NamespaceObject(String key, String value) {
			this.key = key;
			if (value.endsWith(Settings.argCut)) {
				cut = true;
				this.value = value.substring(0, value.length() - Settings.argCut.length());
			} else {
				this.value = value;
			}
		}
		
		public boolean canReplace(LinkObject lo) {
			return lo.uri.startsWith(key + sep);
		}
		
		public LinkObject replace(LinkObject lo) {
			lo.uri = value.replace("%s", lo.uri.substring(key.length()+1));
			if (cut) {
				// Mark link object that the beginning of the name will have to be cut off
				lo.key = key + sep;
			}
			return lo;
		}
		
	}
	
	/**
	 * Represents a link consisting of a name and an URI
	 * @since wiki2xhtml 3.4 */
	public static class LinkObject {
		
		public String uri;
		private String name;
		public String key = ""; 
		
		public LinkObject(String uri, String name) {
			this.uri = uri;
			this.name = name;
		}
		
		/** If uncut is false, e.g. a trailing w: from the namespace will be removed.
		 *  Returns the URI if no name available. */
		public String name(boolean uncut) {
			// If name is not empty, it is set by the user -> no cutting off!
			String s = name.length() > 0 ? name : uri;
			if (!uncut) {
				if (s.startsWith(key)) s = s.replaceFirst(key, "");
			}
			return s;
		}
		public String name() { return name(false); }
		public void setName(String name) { this.name = name; }
		
	}

}
