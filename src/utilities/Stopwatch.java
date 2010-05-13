package src.utilities;

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
 */

/**
 *
 * A timer to measure ... time!
 *
 * @author Simon Eugster
 * ,		hb9eia
 */
public class Stopwatch {
	protected long startTime = 0, stopTime = 0;
	protected String purpose = "";

	public Stopwatch () {}
	public Stopwatch (String purpose) {
		this.purpose = purpose;
	}

	protected long getTime() {
		return System.currentTimeMillis();
	}
	protected String getTimeString(long time) {
		return StringTools.formatTimeMilliseconds(time);
	}


	/** Starts the stop watch */
	public void start () {
		startTime = getTime();
	}
	/** Stops the stop watch */
	public long stop() {
		stopTime = getTime();
		long sT = stopTime - startTime;
		return sT;
	}
	/** Continues measuring the time */
	public void continueTime() {
		long diff = getTime() - stopTime;
		if (startTime == 0)
			startTime = getTime();
		else
			startTime += diff;
	}


	/**
	 * @return The passed time since start
	 */
	public long getPassedTime() {
		long pT = getTime() - startTime;
		return pT;
	}
	/**
	 * @return The passed time since start, as String
	 */
	public String getPassedTimeString() {
		return getTimeString(getPassedTime());
	}
	/**
	 * @return The passed time between start and stop
	 */
	public long getStoppedTime() {
		return (stopTime - startTime);
	}
	/**
	 * @return The passed time between start and stop, as String
	 */
	public String getStoppedTimeString() {
		return getTimeString(getStoppedTime());
	}
	/** Resets the time */
	public void reset() {
		startTime = 0;
		stopTime = 0;
	}

	/** @param purpose The purpose of this timer */
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	/**  @return The purpose of this timer */
	public String getPurpose() {
		return purpose;
	}
}
