package src.utilities;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Resources;

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
 * XML Tools
 *
 * @author Simon Eugster
 */
public final class XMLTools {


	/**
	 * Extracts all NameChars defined for XML 1.0
	 * @param s
	 * @return Valid NameChar sequence
	 */
	public static final String getXmlNameChar(final String s) {

		StringBuilder out = new StringBuilder();

		Matcher mNameChar = Resources.XmlNames.patternNameChar.matcher(s);

		while (mNameChar.find()) {
			out.append(mNameChar.group());
		}

		return out.toString();
	}


	/**
	 * Extracts all NameChars defined for XML 1.0
	 * @param s
	 * @return Valid NameChar sequence
	 */
	public static final String getXmlNameChar(final String s, final String extension) {

		if (extension.length() == 0)
			return getXmlNameChar(s);

		StringBuilder out = new StringBuilder();

		Matcher mNameChar = Pattern.compile("(?x)(" + Resources.XmlNames.nameChar + " | [" + extension + "])+").matcher(s);

		while (mNameChar.find()) {
			out.append(mNameChar.group());
		}

		return out.toString();
	}
	
	/**
	 * <p>Not testing for XML correctness! Simply merges equal arguments.</p>
	 * <p><strong>Example:</strong> Input is<br />
	 * <code>class="a" style="width: 10px;" class="b"</code><br />
	 * Output is<br />
	 * <code>class="a b" style="width: 10px;"</code><br />
	 * (for <code>valueSeparator = " "</code> and <code>argumentSeparator = " "</code>.)
	 * </p>
	 * @param args
	 * @return
	 */
	public static final String mergeArguments(final String args, final String valueSeparator, 
			final String argumentSeparator, final boolean acceptSingleArgs) {
		
		HashMap<String, String> argumentsMap = new HashMap<String, String>();
		StringBuilder arguments = new StringBuilder();
		
		String arg;
		
		Pattern keyValue = Pattern.compile("(?:^|\\s)([^\\\"\\s]+)=\"([^\\\"]+)\"");
		Matcher matcherKV = keyValue.matcher(args);
		int last = 0;
		while (matcherKV.find()) {
			arguments.append(args.subSequence(last, matcherKV.start()));
			last = matcherKV.end();
			
			if (argumentsMap.containsKey(matcherKV.group(1))) {
				// Already there: Append value
				arg = argumentsMap.get(matcherKV.group(1)) + valueSeparator + matcherKV.group(2);
				argumentsMap.put(matcherKV.group(1), arg);
			} else {
				argumentsMap.put(matcherKV.group(1), matcherKV.group(2));
			}
		}
		if (argumentsMap.size() == 0) return args;
		
		arguments.append(args.substring(last));
		
		if (!acceptSingleArgs) {
			// Discard collected and unprocessed arguments
			arguments = new StringBuilder();
		}
		
		for (Entry<String, String> m : argumentsMap.entrySet()) {
			arguments.append(argumentSeparator + m.getKey() + "=\"" + m.getValue() + "\"");
		}
		
		return arguments.toString();
	}

}
