package src.commentator;

import java.util.ArrayList;

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
 * This class handles comments. Supports logging.
 * TODO 5 output log in GUI
 *
 * @author Simon Eugster
 */
public class CommentAtor {
	
	public enum CALevel {
		
		/** Errors */			ERRORS (1),		// 1
		/** Normal messages */	MSG (1 << 1),	// 2
		/** Verbose only */		V_MSG (1 << 2),	// 4
		/** Debug only */		DEBUG (1 << 3),	// 8
		/** Everywhere */		ANY (1 << 4),	// 16
		/** MSG or V_MSG */		C_MSGS (MSG.or(V_MSG));
		
		private final int id;
		CALevel(int id) {
			this.id = id;
		}
		public int v() { return id; }
		public int or(CALevel other) { return v() | other.v(); }
		public int or(int other) { return v() | other; }
		public int and(CALevel other) { return v() & other.v(); }
		public int and(int other) { return v() & other; }
	}

	public int flags = CALevel.ERRORS.or(CALevel.ANY);
	private int oldFlags = 0;	// Keep status

	private CommentAtor() {
		flags = CALevel.ERRORS.or(CALevel.MSG.or(CALevel.ANY));
	}
	private static CommentAtor cat = new CommentAtor();

	public static CommentAtor getInstance() {
		return cat;
	}

	public CommentLog log = new CommentLog();

	/**
	 * Prints an object to stdout
	 * @param level Importance
	 */
	public final void o(Object s, CALevel level) {
		if ((flags & level.v()) != 0) {
			if ((level.v() & CALevel.ERRORS.v()) != 0)
				System.err.print(s);
			else
				System.out.print(s);
			log.add(s, level.v());
		}
	}

	/**
	 * Prints an object line to stdout
	 * @param level Importance
	 */
	public final void ol(Object s, CALevel level) {
		if ((flags & level.v()) != 0) {
			if (level.and(CALevel.ERRORS) != 0)
				System.err.println(s);
			else
				System.out.println(s);
			log.add(s, level.v());
		}
	}


	/**
	 * printf version. See {@link java.util.Formatter}.
	 */
	public final void ol(String s, CALevel level, Object... args) {
		if ((flags & level.v()) != 0) {
			if (level.and(CALevel.ERRORS) != 0)
				System.err.printf(s, args);
			else
				System.out.printf(s, args);
			log.add(s, level.v());
		}
	}

	public void setDebug() {
		flags = CALevel.DEBUG.or(flags);
	}
	public void setDead(boolean dead) {
		if (dead) {
			if (flags != 0) oldFlags = flags;
			flags = 0;
		} else {	// Restore previous status
			flags = oldFlags;
		}
	}
	public void setSilent() {
		flags &= CALevel.ANY.or(CALevel.ERRORS.or(CALevel.DEBUG));
	}
	public void setVerbose() {
		flags |= CALevel.MSG.or(CALevel.V_MSG.or(CALevel.DEBUG));
	}

	/**
	 * Comment logger
	 */
	public static class CommentLog {

		public CommentLog() { }

		private ArrayList<CommentLogItem> log = new ArrayList<CommentLogItem>();

		public void add(Object s, int importance) {
			log.add(new CommentLogItem(s, importance));
		}

		public StringBuffer getLogByImportance(byte importance) {
			StringBuffer out = new StringBuffer();

			for (CommentLogItem cli : log) {
				if ((cli.importance & importance) > 0) {
					out.append(cli.s.toString());
				}
			}

			return out;
		}

	}

	/**
	 * Comment item
	 */
	public static class CommentLogItem {

		int importance = 0;
		Object s = null;

		public CommentLogItem() { }

		public CommentLogItem(Object s, int importance) {
			this.importance = importance;
		}
	}

}
