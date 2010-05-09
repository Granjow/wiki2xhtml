package src.project.settings;

public abstract class SettingReader<K extends Comparable<?>, V extends Comparable<?>> {
	
	abstract public boolean read(Settings<K, V> settings, K key, StringBuffer in, boolean remove); 

}
