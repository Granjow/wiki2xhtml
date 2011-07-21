package src.project.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.resources.ResProjectSettings.SettingsE;


/**
 * <p>Reads the {@link SettingsE} from pages or the common page.</p>
 */
public class PageSettingsReader  {

	private final SettingsReader<SettingsE, String> reader;
	
	//TODO settings: {{k:v1|v2|v3}}
	
	/**
	 * Sets up a settings reader for page settings
	 */
	public PageSettingsReader(final StringBuffer content, final PageSettings settings) {
		reader = new SettingsReader<SettingsE, String>(content, settings);
		
		for (final SettingsE p : SettingsE.values()) {
			reader.attachReader(new SettingReader<SettingsE, String>() {
				
				public boolean read(Settings<SettingsE, String> settings, StringBuffer in, boolean remove) {
					boolean worked = false;
					String val = getProperty(p, content, remove);
					if (val != null) {
						worked = settings.set_(p, val);
						if (!worked) {
							System.err.printf("Could not set {{%s:%s}}. Argument is not valid.\n", p.keyword(), val);
						}
					}
					return worked;
				}
				public String getID() {
					return p.keyword().toString();
				}
			});
		}

//		xhs.local.set_(SettingsLocalE.redirect, getRedirect(content, false));
	}
	
	public void readSettings(boolean remove) {
		reader.readSettings(remove);
	}
	
	public static final String getProperty(SettingsE property, StringBuffer content, final boolean remove) {
		String value = null;
		String temp;
		if (property.loop()) {
			// Property may occur multiple times; get them all
			boolean first = true;
			StringBuilder sb = new StringBuilder();
			Pos pos = new Pos(0);
			while ((temp = getArg(property.regex(), content, remove, pos)) != null) {
				if (first) {
					first = false;
				} else {
					sb.append(property.separator());
				}
				sb.append(temp);
			}
			if (sb.length() > 0) value = sb.toString();
		} else {
			// Property only occurring once
			temp = getArg(property.regex(), content, remove, new Pos(0));
			if (temp != null && temp.length() > 0) value = temp;
		}
		
		return value;
	}
	
	/**
	 * @param pattern Pattern to search for
	 * @param content Content to parse
	 * @param remove Remove found groups in <code>content</code> leave it unchanged?
	 * @return <code>null</code>, if not found, and the content otherwise.
	 */
	private static final String getArg(final Pattern pattern, StringBuffer content, final boolean remove, Pos start) {
		Matcher m = pattern.matcher(content);
		String group = null;

		if (m.find(start.pos())) {
			group = m.group(1);
			if (remove) {
				content.delete(m.start(), m.end());
				start.setPos(m.start());
			} else {
				start.setPos(m.end());
			}
		}

		return group;
	}
	
	private static class Pos {
		private int pos;
		Pos(int pos) { this.pos = pos; }
		public int pos() { return pos; }
		public void setPos(int pos) { this.pos = pos; }
	}
	
	private static final void test(final StringBuffer sb) {
		sb.append("{{H1:Hallo Titel}}");
	}
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		PageSettings sett = new PageSettings();
		PageSettingsReader r = new PageSettingsReader(sb, sett);
		
		test(sb);
		System.err.println(sb);
		
		System.err.println(sett.get_(SettingsE.h1));
		r.readSettings(true);
		System.err.println(sett.get_(SettingsE.h1));
		
	}
	
}
