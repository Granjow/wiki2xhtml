package src.project.settings;

import src.resources.ResProjectSettings.SettingsE;

public class DesignSettingsReader {

	//TODO execute before reading project settings
	
	private SettingsReader<SettingsE, String> reader;

	/** Reads design specific settings */
	public DesignSettingsReader(final StringBuffer content, final PageSettings settings) {
		reader = new SettingsReader<SettingsE, String>(content, settings);
		for (final SettingsE p : new SettingsE[] {SettingsE.galleryImagesPerLine, SettingsE.imagepageImgWidth,
				SettingsE.thumbWidth, SettingsE.galleryThumbWidth}) {
			reader.attachReader(new SettingReader<SettingsE, String>() {
				
				public boolean read(Settings<SettingsE, String> settings, StringBuffer in, boolean remove) {
					String val = PageSettingsReader.getProperty(p, content, remove);
					if (val != null) {
						return settings.set_(p, val);
					}
					return false;
				}
				public String getID() {
					return p.keyword().toString();
				}
			});
		}
	}
	
	public void read(boolean remove) {
		reader.readSettings(remove);
	}
	
}
