package src.utilities;

public abstract class Tuple<K, V> {
	private final K key;
	private final V val;
	public Tuple(K key, V val) {
		this.key = key;
		this.val = val;
	}
	public V v() { return val; }
	public K k() { return key; }
}
