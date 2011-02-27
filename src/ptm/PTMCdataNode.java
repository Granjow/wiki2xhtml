/*
 *   Copyright (C) 2011 Simon Eugster <granjow@users.sf.net>

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

package src.ptm;

/**
 * <p>Handles CDATA sections.<p>
 * <p>CDATA sections may contain any kind of text: It will not be evaluated. This is very useful for passing
 * special characters like a pipe symbol as an argument.</p>
 * <p>Example: {@code {{:template|This <![CDATA[|]]> is a pipe symbol passed to the template.}}</p>
 * <p>Note that <em>nesting</em> is not possible for CDATA sections.
 * @see <a href="http://en.wikipedia.org/wiki/CDATA">Wikipedia: CDATA</a> (also about nesting workarounds)
 */
public class PTMCdataNode extends PTMNode {
	

	public static final String startString = "<![CDATA[";
	public static final String endString = "]]>";

	/**
	 * <p>Tries to create a CDATA node at the given position. This is much easier than for other nodes 
	 * since no nesting is allowed here.</p>
	 */
	public PTMCdataNode(StringBuffer content, int beginIndex, PTMNode parent, PTMRootNode root) throws ObjectNotApplicableException {
		super(content, beginIndex, parent, root);
		
		try {
			if (startString.equals(content.substring(beginIndex, beginIndex + startString.length()))) {
				endIndex = beginIndex + startString.length();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			throw new ObjectNotApplicableException("No CDATA: Does not start with <![CDATA[.");
		}
		
		int index;
		if ((index = content.indexOf(endString, endIndex)) >= endIndex) {
			endIndex = index + endString.length();
		} else {
			throw new ObjectNotApplicableException("No closing ]]> found for the CDATA section.");
		}

		assert endIndex > this.beginIndex;
	}
	
	public String evaluate() throws RecursionException {
		return content.substring(beginIndex + startString.length(), endIndex - endString.length());
	}

}
