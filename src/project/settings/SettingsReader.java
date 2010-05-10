package src.project.settings;

import java.util.ArrayList;
/*
 *   Copyright (C) 2007-2009 Simon Eugster <granjow@users.sf.net>

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
 */

public class SettingsReader<K extends Comparable<?>, V extends Comparable<?>> {

	// Search for settings (XhtmlSettings)
	
	private final ArrayList<SettingReader<K, V>> pageSettingsReader;
	
	private final StringBuffer content;
	private final Settings<K, V> settings;
	
	public SettingsReader(StringBuffer content, Settings<K, V> settings) {
		pageSettingsReader = new ArrayList<SettingReader<K, V>>();
		this.content = content;
		this.settings = settings;
	}
	
	/** Attaches a reader if it does not exist yet */
	public boolean attachReader(SettingReader<K,V> reader) {
		if (!pageSettingsReader.contains(reader)) {
			System.err.println("Attaching reader " + reader.getID());
			return pageSettingsReader.add(reader);
		} else {
			return false;
		}
	}
	
	/** Reads all settings and, if desired, removes them from the input. */
	public void readSettings(boolean remove) {
		for (SettingReader<K, V> reader : pageSettingsReader) {
			reader.read(settings, content, remove);
		}
	}

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		Settings<String, String> sett = new Settings<String, String>() {
			protected String concatenate(String left, String right) {
				return left+right;
			}
			public String nullValue() {
				return null;
			}
		};
		SettingsReader<String, String> r = new SettingsReader<String, String>(sb, sett);
		
		boolean b;
		b = r.attachReader(new SettingReader<String, String>() {
			
			public boolean read(Settings<String, String> settings, 
					StringBuffer in, boolean remove) {
				if (in.indexOf("bla") > 0) {
					if (remove) in.replace(in.indexOf("bla"), in.indexOf("bla")+3, "");
					settings.set_("bla", "true");
					return true;
				}
				return false;
			}
			
			@Override
			public String getID() {
				return "a";
			}
		});
		System.out.println(b);
		b = r.attachReader(new SettingReader<String, String>() {
			
			public boolean read(Settings<String, String> settings,
					StringBuffer in, boolean remove) {
				return true;
			}
			
			@Override
			public String getID() {
				return "b";
			}
		});
		System.out.println(b);
		
		sb.append("Hallo. Hier steht bla.");
		r.readSettings(true);
		System.err.println(sett.isSet("bla") + "; " + sb);
		
	}
	
}
