package src;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;


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
 * Tries to allow undoing changes ...
 *
 * @author Simon Eugster
 * ,		hb9eia
 */
public class ExtendedUndoManager extends UndoManager {

	private static final long serialVersionUID = -6044948926181570350L;

	@Override
	protected UndoableEdit editToBeUndone() {
//		System.out.println(super.editToBeUndone());
		return super.editToBeUndone();
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
//		UndoableEdit ue = e.getEdit();
//		System.err.println(ue);
		super.undoableEditHappened(e);
	}

	@Override
	public synchronized boolean addEdit(UndoableEdit anEdit) {
//		System.err.println(anEdit);
		return super.addEdit(anEdit);
	}

	@Override
	protected void undoTo(UndoableEdit edit) throws CannotUndoException {
//		System.out.println(edit);
		super.undoTo(edit);
	}

}
