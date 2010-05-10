package src.guiWindows;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.xnap.commons.i18n.*;

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
 * Miscellaneous windows which don't suit anywhere else
 *
 * @author Simon Eugster
 * ,		hb9eia
 */
public final class MiscWindows {

	static I18n i18n = I18nFactory.getI18n(MiscWindows.class, "bin.l10n.Messages", src.Globals.getLocale());

	public static File getBaseStyleDir() {

		String[] options = {i18n.tr("Yes"), i18n.tr("No")};

		if (JOptionPane.showOptionDialog(null, i18n.tr("Directory \u201cstyle\u201d not found. Do you want to select an other directory?"),
										 i18n.tr("Select different style directory"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
										 options, options[0]) == JOptionPane.YES_OPTION) {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfc.showOpenDialog(null);

			return jfc.getSelectedFile();
		}

		return null;
	}
}
