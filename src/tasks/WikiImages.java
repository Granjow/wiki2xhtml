package src.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;

import src.Container_Files;
import src.Statistics;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.project.file.WikiFile;
import src.resources.RegExpressions;
import src.tasks.Tasks.Task;


public class WikiImages extends WikiTask {

	public Task desc() {
		return Task.Images;
	}
	public WikiTask nextTask() {
		return new WikiLinks();
	}
	
	/**
	 * Make the [[Image:xx]] to &lt;img src="xx" /&gt; tags.
	 * @since August 2008: rewritten
	 */
	public void parse(WikiFile file) {
		Statistics.getInstance().sw.timeInsertingImages.continueTime();

		StringBuffer in = file.getContent();
		StringBuffer out = new StringBuffer();
		short counter = 0;

		Matcher m;

		m = RegExpressions.images.matcher(in);
		int last = 0;

		while (m.find()) {
			out.append(in.substring(last, m.start()));
			Statistics.getInstance().counter.imagesTotal.increase();

			// Read the image's arguments
			is.nextItem();
			is.image.readArguments(m.group(1), false);
			is.image.set_(SettingsImgE.galleryEnabled, is.image.nullValue());

			// Generate an image page if they are to be generated, a isThumbDesired has to be inserted and it's no direct link.
			if (is.image.contains(SettingsImgE.thumbEnabled) && !is.image.contains(SettingsImgE.linkDirect)) {
				generateImagepage();
			}

			try {
				out.append(generateThumbnailEntry());
			} catch (FileNotFoundException e) {
				CommentAtor.getInstance().ol("A file could not be found. Don't know why. Error message: \n" + e.getMessage(), CALevel.MSG);
			} catch (IOException e) {
				CommentAtor.getInstance().ol("An IO Error occurred. Don't know why. Error message: \n" + e.getMessage(), CALevel.MSG);
			}


			last = m.end();
		}
		out.append(in.substring(last, in.length()));

		/* Statistics */
		Statistics.getInstance().counter.imagesTotal.increase(counter);
		Statistics.getInstance().sw.timeInsertingImages.stop();

		return out;
	}
	
}
