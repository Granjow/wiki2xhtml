package src.guiWindows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.Container_Colors;
import src.GUI;
import src.Container_Resources;
import src.UserInterface;
import src.tools.ScriptExport;
import src.tools.UpdateMenufile;
import src.tools.UpdateTemplate;

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
 * The MenuBar for the GUI
 *
 * @author Simon Eugster
 */
public final class GuiMenuBar {

	private final I18n i18n = I18nFactory.getI18n(GuiMenuBar.class, "bin.l10n.Messages", src.Globals.getLocale());

	private static final Container_Resources cr = Container_Resources.getInstance();

	private JMenuBar menubar = null;
	private JMenu jmFile = null, jmExport = null, jmSettings = null, jmTools = null, jmInfo = null, jmLanguage = null;
	private JMenuItem jmiOpen = null, jmiSave = null, jmiExportBat = null, jmiExportSh = null, jmiQuit = null, jmiPreferences = null,
								jmiHelp = null, jmiAbout = null, jmiUpdateMenufile = null, jmiUpdateTemplatefile = null;
	private JMenuItem jmiLangDe = null, jmiLangEn = null, jmiLangDeCH = null, jmiLangEnGB = null,
								  jmiLangIt = null, jmiLangItCH, jmiLangFr = null, jmiLangFrCH = null, jmiLangRu = null, jmiLangEs = null, jmiLangHr = null;

	private JEPwindow jepHelp = null, jepAbout = null;

	private DialoguePreferences dp = null;

	public GuiMenuBar() {
	}

	public  JMenuBar getMenuBar(GUI gui) {
		if (menubar == null) {
			menubar = new JMenuBar();
			menubar.add(getJMFile(gui));
			menubar.add(getJMSettings());
			menubar.add(getJMTools());
			menubar.add(getJMInfo());
			menubar.setBackground(Container_Colors.lightGrey);
			menubar.setBorder(BorderFactory.createEtchedBorder());
		}
		return menubar;
	}

	private JMenu getJMFile(GUI gui) {
		if (jmFile == null) {
			jmFile = new JMenu(i18n.tr("File"));
			jmFile.setMnemonic('f');

			/*
			 * Add the menu items
			 */
			jmFile.add(getJmiOpen(gui));
			jmFile.add(getJmiSave());
			jmFile.add(getJMExport());
			jmFile.addSeparator();
			jmFile.add(getJmiQuit());
		}
		return jmFile;
	}

	private JMenu getJMExport() {
		if (jmExport == null) {
			jmExport = new JMenu(i18n.tr("Export"));
			jmExport.setMnemonic('e');

			jmExport.add(getJmiExportSh());
			jmExport.add(getJmiExportBat());
		}
		return jmExport;
	}

	private JMenuItem getJmiOpen(final GUI gui) {
		if (jmiOpen == null) {
			jmiOpen = new JMenuItem(i18n.tr("Open"), cr.rOpen);
			jmiOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ScriptExport.readFile();
					gui.updateSettings();
				}
			});
			jmiOpen.setMnemonic('o');
			jmiOpen.setAccelerator(
				KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK));
		}
		return jmiOpen;
	}

	private JMenuItem getJmiSave() {
		if (jmiSave == null) {
			jmiSave = new JMenuItem(i18n.tr("Save"), cr.rSaveDisk);
			jmiSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ScriptExport.writeFile(ScriptExport.Filetype.wx);
				}
			});
			jmiSave.setMnemonic('s');
			jmiSave.setAccelerator(
				KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
		}
		return jmiSave;
	}

	private JMenuItem getJmiExportBat() {
		if (jmiExportBat == null) {
			jmiExportBat = new JMenuItem(i18n.tr("Export as .bat Script (Windows)"), cr.rExportBat);
			jmiExportBat.setMnemonic('w');

			jmiExportBat.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ScriptExport.writeFile(ScriptExport.Filetype.bat);
				}
			});
		}
		return jmiExportBat;
	}

	private JMenuItem getJmiExportSh() {
		if (jmiExportSh == null) {
			jmiExportSh = new JMenuItem(i18n.tr("Export as .sh Script (Unix)"), cr.rExportSh);
			jmiExportSh.setMnemonic('u');

			jmiExportSh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ScriptExport.writeFile(ScriptExport.Filetype.sh);
				}
			});
		}
		return jmiExportSh;
	}

	private JMenuItem getJmiQuit() {
		if (jmiQuit == null) {
			jmiQuit = new JMenuItem(i18n.tr("Quit"), cr.rClose);
			jmiQuit.setMnemonic('q');
			jmiQuit.setAccelerator(
				KeyStroke.getKeyStroke('Q', InputEvent.CTRL_MASK));

			jmiQuit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					System.exit(0);
				}
			});
		}
		return jmiQuit;
	}

	private JMenu getJMSettings() {
		if (jmSettings == null) {
			jmSettings = new JMenu(i18n.tr("Settings"));
			jmSettings.setMnemonic('s');

			jmSettings.add(getJmiPreferences());
			jmSettings.add(getJmLanguage());
		}
		return jmSettings;
	}

	private JMenu getJMTools() {
		if (jmTools == null) {
			jmTools = new JMenu(i18n.tr("Tools"));
			jmTools.setMnemonic('t');

			jmTools.add(getJmiUpdateMenufile());
			jmTools.add(getJmiUpdateTemplatefile());
		}
		return jmTools;
	}

	private JMenuItem getJmiPreferences() {
		if (jmiPreferences == null) {
			jmiPreferences = new JMenuItem(i18n.tr("Preferences"));
			jmiPreferences.setMnemonic('p');
			jmiPreferences.setAccelerator(
				KeyStroke.getKeyStroke(',', InputEvent.CTRL_MASK));

			jmiPreferences.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (dp == null)
						dp = new DialoguePreferences();
					dp.getWindow().setVisible(true);
				}
			});
		}
		return jmiPreferences;
	}

	private JMenu getJmLanguage() {
		if (jmLanguage == null) {
			jmLanguage = new JMenu(i18n.tr("Language"));
			jmLanguage.setMnemonic('l');

			jmLanguage.add(getJmiLangEn());
			jmLanguage.add(getJmiLangEnGB());
			jmLanguage.add(getJmiLangDe());
			jmLanguage.add(getJmiLangDeCH());
			jmLanguage.add(getJmiLangFr());
			jmLanguage.add(getJmiLangFrCH());
			jmLanguage.add(getJmiLangIt());
			jmLanguage.add(getJmiLangItCH());
			jmLanguage.add(getJmiLangEs());
			jmLanguage.add(getJmiLangRu());
			jmLanguage.add(getJmiLangHr());
		}
		return jmLanguage;
	}

	private JMenuItem getJmiLangDe() {
		if (jmiLangDe == null) {
			jmiLangDe = new JMenuItem("Deutsch", cr.rGermany);
			jmiLangDe.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					src.Globals.setLocale(Locale.GERMAN);

					UserInterface.restartGui();
				}
			});
		}
		return jmiLangDe;
	}

	private JMenuItem getJmiLangDeCH() {
		if (jmiLangDeCH == null) {
			jmiLangDeCH = new JMenuItem("Deutsch, Schweiz", cr.rSwiss);
			jmiLangDeCH.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					src.Globals.setLocale("de_CH");

					UserInterface.restartGui();
				}
			});
		}
		return jmiLangDeCH;
	}

	private JMenuItem getJmiLangEn() {
		if (jmiLangEn == null) {
			jmiLangEn = new JMenuItem("English");
			jmiLangEn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					src.Globals.setLocale(Locale.ENGLISH);

					UserInterface.restartGui();
				}
			});
		}
		return jmiLangEn;
	}

	private JMenuItem getJmiLangEnGB() {
		if (jmiLangEnGB == null) {
			jmiLangEnGB = new JMenuItem("English GB");
			jmiLangEnGB.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					src.Globals.setLocale("en_GB");

					UserInterface.restartGui();
				}
			});
		}
		return jmiLangEnGB;
	}

	private JMenuItem getJmiLangFr() {
		if (jmiLangFr == null) {
			jmiLangFr = new JMenuItem("Français", cr.rFrance);
			jmiLangFr.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					src.Globals.setLocale(Locale.FRENCH);

					UserInterface.restartGui();
				}
			});
		}
		return jmiLangFr;
	}

	private JMenuItem getJmiLangFrCH() {
		if (jmiLangFrCH == null) {
			jmiLangFrCH = new JMenuItem("Français, Suisse", cr.rSwiss);
			jmiLangFrCH.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					src.Globals.setLocale("fr_CH");

					UserInterface.restartGui();
				}
			});
		}
		return jmiLangFrCH;
	}

	private JMenuItem getJmiLangIt() {
		if (jmiLangIt == null) {
			jmiLangIt = new JMenuItem("Italiano", cr.rItaly);
			jmiLangIt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					src.Globals.setLocale(Locale.ITALIAN);

					UserInterface.restartGui();
				}
			});
		}
		return jmiLangIt;
	}

	private JMenuItem getJmiLangItCH() {
		if (jmiLangItCH == null) {
			jmiLangItCH = new JMenuItem("Italiano, Svizzera", cr.rSwiss);
			jmiLangItCH.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					src.Globals.setLocale("it_CH");

					UserInterface.restartGui();
				}
			});
		}
		return jmiLangItCH;
	}

	private JMenuItem getJmiLangRu() {
		if (jmiLangRu == null) {
			jmiLangRu = new JMenuItem("Русский");
			jmiLangRu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					src.Globals.setLocale("ru");

					UserInterface.restartGui();
				}
			});
		}
		return jmiLangRu;
	}

	private JMenuItem getJmiLangEs() {
		if (jmiLangEs == null) {
			jmiLangEs = new JMenuItem("Español");
			jmiLangEs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					src.Globals.setLocale("es");

					UserInterface.restartGui();
				}
			});
		}
		return jmiLangEs;
	}

	private JMenuItem getJmiLangHr() {
		if (jmiLangHr == null) {
			jmiLangHr = new JMenuItem("Hrvatski jezik");
			jmiLangHr.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					src.Globals.setLocale("hr");

					UserInterface.restartGui();
				}
			});
		}
		return jmiLangHr;
	}

	private JMenu getJMInfo() {
		if (jmInfo == null) {
			jmInfo = new JMenu(i18n.tr("Info"));
			jmInfo.setMnemonic('i');

			/* Add the menu items */
			jmInfo.add(getJmiHelp());
			jmInfo.add(getJmiAbout());
		}
		return jmInfo;
	}

	private JMenuItem getJmiHelp() {
		if (jmiHelp == null) {
			jmiHelp = new JMenuItem(i18n.tr("Help"), cr.rHelp);
			jmiHelp.setMnemonic('h');
			jmiHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			jmiHelp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (jepHelp == null)
						jepHelp = new JEPwindow(i18n.tr("Help"), 500, 350, Container_Resources.uhelp);
					jepHelp.show();
				}
			});

		}
		return jmiHelp;
	}

	private JMenuItem getJmiAbout() {
		if (jmiAbout == null) {
			jmiAbout = new JMenuItem(i18n.tr("About"), cr.rAbout);
			jmiAbout.setMnemonic('a');
			jmiAbout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jepAbout == null)
						jepAbout = new JEPwindow(i18n.tr("About wiki2xhtml"), 500, 350, Container_Resources.uabout);
					jepAbout.show();
				}
			});
		}
		return jmiAbout;
	}

	private JMenuItem getJmiUpdateMenufile() {
		if (jmiUpdateMenufile == null) {
			jmiUpdateMenufile = new JMenuItem(i18n.tr("Update Menufile"));
			jmiUpdateMenufile.setMnemonic('m');
			jmiUpdateMenufile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (UpdateMenufile.run(null, false))
						JOptionPane.showMessageDialog(null, i18n.tr("Menufile updated. Backup has been created."));
				}
			});
		}
		return jmiUpdateMenufile;
	}

	private JMenuItem getJmiUpdateTemplatefile() {
		if (jmiUpdateTemplatefile == null) {
			jmiUpdateTemplatefile = new JMenuItem(i18n.tr("Update template file"));
			jmiUpdateTemplatefile.setMnemonic('m');
			jmiUpdateTemplatefile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (UpdateTemplate.run(null, false))
						JOptionPane.showMessageDialog(null, i18n.tr("Template file updated. Backup has been created."));
				}
			});
		}
		return jmiUpdateTemplatefile;
	}

}
