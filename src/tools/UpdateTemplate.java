package src.tools;

import java.io.File;
import java.io.IOException;

import src.ConstantTexts;
import src.Constants;
import src.commentator.CommentAtor;
import src.commentator.Logger;
import src.commentator.CommentAtor.CALevel;
import src.utilities.IORead_Stats;
import src.utilities.IOUtils;
import src.utilities.IOWrite_Stats;

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
 * Converts an old template file into a «new» one
 *
 * @since wiki2xhtml 3.3
 *
 * @author Simon Eugster
 */
public class UpdateTemplate {


	File menu = null;

	/**
	 * @param file The old menu file to update
	 */
	public static boolean run(String file, boolean exitOnClose) {
		File f;

		/*
		 * Get file
		 */
		if (file == null || file.length() == 0) {
			f = IOUtils.openInFileDialog("", new File(Constants.Directories.workingDir), ConstantTexts.NounCap.templateFile, false);
			if (f == null) {
				if (exitOnClose)
					System.exit(0);
				return false;
			}
		} else
			f = new File(file);

		/*
		 * Check permissions
		 */
		{
			boolean e, i = false, r = false, w = false;
			if (!(e = f.exists()) | !(i = f.isFile()) | !(r = f.canRead()) | !(w = f.canWrite())) {
				System.err.println("Error. exists: " + e + ", is file: " + i + "; read: " + r + "; write: " + w);
				System.exit(1);
			}
		}

		try {

			String out = IORead_Stats.readSBuffer(f).toString();

			out = out.replaceAll("(?i)\\[back\\]", Constants.TemplateTags.back);
			out = out.replaceAll("(?i)\\[caption\\]", Constants.TemplateTags.caption);
			out = out.replaceAll("(?i)\\[content\\]", Constants.TemplateTags.content);
			out = out.replaceAll("(?i)\\[desc\\]", Constants.TemplateTags.desc);
			out = out.replaceAll("(?i)\\[footer\\]", Constants.TemplateTags.footer);
			out = out.replaceAll("(?i)\\[h1\\]", Constants.TemplateTags.h1);
			out = out.replaceAll("(?i)\\[head\\]", Constants.TemplateTags.head);
			out = out.replaceAll("(?i)\\[homelink\\]", Constants.TemplateTags.homelink);
			out = out.replaceAll("(?i)\\[id\\]", Constants.TemplateTags.id);
			out = out.replaceAll("(?i)\\[imagecaption\\]", Constants.TemplateTags.imageCaption);
			out = out.replaceAll("(?i)\\[imagename\\]", Constants.TemplateTags.imageName);
			out = out.replaceAll("(?i)\\[imagepath\\]", Constants.TemplateTags.imagePath);
			out = out.replaceAll("(?i)\\[link\\]", Constants.TemplateTags.link);
			out = out.replaceAll("(?i)\\[menu\\]", Constants.TemplateTags.menu);
			out = out.replaceAll("(?i)\\[meta\\]", Constants.TemplateTags.meta);
			out = out.replaceAll("(?i)\\[nextimage\\]", Constants.TemplateTags.nextImage);
			out = out.replaceAll("(?i)\\[pos\\]", Constants.TemplateTags.pos);
			out = out.replaceAll("(?i)\\[pre\\]", Constants.TemplateTags.pre);
			out = out.replaceAll("(?i)\\[previmage\\]", Constants.TemplateTags.prevImage);
			out = out.replaceAll("(?i)\\[textheader\\]", Constants.TemplateTags.textheader);
			out = out.replaceAll("(?i)\\[thumb\\]", Constants.TemplateTags.thumb);
			out = out.replaceAll("(?i)\\[title\\]", Constants.TemplateTags.title);
			out = out.replaceAll("(?i)\\[width\\]", Constants.TemplateTags.width);


			/* Create backup */
			File updatedFile = new File(f.getPath() + "~old");
			IOUtils.copy(f, updatedFile);

			try {
				IOWrite_Stats.writeString(f, out, false);
				CommentAtor.getInstance().ol("Successfully written. Backup is " + updatedFile.getPath(), CALevel.MSG);

			} catch (IOException e) {
				Logger.getInstance().log.append(e);
				e.printStackTrace();
			}
		} catch (IOException e1) {
			Logger.getInstance().log.append(e1);
			e1.printStackTrace();
			return false;
		}

		return true;

	}
}
