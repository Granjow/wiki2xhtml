package src.utilities.listcomprehension;

import java.util.*;
import src.utilities.listcomprehension.Func;
import src.utilities.listcomprehension.Filter;

public class ListComprehension {
	
	public static <T> void mapInPlace(List<T> in, Func<T, T> f) {
		ListIterator<T> it = in.listIterator();
		while (it.hasNext()) {
			it.set(f.apply(it.next()));
		}
	}
	
	public static <I, O> List<O> map(List<I> in, Func<I, O> f) {
		List<O> al = new ArrayList<O>();
		ListIterator<I> it = in.listIterator();
		while (it.hasNext()) {
			al.add(f.apply(it.next()));
		}
		return al;
	}
	
	public static <T> List<T> filter(List<T> in, Filter<T> f) {
		List<T> al = new ArrayList<T>();
		ListIterator<T> it = in.listIterator();
		while (it.hasNext()) {
			T item = it.next();
			if (f.test(item)) { al.add(item); }
		}
		return al;
	}
	
}
