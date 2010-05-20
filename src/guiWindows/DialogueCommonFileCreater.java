package src.guiWindows;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


import src.Constants;
import src.commentator.CommentAtor;
import src.commentator.CommentAtor.CALevel;
import src.resources.ResProjectSettings.SettingsE;
import src.settings.XhtmlSettings;
import src.settings.XhtmlSettings.NegativeValueException;

import javax.swing.*;

/*
 *   Copyright (C) 2007-2010 Simon Eugster <granjow@users.sf.net>

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
 *
 */

/**
 *
 * @author Simon Eugster
 * ,		hb9eia
 */
public final class DialogueCommonFileCreater {

	private static I18n i18n = I18nFactory.getI18n(MiscWindows.class, "bin.l10n.Messages", src.Globals.getLocale());

	public static JFrame getWindow(boolean standalone) {

		JFrame f = new JFrame(i18n.tr("common.txt Wizard"));
		if (standalone)
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		else
			f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		f.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) { }
			public void keyReleased(KeyEvent arg0) {
				if (arg0.equals(KeyEvent.VK_ESCAPE)) {
					System.exit(0);
				}
			}
			public void keyTyped(KeyEvent arg0) { }
		});

		f.setLayout(new java.awt.GridLayout(/* auto */ 0, 2, /* margin */ 10, 4));
//		f.add(new JLabel(i18n.tr("Path of this common file")));
//		f.add(new CTextField(cPath));
		f.add(new JLabel(i18n.tr("Author")));
		f.add(new CTextField(SettingsE.author));
		f.add(new JLabel(i18n.tr("Default title")));//TODO yes/no/inherit
		f.add(new CTextField(SettingsE.defaultTitle));
//		f.add(new JLabel(i18n.tr("Use image description for caption on image pages")));
//		f.add(new CCheckBox(SettingsE.descForCaption));
		f.add(new JLabel(i18n.tr("Number of images per line in the gallery")));
		f.add(new CTextField(SettingsE.galleryImagesPerLine, InputMaskE.numberNatural));
		f.add(new JLabel(i18n.tr("Width of thumbnails in the gallery")));
		f.add(new CTextField(SettingsE.galleryThumbWidth, InputMaskE.numberNatural));
		f.add(new JLabel(i18n.tr("Link to the start page")));
		f.add(new CTextField(SettingsE.homelink));
		f.add(new JLabel(i18n.tr("Path to Favicon")));
		f.add(new CTextField(SettingsE.icon));
		f.add(new JLabel(i18n.tr("Width of images on image pages")));
		f.add(new CTextField(SettingsE.imagepageImgWidth, InputMaskE.numberNatural));
		f.add(new JLabel(i18n.tr("Directory for image pages")));
		f.add(new CTextField(SettingsE.imagepagesDir));
		f.add(new JLabel(i18n.tr("Standard directory for images")));
		f.add(new CTextField(SettingsE.imagesDir));
		f.add(new JLabel(i18n.tr("Standard keywords")));
		f.add(new CTextField(SettingsE.keywords));
		f.add(new JLabel(i18n.tr("Language pages are written in")));
		f.add(new CTextField(SettingsE.lang));
//		f.add(new JLabel(i18n.tr("Use image name for caption on image pages")));
//		f.add(new CCheckBox(SettingsE.nameForCaption));
		//f.add(new CTextField(Constants.Settings.reckAlternative));
		f.add(new JLabel(i18n.tr("Text header")));
		f.add(new CTextField(SettingsE.textHeader));
		f.add(new JLabel(i18n.tr("Directory for thumbnails")));
		f.add(new CTextField(SettingsE.thumbsDir));
		f.add(new JLabel(i18n.tr("Width of normal thumbnails")));
		f.add(new CTextField(SettingsE.thumbWidth, InputMaskE.numberNatural));
		f.add(new JLabel(i18n.tr("Standard title")));
		f.add(new CTextField(SettingsE.title));

		f.pack();

		return f;
	}

	/*
	 * Common file reader -> XhtmlSettings.Global (> XhtmlSettings.AllSettings)
	 * Dialog writes «common file»: Common file writer -> XhtmlSettings.AllSettings
	 * ... and reads common file
	 */

	private static class CCheckBox extends JCheckBox {
		private static final long serialVersionUID = 1L;

		public CCheckBox(final SettingsE property) {
			super(i18n.tr("Yes"));

			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
//					try {
//						SettingsCollector.set(property, "" + isSelected());
//					} catch (NegativeValueException e) {
//						CommentAtor.getInstance().ol(e.getMessage(), CALevel.ERRORS);
//					}
				}
			});
		}
	}

	private enum InputMaskE {

		all("^.*$"), numberNatural("^\\d*$"), number("^[+-]?\\d*$");

		private Pattern maskPattern;

		InputMaskE(String mask) {
			maskPattern = Pattern.compile(mask);
		}

		public Pattern maskPattern() {
			return maskPattern;
		}

	}

	private static class CTextField extends JTextField {
		private static final long serialVersionUID = 1L;
		private String lastInput = "";

		public CTextField(final SettingsE property) {
			this(property, InputMaskE.all);
		}

		public CTextField(final SettingsE property, final InputMaskE inputMask) {
			super();

			addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent arg0) {

					// Validate input
					Matcher m = inputMask.maskPattern().matcher(getText());
					if (!m.matches()) {
						setText(lastInput);
						System.err.println(lastInput);
					} else {
						lastInput = getText();

						// Set property
//						try {
//							SettingsCollector.set(property, getText());
//						} catch (NegativeValueException e) {
//							CommentAtor.getInstance().ol(e.getMessage(), CALevel.ERRORS);
//						}
					}
				}
				public void keyReleased(KeyEvent arg0) { }
				public void keyTyped(KeyEvent arg0) { }
			});

		}
	}

	private static class SettingsCollector {

//		private static XhtmlSettings.Settings s = new XhtmlSettings.Settings();
//
//		public static void set(src.SettingsE property, String arg) throws NegativeValueException {
//			s.set_(property, arg);
//		}

	}


	public static void main(String[] args) {
		JFrame f = getWindow(true);
		f.setVisible(true);

	}

}
