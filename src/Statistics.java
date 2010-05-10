package src;

import java.util.Vector;

import src.utilities.StopwatchNano;

/*
 *   Copyright (C) 2007-2010 Simon Eugster <granjow@users.sf.net>

 *   This program is free software: you can redistribute it and/or modify
 *   it under Terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

/**
 * Collects statistics about everything.
 *
 * @author Simon Eugster
 */
public class Statistics {

	private Statistics() { }
	private static Statistics statistics = new Statistics();

	public static Statistics getInstance() {
		return statistics;
	}

	public Counter counter = new Counter();
	public Stopwatches sw = new Stopwatches();
	private Vector<StopwatchNano> allWatches = new Vector<StopwatchNano>();

	/** Contains generated statistics */
	StringBuffer stats = new StringBuffer();

	/** Generates statistics */
	public StringBuffer generateStats() {
		stats = new StringBuffer();
		createStopwatchList();
		for (StopwatchNano sws : allWatches) {
			stats.append(sws.getPurpose() + ": " + sws.getStoppedTimeString() + '\n');
		}
		return stats;
	}

	/** Resets the statistics */
	public void reset() {
		counter = new Counter();
		sw = new Stopwatches();
	}

	public static class Counter {
		public CounterItem italicType = new CounterItem("Italic type");
		public CounterItem boldType = new CounterItem("Bold type");
		public CounterItem imagesTotal = new CounterItem("Images in total");
		public CounterItem imagesGallery = new CounterItem("Images in galleries");
		public CounterItem galleries = new CounterItem("Galleries");
		public CounterItem linksExt = new CounterItem("External links");
		public CounterItem linksInt = new CounterItem("Internal links");
		public CounterItem headings = new CounterItem("Headings");
		public CounterItem horizontalLines = new CounterItem("Horizontal lines inserted");
		public CounterItem TOC = new CounterItem("Tables of Content");
		public CounterItem nowikiRemoved = new CounterItem("Nowiki tags removed");
		public CounterItem nowikiInserted = new CounterItem("Nowiki tags inserted");
		public CounterItem paragraphsOpened = new CounterItem("Paragraphs");
		public CounterItem references = new CounterItem("References");
		public CounterItem tablesOpened = new CounterItem("Tables opened");
		public CounterItem tablesClosed = new CounterItem("Tables closed");
		public CounterItem templates = new CounterItem("Templates inserted");
		public CounterItem cr = new CounterItem("Carriage returns found");
		public CounterItem lists = new CounterItem("Lists");
		public CounterItem listItems = new CounterItem("List items");
		public CounterItem functions = new CounterItem("Functions");
	}

	public static class Stopwatches {
		public StopwatchNano timeSortingKeywords = new StopwatchNano("Time spent on sorting the keywords and removing duplicates");
		public StopwatchNano timeInitializingProcess = new StopwatchNano("Time spent on initializing all files");
		public StopwatchNano timeOverall = new StopwatchNano("Overall time needed for generating documents");
		public StopwatchNano timeReadingFiles = new StopwatchNano("Time spent on reading files");
		public StopwatchNano timeWritingFiles = new StopwatchNano("Time spent on writing files");
		public StopwatchNano timeReadingLocalSettings = new StopwatchNano("Time spent on reading local settings");	// 32-70 ms (3.2.1), 25-50 ms (3.3)
		public StopwatchNano timeApplyingTemplates = new StopwatchNano("Time spent on applying templates");
		public StopwatchNano timeCreatingLists = new StopwatchNano("Time spent on creating lists");
		public StopwatchNano timeMakingHeadings = new StopwatchNano("Time spent on making the headings");	// 140 ms (3.2.1) -> 112 ms (3.3)
		public StopwatchNano timeInsertingImages = new StopwatchNano("Time spent on inserting images and galleries");	// 110-140 ms (3.2.1) -> 170 ms (3.3)
		public StopwatchNano timeInsertingParagraphs = new StopwatchNano("Time spent on inserting paragraphs");
		public StopwatchNano timeFormattingCode = new StopwatchNano("Time spent on formatting $$code$$, --removed text-- etc.");
		public StopwatchNano timeFunctionParser = new StopwatchNano("Time spent on parsing functions");

		public StopwatchNano temp = new StopwatchNano("Temp Stopwatch");
		public StopwatchNano temp2 = new StopwatchNano("Second temp stopwatch");
	}

	private void createStopwatchList() {
		allWatches.setSize(0);
		allWatches.add(sw.timeInitializingProcess);
		allWatches.add(sw.timeSortingKeywords);
		allWatches.add(sw.timeReadingFiles);
		allWatches.add(sw.timeReadingLocalSettings);
		allWatches.add(sw.timeWritingFiles);
		allWatches.add(sw.timeCreatingLists);
		allWatches.add(sw.timeMakingHeadings);
		allWatches.add(sw.timeApplyingTemplates);
		allWatches.add(sw.timeInsertingImages);
		allWatches.add(sw.timeInsertingParagraphs);
		allWatches.add(sw.timeFunctionParser);
		allWatches.add(sw.timeFormattingCode);
		allWatches.add(sw.timeOverall);
	}

	public static class CounterItem {
		private long number = 0;
		String description = "";
		CounterItem() { }
		CounterItem(String description) {
			this.description = description;
		}

		public void increase() {
			number++;
		}

		public void increase(short s) {
			number += s;
		}

		public long getCounter() {
			return number;
		}

		public String getCounterDesc() {
			return description + " : " + number + " times";
		}
	}

}
