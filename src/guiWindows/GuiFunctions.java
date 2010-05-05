package src.guiWindows;

import java.io.*;

import javax.swing.JTextField;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.Container_Files;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;

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
 * This is a class.
 *
 * @author Simon Eugster
 */
public final class GuiFunctions {

	private static final I18n i18n = I18nFactory.getI18n(GuiFunctions.class, "bin.l10n.Messages", src.Globals.getLocale());
	private static final CommentAtor ca = CommentAtor.getInstance();

	private static final String className = "GuiFunctions";


	public static void targetDir(JTextField jtfTargetDir) {

		if (Container_Files.getInstance().setTargetDirectory(jtfTargetDir.getText())) {
			if (new File(jtfTargetDir.getText()).exists())
				ca.ol(className
					  + i18n.tr("(Existing) Target directory set: ")
					  + Container_Files.getInstance().cont.targetDir().getPath(), CALevel.V_MSG);
			else
				ca.ol(className
					  + i18n.tr("(New) Target directory set: ")
					  + Container_Files.getInstance().cont.targetDir().getPath(), CALevel.V_MSG);
		} else
			System.out.println(className + "Strange ...");
	}

}
