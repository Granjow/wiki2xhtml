package src.tasks;

import src.project.file.WikiFile;

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
 *
 */

public abstract class WikiTask {
	
	/** Parses the content of the file given by its ID. */
	abstract public void parse(WikiFile file);
	
	/** null if no next task. */
	abstract public WikiTask nextTask();
	
	/** Kind of a task ID. */
	abstract public Tasks.Task desc();

}
