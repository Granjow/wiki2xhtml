package src.commentator;

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
 * TODO 5 everything: write support, logging, ...
 *
 * @author Simon Eugster
 */
public class Logger {

	private Logger() { }
	private static Logger logger = new Logger();

	public static Logger getInstance() {
		return logger;
	}

	public StringBuffer log = new StringBuffer();

}