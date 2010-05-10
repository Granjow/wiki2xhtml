package unittests;

import src.Constants;
import src.commentator.CommentAtor;

import src.resources.ResProjectSettings.SettingsE;
import src.settings.XhtmlSettings;
import src.settings.XhtmlSettingsReader;

public class XhtmlSettingsTester extends junit.framework.TestCase {
	
	public void testReadCommonFile() {
		StringBuffer sb = new StringBuffer();
		XhtmlSettings.LocalSettings sett;
		sb.append("{{GalleryImagesPerLine:42}}\n");
		sb.append("{{ThumbWidthImages:22px}}\n");
		sb.append("{{Author:Autor}}\n");
		sb.append("{{DescForCaption}}\n");
		sb.append("{{Desc:Description}}\n");
		sb.append("{{DirImages:my\\dir/}}");
		sb.append("{{DirImagepages:my\\dir/2}}");
		sett = (XhtmlSettings.LocalSettings) XhtmlSettingsReader.getSettings(sb, false);
		assertEquals("42", sett.get_(SettingsE.galleryImagesPerLine));
		assertEquals("Autor", sett.get_(SettingsE.author));
		assertEquals("Description", sett.get_(SettingsE.desc));
		assertEquals("Thumb width", "22px", sett.get_(SettingsE.thumbWidth));
		assertEquals("Image dir", "my\\dir/", sett.get_(SettingsE.imagesDir));
		assertEquals("Imagepage dir", "my\\dir/2", sett.get_(SettingsE.imagepagesDir));

		
		XhtmlSettings.GlobalSettings gsett;
		sb = new StringBuffer();
		sb.append("{{DefaultTitle:deft}}\n");
		gsett = (XhtmlSettings.GlobalSettings) XhtmlSettingsReader.getSettings(sb, true);
		assertEquals("Default title", "deft", gsett.get_(SettingsE.defaultTitle));
		
		
		
		
		CommentAtor.getInstance().setDead(true);
		sb = new StringBuffer();
		sb.append("{{GalleryImagesPerLine:-42}}\n");
		sett = (XhtmlSettings.LocalSettings) XhtmlSettingsReader.getSettings(sb, false);
		assertEquals(Constants.Standards.galleryImagesPerLine, "" + sett.galleryImagesPerLine());
		CommentAtor.getInstance().setDead(false);
		
		sb = new StringBuffer();
		sb.append("{{Description:Description}}\n");
		sett = (XhtmlSettings.LocalSettings) XhtmlSettingsReader.getSettings(sb, false);
		assertEquals("{{Description:", "Description", sett.get_(SettingsE.desc));
	}
	
	public void testReadFile() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("{{H1:<?php if (whatever) {something} ?>}}");
		
		XhtmlSettingsReader.getXhtml(sb);
		assertEquals("<?php if (whatever) {something} ?>", XhtmlSettings.getInstance().local.get_(SettingsE.h1));
		
	}

}
