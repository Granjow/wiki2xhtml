package src.utilities;

import java.io.File;
import java.io.IOException;

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
 * Provides some sorting algorithms
 *
 * @author Simon Eugster
 * ,		hb9eia
 */
public class Sort {

	/**
	 * Shellsort implementation.
	 * @param <T>
	 * @param array
	 */
	public static <T extends Comparable<? super T>> void shellsort(T[] array) {
		if (array == null || array.length == 0)
			return;

		shellsort(array, 0, array.length - 1);
	}

	/**
	 * Quicksort implementation.
	 * @param <T>
	 * @param array
	 */
	public static <T extends Comparable<? super T>> void quicksort(T[] array) {
		quicksort(array, 0, array.length - 1);
	}

	/**
	 * Simplesort, slow sorting algorithm
	 * @param <T>
	 * @param array
	 */
	public static <T extends Comparable<? super T>> void simplesort(T[] array) {
		simplesort(array, 0, array.length);
	}

	/**
	 * @param <T>
	 * @param t1
	 * @param t2
	 * @return true if t1 < t2
	 */
	private static <T extends Comparable<? super T>> boolean less(T t1, T t2) {
		return t1.compareTo(t2) < 0;
	}
	/**
	 * Swaps elements in field at position i and j
	 * @param <T>
	 * @param field
	 * @param i
	 * @param j
	 */
	private static <T extends Comparable<? super T>> void exch(T[] field, int i, int j) {
		T temp = field[i];
		field[i] = field[j];
		field[j] = temp;
	}
	/**
	 * Swaps elements in field at position i and j if j < i
	 * @param <T>
	 * @param field
	 * @param i
	 * @param j
	 */
	private static <T extends Comparable<? super T>> void compExch (T[] field, int i, int j) {
		if (less(field[j], field[i]))
			exch(field, i, j);
	}

	/**
	 * Simplesort implementation. Slow.
	 * @param <T>
	 * @param field
	 * @param begin
	 * @param end
	 */
	private static <T extends Comparable<? super T>> void simplesort (T[] field, int begin, int end) {
		for (int i = begin + 1; i <= end; i++) {
			for (int j = i; j > begin; j--)
				compExch(field, j-1, j);
		}
	}

	/**
	 * Shellsort implementation
	 * @param field Array to sort
	 * @param l Left limitation
	 * @param r Right limitation
	 */
	private static <T extends Comparable<? super T>> void shellsort (T[] field, int l, int r) {
		int h;
		for (h = 1; h <= (r-l)/9; h = 3*h+1) ;
		for (; h > 0; h /= 3) {
			for (int i = l+h; i <= r; i++) {
				int j = i;
				T t = field[i];
				while (j >= l+h && less(t, field[j-h])) {
					field[j] = field[j-h];
					j -= h;
				}
				field[j] = t;
			}
		}
	}

	/**
	 * Quicksort
	 * @param s Array to sort
	 * @param l left position
	 * @param r right position
	 */
	private static <T extends Comparable<? super T>> void quicksort(T[] t, int l, int r) {
		if (r <= l) return;
		int i = partition(t, l, r);
		quicksort(t, l, i-1);
		quicksort(t, i+1, r);
	}

	/**
	 * Used for Quicksort
	 * @param t
	 * @param l
	 * @param r
	 * @return
	 */
	private static <T extends Comparable<? super T>> int partition(T[] t, int l, int r) {
		int i = l-1;
		int j = r;
		T tt = t[r];

		for (;;) {
			while (less(t[++i], tt))
				;
			while (less(tt, t[--j]))
				if (j == l)
					break;
			if (i >= j) break;
			exch(t, i, j);
		}
		exch(t, i, r);
		return i;
	}

	/**
	 * Bench for Quicksort and Shellsort
	 * @param n
	 */
	public static void bench(int n) {
		Long[] d = new Long[n];
		for (int i = 0; i < n; i++) {
			d[i] = (long) (Math.random() * n);
		}
		Long[] d2 = d.clone();
		StopwatchNano tn = new StopwatchNano();

		tn.start();
		shellsort(d);
		tn.stop();
		System.err.println("Shellsort: " + tn.getStoppedTimeString());
		tn.reset();

		tn.start();
		quicksort(d2);
		tn.stop();
		System.err.println("Quicksort: " + tn.getStoppedTimeString());

		StringBuffer sb = new StringBuffer();
		for (long i : d)
			sb.append(i + " ");
		sb.append("\n");
		for (long i : d2)
			sb.append(i + " ");

		try {
			IOWrite.writeString(new File("sort"), sb.toString(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
