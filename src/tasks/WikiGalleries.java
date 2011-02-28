/*
 *   Copyright (C) 2010-2011 Simon A. Eugster <simon.eu@gmail.com>

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

package src.tasks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import src.Statistics;
import src.project.WikiProject;
import src.project.file.VirtualWikiFile;
import src.project.file.WikiFile;
import src.project.settings.GalleryProperties;
import src.project.settings.ImageProperties;
import src.resources.ResProjectSettings.EImageContext;
import src.tasks.Tasks.Task;

public class WikiGalleries extends WikiTask {

	public Task desc() {
		// TODO check: Galleries before lists?
		return Task.Galleries;
	}
	public WikiTask nextTask() {
		return new WikiFormattings();
	}
	/**
	 * <p>Builds all galleries.</p>
	 * <ul>
	 * <li>Image entries start with <code>Image:</code>.</li>
	 * <li>Text entries start with text only</li>
	 * <li>Comments start with <code>//</code> (will not be shown on the page)</li>
	 * </ul>
	 */
	public void parse(WikiFile file) {

		Statistics.getInstance().sw.timeInsertingImages.continueTime();

		BufferedReader b = new BufferedReader(new StringReader(file.getContent().toString()));
		StringBuffer out = new StringBuffer();
		StringBuffer cache = new StringBuffer();

		GalleryProperties gp = null;

		byte counter = 0;

		try {
			String line;
			boolean open = false;

			for (line = b.readLine(); line != null; line = b.readLine()) {

				if (open) {
					if (line.startsWith("</gallery>")) {

						open = false;
						out.append(gp.placeholder());

					} else {

						cache.append(line + '\n');
						
						if (line.startsWith("//")) {
							// Comment. Do not add image.
							continue;
						}

						// Add a new item
						ImageProperties prop = new ImageProperties(file, gp);
						prop.setContext(EImageContext.gallery);
						file.addImageProperties(prop);
						gp.imagePropertiesList.add(prop);
						prop.readArguments(line);
					}
				} else {
					if (line.startsWith("<gallery ") || line.startsWith("<gallery>")) {
						// add gallery start
						open = true;
						gp = new GalleryProperties(file);
						file.addGalleryProperties(gp);
						
						gp.readArguments(line);
						
						// Cache all lines following the gallery tag in case it is not closed correctly.
						cache = new StringBuffer();
						cache.append(line + '\n');
						
					} else {
						// add line
						out.append(line + '\n');
					}
				}

			}

			if (open) {
				// Unclosed gallery tag. Add all text without processing.
				file.galleryPropertiesList.remove(gp);
				out.append(cache);
				System.err.println("Gallery was not closed in " + file.internalName());
			}

		} catch (IOException e) {
			e.printStackTrace();
//			CommentAtor.getInstance().ol(
//				String.format("IO Exception while trying to read content in file %s!", Container_Files.getInstance().currentFilename),
//				CALevel.ERRORS);
			return;
		}
		gp = null;
		
		// Insert the galleries by replacing their placeholders
		int last = 0;
		for (GalleryProperties gap : file.galleryPropertiesList) {
			final String placeholder = gap.placeholder();
			last = out.indexOf(placeholder);
			if (last >= 0) {
				try {
					out = out.replace(last, last+placeholder.length(), gap.generateGallery());
					counter++;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		

		/* Statistics */
		Statistics.getInstance().counter.galleries.increase(counter);
		Statistics.getInstance().sw.timeInsertingImages.stop();

		file.setContent(out);
	}
	
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("<gallery>\nImage:test.jpg\n</gallery>");
		WikiProject wp = new WikiProject(".");
		VirtualWikiFile vf = new VirtualWikiFile(wp, "name", false, true, sb);
		vf.removeAllTasks();
		vf.addTask(Task.Galleries);
		vf.parse();
		System.out.println(vf.getContent());
	}
	

}
