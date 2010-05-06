package src.utilities.listcomprehension;

import java.util.*;
import src.utilities.listcomprehension.Func;
import src.utilities.listcomprehension.Filter;

public class ListComprehension {
	
	public static <T> void applyInPlace(List<T> in, Func<T, T> f) {
		ListIterator<T> it = in.listIterator();
		while (it.hasNext()) {
			it.set(f.apply(it.next()));
		}
	}
	
	public static <In, Out> List<Out> map(List<In> in, Func<In, Out> f) {
		List<Out> al = new ArrayList<Out>();
		ListIterator<In> it = in.listIterator();
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
