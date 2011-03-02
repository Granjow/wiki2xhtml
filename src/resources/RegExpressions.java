/*
 *   Copyright (C) 2007-2011 Simon A. Eugster <simon.eu@gmail.com>

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

package src.resources;

import java.util.regex.Pattern;

import src.Constants;

/**
 * Pre-compiled regular expression. Will only be compiled once.
 */
public final class RegExpressions {
	


	/** Punctuation marks. Don't forget to enclose it with []! */
	private static final String specialCharacters = "\\s.,:;?!'\\(\\)<>-";

	/** Matches a CDATA section.
	 *  Group 1 contains the CDATA (Character data), group 0 contains &lt;![CDATA[\1]]&gt; */
	public static final String rCDATA = "<!\\[CDATA\\[(.*?)\\]\\]>";

	/** Matches a CDATA tag */
	private static final String rCdataNogroups = "<!\\[CDATA\\[.*?\\]\\]>";
	

	/** Matches a number. */
	public static final Pattern num = Pattern.compile("(\\d+)");
	
	/** Matches something.args */
	public static final Pattern argsFile = Pattern.compile(
			"(?x)" +
			"(?<=(?:^|\\s))([\\w]+\\.args)(?=(?:$|\\s))	# \\p{L}: Word boundary"
	);

	public static final Pattern tagContent = Pattern.compile(
				"(?x)" +
				"(?:<[^\\/][^>]+>)(.*?)(?:<\\/[^\\>]+>)"
			);

	/** Matches nowiki content.
	 *  Group 1 contains the content, if available, group 0 contains &lt;nowiki&gt;\1&lt;/nowiki&gt;. */
	public static final Pattern nowikiContent = Pattern.compile("(?si)<nowiki>(.*?)</nowiki>");


	public static final Pattern redirect = Pattern.compile(
											   "(?mx)			# Multiline mode (^$), comments \n" +
											   "^\\#REDIRECT	# Line starts with #REDIRECT \n" +
											   "[\\ ]			# Then a space \n" +
											   "(\\d+)			# and some (captured) digits (timeout) ... \n" +
											   "[\\ ]			# again a space \n" +
											   "(.+)$			# ... and more chars (link) until line end."
										   );

	/**
	 * Matches wiki headings.
	 * Group 1 contains the heading, group 0 contains ={i}\1={i}\s*
	 * @param i Number of equality signs
	 * @return The pattern matching a heading of level i
	 */
	public static final Pattern wikiHeading(int i) {
		return Pattern.compile(
				   "(?x)(?m)		# Allow comments, multiline (dot doesn't match new lines) \n" +
				   "^={"+i+"}		# Exactly i equality signs at the beginning of the line \n" +
				   "(.+[^=]+)		# Any characters, followed by at least one non-equal-sign-character \n" +
				   "={"+i+"}\\s*$	# Exactly i equality signs at the end of the line, eventualy followed by spaces"
			   );
	}

	/** If a parameter has a name given with name=attribute, the regex will match. */
	public static final Pattern templateParameter = Pattern.compile("^\\s*((?:" + rCdataNogroups + "|\\S)*?)\\s*=\\s*((?:" + rCdataNogroups + "|.)*)$");

	/** Matches a CDATA section.
	 *  Group 1 contains the content, group 0 contains &lt;![CDATA[\1]]!&gt;. */
	public static final Pattern cdata = Pattern.compile(rCDATA);

	/** Matches an argument; multiple arguments are separated by a pipe symbol. May contain CDATA blocks.
	 *  Group 1 contains the argument, group 0 the pipe symbol plus the content. */
	public static final Pattern argument = Pattern.compile(
											   "(?x)(?s)			# Allow comments, dot matches all \n" +
											   "\\|				# Argument has to start with a pipe symbol to allow empty arguments; \n" +
											   "#	the pipe symbol may have to be added first before searching. \n" +
											   "((?:				# Argument may contain: \n" +
											   rCdataNogroups + "|		# A CDATA section or \n" +
											   "[^|]							# all characters except of a pipe \n" +
											   ")*)				# in any order, or nothing at all."
										   );


	/** Pattern matching the head block.
	 *  Group 1 contains the head content, group 0 contains &lt;!--head--&gt;\1&lt;!--/head--&gt; */
	public static final Pattern head = Pattern.compile(
										   "(?i)(?x)(?s)		# Ignore case, Allow comments, Dot matches all \n" +
										   "<!--head-->		# Start of the head block \n" +
										   "(.*?)				# head content \n" +
										   "<!--\\/head-->		# End of head block"
									   );

	/** Pattern for images.
	 *  Group 1 contains the arguments, group 0 contains [[(image|bild):\1]] */
	public static final Pattern images = Pattern.compile(
											 "(?i)(?x)			# Ignore case, Allow comments \n" +
											 "\\[\\[				# Two square brackets \n" +
											 "(" +
											 "(?:image|bild|file|datei):	# Followed by imge: or bild: \n" +
											 "(?:					# Followed by ... \n" +
											 "[^\\n\\[\\]]				# Anything except a new line or square brackets \n" +
											 "|							# or ... \n" +
											 "\\[\\[[^\\n\\[\\]]*\\]\\]	# a link inside of two square brackets \n" +
											 "|							# or ... \n" +
											 "\\[[^\\n\\[\\]]*\\]		# an external link inside of ever one square bracket \n" +
											 ")++					# All that several sw, at least once. Possessive for faster failing(?). \n" +
											 ")" +
											 "\\]\\]				# Followed finally by two closing brackets"
										 );
	

	

	/** Pattern for list arguments (referring to ul, ol etc, not li) */
	public static final Pattern listGroupArguments = Pattern.compile(
				"(?x)				# Allow comments \n" +
				"(?:\\s*\\(\\()		# Non-capturing group: 0 or more spaces followed by (( \n" +
				"(.*?)				# Capturing group: any character \n" +
				"(?:\\)\\))			# Non-capturing group: Closing ))"
			);
	
	public static final Pattern namespace = Pattern.compile("(?m)^(.+?)=(.+)$");

	/** <p>Matches:</p>
	 * <ul><li><code>$$code$$</code></li>
	 * <li><code>$$((args))code$$</code></li></ul>
	 * <p>Pattern groups: <code>[1: character]$$((2:args))3:code$$</code></p>
	 * <p>The first character is used to avoid lookbehinds (speedup).</p>
	 * @see #textCodeBlock*/
	public static final Pattern textCode = Pattern.compile(
											  "(?x)				# just like above \n" +
											  "(?:" +
												  "([" + specialCharacters + "]|^)" +
												  "\\$\\$(?!\\s)" +
											  ")" +
											  "(?:\\(\\((.+?)\\)\\))?" +
											  "(.*?[^\\s])" +
											  "(?:\\$\\$(?=(?:[" + specialCharacters + "]|$)))"
										  );

	/** <p>Matches <br/>$$<br/>code blocks<br/>$$</p>
	 * <p>The first <code>$$</code> may be followed by <code>((arguments))</code>.</p>
	 * <p>Pattern groups: <code>$$\n((1:args))\n2:code\n$$</code></p>
	 * @see #textCode */
	public static final Pattern textCodeBlock = Pattern.compile(
				"(?x)(?s)" +
				"(?:^|\\n)\\$\\$" +
				"(?:\\(\\((.+?)\\)\\))?\\n" +
				"(.+?)\\n" +
				"\\$\\$(?:\\n|$)"
			);

	public static final Pattern scriptModeFile = Pattern.compile("(?i)(?s)\\.(php[345]?|aspx?)$");

	public static final String xmlName = new String("[\u0041-\u005A] | [\u0061-\u007A] | [\u00C0-\u00D6] | [\u00D8-\u00F6] | [\u00F8-\u00FF] | [\u0100-\u0131] | [\u0134-\u013E] | [\u0141-\u0148] | [\u014A-\u017E] | [\u0180-\u01C3] | [\u01CD-\u01F0] | [\u01F4-\u01F5] | [\u01FA-\u0217] | [\u0250-\u02A8] | [\u02BB-\u02C1] | \u0386 | [\u0388-\u038A] | \u038C | [\u038E-\u03A1] | [\u03A3-\u03CE] | [\u03D0-\u03D6] | \u03DA | \u03DC | \u03DE | \u03E0 | [\u03E2-\u03F3] | [\u0401-\u040C] | [\u040E-\u044F] | [\u0451-\u045C] | [\u045E-\u0481] | [\u0490-\u04C4] | [\u04C7-\u04C8] | [\u04CB-\u04CC] | [\u04D0-\u04EB] | [\u04EE-\u04F5] | [\u04F8-\u04F9] | [\u0531-\u0556] | \u0559 | [\u0561-\u0586] | [\u05D0-\u05EA] | [\u05F0-\u05F2] | [\u0621-\u063A] | [\u0641-\u064A] | [\u0671-\u06B7] | [\u06BA-\u06BE] | [\u06C0-\u06CE] | [\u06D0-\u06D3] | \u06D5 | [\u06E5-\u06E6] | [\u0905-\u0939] | \u093D | [\u0958-\u0961] | [\u0985-\u098C] | [\u098F-\u0990] | [\u0993-\u09A8] | [\u09AA-\u09B0] | \u09B2 | [\u09B6-\u09B9] | [\u09DC-\u09DD] | [\u09DF-\u09E1] | [\u09F0-\u09F1] | [\u0A05-\u0A0A] | [\u0A0F-\u0A10] | [\u0A13-\u0A28] | [\u0A2A-\u0A30] | [\u0A32-\u0A33] | [\u0A35-\u0A36] | [\u0A38-\u0A39] | [\u0A59-\u0A5C] | \u0A5E | [\u0A72-\u0A74] | [\u0A85-\u0A8B] | \u0A8D | [\u0A8F-\u0A91] | [\u0A93-\u0AA8] | [\u0AAA-\u0AB0] | [\u0AB2-\u0AB3] | [\u0AB5-\u0AB9] | \u0ABD | \u0AE0 | [\u0B05-\u0B0C] | [\u0B0F-\u0B10] | [\u0B13-\u0B28] | [\u0B2A-\u0B30] | [\u0B32-\u0B33] | [\u0B36-\u0B39] | \u0B3D | [\u0B5C-\u0B5D] | [\u0B5F-\u0B61] | [\u0B85-\u0B8A] | [\u0B8E-\u0B90] | [\u0B92-\u0B95] | [\u0B99-\u0B9A] | \u0B9C | [\u0B9E-\u0B9F] | [\u0BA3-\u0BA4] | [\u0BA8-\u0BAA] | [\u0BAE-\u0BB5] | [\u0BB7-\u0BB9] | [\u0C05-\u0C0C] | [\u0C0E-\u0C10] | [\u0C12-\u0C28] | [\u0C2A-\u0C33] | [\u0C35-\u0C39] | [\u0C60-\u0C61] | [\u0C85-\u0C8C] | [\u0C8E-\u0C90] | [\u0C92-\u0CA8] | [\u0CAA-\u0CB3] | [\u0CB5-\u0CB9] | \u0CDE | [\u0CE0-\u0CE1] | [\u0D05-\u0D0C] | [\u0D0E-\u0D10] | [\u0D12-\u0D28] | [\u0D2A-\u0D39] | [\u0D60-\u0D61] | [\u0E01-\u0E2E] | \u0E30 | [\u0E32-\u0E33] | [\u0E40-\u0E45] | [\u0E81-\u0E82] | \u0E84 | [\u0E87-\u0E88] | \u0E8A | \u0E8D | [\u0E94-\u0E97] | [\u0E99-\u0E9F] | [\u0EA1-\u0EA3] | \u0EA5 | \u0EA7 | [\u0EAA-\u0EAB] | [\u0EAD-\u0EAE] | \u0EB0 | [\u0EB2-\u0EB3] | \u0EBD | [\u0EC0-\u0EC4] | [\u0F40-\u0F47] | [\u0F49-\u0F69] | [\u10A0-\u10C5] | [\u10D0-\u10F6] | \u1100 | [\u1102-\u1103] | [\u1105-\u1107] | \u1109 | [\u110B-\u110C] | [\u110E-\u1112] | \u113C | \u113E | \u1140 | \u114C | \u114E | \u1150 | [\u1154-\u1155] | \u1159 | [\u115F-\u1161] | \u1163 | \u1165 | \u1167 | \u1169 | [\u116D-\u116E] | [\u1172-\u1173] | \u1175 | \u119E | \u11A8 | \u11AB | [\u11AE-\u11AF] | [\u11B7-\u11B8] | \u11BA | [\u11BC-\u11C2] | \u11EB | \u11F0 | \u11F9 | [\u1E00-\u1E9B] | [\u1EA0-\u1EF9] | [\u1F00-\u1F15] | [\u1F18-\u1F1D] | [\u1F20-\u1F45] | [\u1F48-\u1F4D] | [\u1F50-\u1F57] | \u1F59 | \u1F5B | \u1F5D | [\u1F5F-\u1F7D] | [\u1F80-\u1FB4] | [\u1FB6-\u1FBC] | \u1FBE | [\u1FC2-\u1FC4] | [\u1FC6-\u1FCC] | [\u1FD0-\u1FD3] | [\u1FD6-\u1FDB] | [\u1FE0-\u1FEC] | [\u1FF2-\u1FF4] | [\u1FF6-\u1FFC] | \u2126 | [\u212A-\u212B] | \u212E | [\u2180-\u2182] | [\u3041-\u3094] | [\u30A1-\u30FA] | [\u3105-\u312C] | [\uAC00-\uD7A3] | 	[\u4E00-\u9FA5] | \u3007 | [\u3021-\u3029] | : | _ ");

	
	/**
	 * Pattern for simplified meta data.
	 */
	public static final Pattern argMetaSimple = Pattern.compile("([^=]+)=([^\"]+)");
	
	

	public static final class RELink {
		

		public static final Pattern linkExternalIndicator = Pattern.compile(
				"(?i)^(" + Constants.Links.supportedURIs + ")"
		);

		/** 
		 * <p>Pattern matching external links in wiki brackets: <code>[http://example.com my|arguments]</code></p>
		 * <p><ul><li>First grup contains the Link URI (<code>http://example.com</code>)</li>
		 * <li>Second group contains the link arguments (<code>my|arguments</code>)</li></ul></p> 
		 */
		public static final Pattern linkExternalWikitype = Pattern.compile(
				"(?i)(?x)(?<!\\[)\\[	# Link starts with [ and not with [[\n" +
				"(" +
				"(?:" + Constants.Links.supportedURIs + ")" +  
				"[^\\s\\]|]+			# Link may contain anything except spaces, ] or | \n" +
				")" +
				"[\\s|]?				# Possible separator, space or | \n" +
				"([^\\n\\]]*)			# Arguments, link text, etc \n" +
				"\\]"
				
		);
		
		/**
		 * <p>Pattern matching external links given directly by an URI: <code>http://example.com</code></p>
		 * <p><ul><li>Both group 1 and 2 contain the URI (<code>http://example.com</code>).</li></ul></p>
		 */
		public static final Pattern linkExternalUri = Pattern.compile(
				"(?i)(?x)(?<![\\[\\\">])	# Link starts with like http and not [ or \" \n" +
				"						# Forbidding \" is necessary because otherwise we'd match in HTML tags\n" +
				"((" +
				"(?:" + Constants.Links.supportedURIs + ")" +
				"[^\\s<>\\[\\]]+" +		
				"[^\\s,;.:?!\\(\\)<>']	# Do not include this chars at the end. \n" +
				"))"
		);
		
		/**
		 * <p>Pattern matching external links given directly by an URI: <code>www.example.com</code></p>
		 * <p><ul><li>Both group 1 and 2 contain the URI (<code>www.example.com</code>).</li></ul></p>
		 */
		public static final Pattern linkExternalUriShortened = Pattern.compile(
				"(?i)(?x)(?<![\\[\\\"/])	# Link starts with like http and not [ or \" or / (because of http://www.example.com) \n" +
				"						# Forbidding \" is necessary because otherwise we'd match in HTML tags\n" +
				"((" +
				"(?:" + Constants.Links.supportedURIsShortened + ")" +
				"[^\\s<>\\[\\]]+" +		
				"[^\\s,;.:?!\\(\\)<>']	# Do not include this chars at the end. \n" +
				"))"
		);
		
		/**
		 * <p>Pattern matching wikilinks without description (and thus without space or |): <code>[[link.html]]</code></p>
		 * <p><ul><li>Both group 1 and 2 contain the relative URI (<code>page.html</code>).</li></ul></p>
		 */
		public static final Pattern linkInternalShort = Pattern.compile(
				"(?i)(?x)\\[\\[" +
				"((" +
				"[^\\s\\]|]+" +
				"))" +
				"\\]\\]"
		);
		
		public static final Pattern linkInternalDescPipe = Pattern.compile(
				"(?i)(?x)\\[\\[" +
				"(" +
				"[^\\]|]+" +
				")" +
				"\\|" +
				"(" +
				"[^\\]]+" +
				")" +
				"\\]\\]"
		);
		
		public static final Pattern linkInternalDescSpace = Pattern.compile(
				"(?i)(?x)\\[\\[" +
				"(" +
				"[^\\s\\]|]+" +
				")" +
				"\\s+" +
				"(" +
				"[^\\]|]+" +
				")" +
				"\\]\\]"
		);
	}

}
