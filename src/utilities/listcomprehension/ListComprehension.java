package src.utilities.listcomprehension;

import java.lang.reflect.Array;
import java.util.*;
import src.utilities.listcomprehension.Func;
import src.utilities.listcomprehension.Filter;

public class ListComprehension {
	
	////////// Map in-place /////////
	public static <T> void mapInPlace(List<T> in, Func<T, T> f) {
		ListIterator<T> it = in.listIterator();
		while (it.hasNext()) {
			it.set(f.apply(it.next()));
		}
	}
	public static <T> void mapInPlace(T[] in, Func<T, T> f) {
		for (int i = 0; i < in.length; i++) {
			in[i] = f.apply(in[i]);
		}
	}
	
	
	////////// Map //////////
	public static <I, O> List<O> map(List<I> in, Func<I, O> f) {
		List<O> al = new ArrayList<O>();
		ListIterator<I> it = in.listIterator();
		while (it.hasNext()) {
			al.add(f.apply(it.next()));
		}
		return al;
	}
	@SuppressWarnings("unchecked")
	public static <I, O> O[] map(I[] in, Func<I, O> f, Class<O> type) {
		// Generic array creation is not possible in Java 1.6
		O[] out = (O[]) Array.newInstance(type, in.length);	
		for (int i = 0; i < in.length; i++) {
			out[i] = f.apply(in[i]);
		}
		return (O[]) out;
	}
	
	/////// Filter ///////
	public static <T> List<T> filter(List<T> in, Filter<T> f) {
		List<T> al = new ArrayList<T>();
		ListIterator<T> it = in.listIterator();
		while (it.hasNext()) {
			T item = it.next();
			if (f.test(item)) { al.add(item); }
		}
		return al;
	}
	@SuppressWarnings("unchecked")
	public static <T> T[] filter(T[] in, Filter<T> f, Class<T> type) {
		List<T> al = new ArrayList<T>();
		for (int i = 0; i < in.length; i++) {
			if (f.test(in[i])) { al.add(in[i]); }
		}
		T[] arr = (T[]) Array.newInstance(type, al.size());
		al.toArray(arr);
		return arr;
	}
	
}
