package src.project.settings;

import java.util.Map.Entry;

import src.utilities.Base64;

abstract public class StringSettings<K extends Comparable<?>> extends Settings<K, String> {

	protected String concatenate(String left, String right) {
		return left+right;
	}
	public String nullValue() {
		return null;
	}

	
	/**
	 * Encodes the values with the Base64 encoding
	 */
	public String getBase64List(String propertySeparator, String kvSeparator) {
		StringBuffer sb = new StringBuffer();
		for (Entry<K, String> e : settingsMap.entrySet()) {
			if (isSet(e.getKey())) {
				sb.append(e.getKey() + kvSeparator + Base64.encodeBytes(get_(e.getKey()).getBytes()) + propertySeparator);
			}
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - propertySeparator.length());
		}
		return sb.toString();
	}
	
}
