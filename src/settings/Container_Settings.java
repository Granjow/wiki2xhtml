package src.settings;


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
 * A container for some settings.
 *
 * @author Simon Eugster
 */
public class Container_Settings {

	public boolean onlyCode = false;
	public boolean stdOut = false;
	public boolean removeLineBreaks = false;
	public boolean gui = false;
	public boolean noUpdateCheck = false;
	public boolean incremental = false;

	public Container_Settings() { }
}
