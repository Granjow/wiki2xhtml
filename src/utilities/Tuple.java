package src.utilities;

public abstract class Tuple<K, V> {
	protected K key;
	protected V val;
	public Tuple() {}
	public Tuple(K key, V val) {
		this.key = key;
		this.val = val;
	}
	public V v() { return val; }
	public K k() { return key; }
	public void setKey(K key) { this.key = key; }
	public void setValue(V val) { this.val = val; }
}
