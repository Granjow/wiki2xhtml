package src.utilities.listcomprehension;

import java.util.*;

public class Main {
	
	public static void main(String[] args) {
		Func<Integer,Integer> f = new Twice();
		Filter<Integer> fi = new Filter<Integer>() {
			public boolean test(Integer i) {
				return i % 3 == 0;
			}
		};
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			al.add(i);
		}
		for (Integer i : ListComprehension.map(al, f)) {
			System.out.println(i);
		}
		for (Integer i : ListComprehension.filter(al, fi)) {
			System.out.println(i);
		}
	}
	
	private static class Twice implements Func<Integer, Integer> {
		public Integer apply(Integer i) { return i*2; }
	}
	
}
