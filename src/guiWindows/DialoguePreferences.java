package src.guiWindows;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.ConstantTexts;
import src.Constants;
import src.Container_Colors;
import src.Container_Resources;
import src.Globals;
import src.update.UpdateChecker;


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
 *   along with this src.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * This class creates a window where preferences can be set.
 *
 * @author Simon Eugster
 */
final class DialoguePreferences {

	private final I18n i = I18nFactory.getI18n(DialoguePreferences.class, "bin.l10n.Messages", src.Globals.getLocale());

	private int width = 640, height = 100;

	private JFrame window = null;

	JButton jbUpdate = null;
	JCheckBox jcbUpdate = null;
	JLabel jlUpdate = null, jlUpdateMsg = null, jlIgnoreV = null;
	JEditorPane jtaNotes = null;
	JTextField jtfIgnoreV = null;

	private static void addComponent(Container c, GridBagLayout gbl, Component co,
									 int x, int y, int width, int height, double weightx, double weighty) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbl.setConstraints(co, gbc);
		c.add(co);
	}

	public JFrame getWindow() {
		if (window == null) {
			window = new JFrame(i.tr("Preferences"));

			window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			window.setResizable(true);
			window.setFocusable(true);

			window.setBackground(Container_Colors.lightGrey);
			window.setIconImage(Container_Resources.getInstance().getIcon());

			AbstractAction aa = new AbstractAction() {
				private static final long serialVersionUID = -4682579444861777254L;

				public void actionPerformed(ActionEvent e) {
					window.setVisible(false);
				}
			};
			window.getRootPane().getActionMap().put("exit", aa);
			InputMap im = window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			im.put(Constants.Keys.quit, "exit");
			im.put(Constants.Keys.close, "exit");
			im.put(Constants.Keys.esc, "exit");

			GridBagLayout gbl = new GridBagLayout();

			Container c = window.getContentPane();
			c.setLayout(gbl);
			c.setBackground(Container_Colors.lightGrey);

			addComponent(c, gbl, getJbUpdate(), 0, 0, 1, 1, 0, 0);
			addComponent(c, gbl, getJlUpdateMsg(), 1, 0, 2, 1, .1, 0);

			addComponent(c, gbl, getJtaNotes(), 0, 1, 3, 2, 0, 1);

			addComponent(c, gbl, getJcbUpdate(), 0, 3, 1, 1, 0, 0);
			addComponent(c, gbl, getJlIgnoreV(), 1, 3, 1, 1, 0, 0);
			addComponent(c, gbl, getJtfIgnoreV(), 2, 3, 1, 1, 0, 0);

			window.setSize(width, height);
			window.setVisible(true);
		}
		return window;
	}

	private JButton getJbUpdate() {
		if (jbUpdate == null) {
			jbUpdate = new JButton(i.tr("Check for updates"));
			jbUpdate.setMnemonic('c');

			jbUpdate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					String msg = UpdateChecker.getUpdateMessage().toString();
					if (UpdateChecker.isEmpty())
						getJtaNotes().setText(ConstantTexts.noConnection);
					else
						getJtaNotes().setText(msg);

					int i = window.getHeight() - height;
					int plus = UpdateChecker.getIncreaseSize();
					for ( ; i < plus; i += 2) {
						window.setSize(width, height + i);
					}

				}
			});
		}
		return jbUpdate;
	}

	private JCheckBox getJcbUpdate() {
		if (jcbUpdate == null) {
			jcbUpdate = new JCheckBox(i.tr("Check for updates on Startup?"));
			jcbUpdate.setMnemonic('u');

			jcbUpdate.setSelected(Globals.programSettings.isTrue(Constants.ProgramSettings.autoUpdate));

			jcbUpdate.setBackground(Container_Colors.lightGrey);

			jcbUpdate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					if (Globals.programSettings.readSettings()) {
						Globals.programSettings.set(Constants.ProgramSettings.autoUpdate, jcbUpdate.isSelected());
					}

				}
			});

		}
		return jcbUpdate;
	}

	private JLabel getJlUpdateMsg() {
		if (jlUpdateMsg == null) {
			jlUpdateMsg = new JLabel(i.tr("You''re currently using wiki2xhtml {0} from {1}.", Constants.Wiki2xhtml.versionNumber, Constants.Wiki2xhtml.versionDate));
			jlUpdateMsg.setFont(src.GUI.stdFont);
		}
		return jlUpdateMsg;
	}

	private JLabel getJlIgnoreV() {
		if (jlIgnoreV == null) {
			jlIgnoreV = new JLabel(i.tr("Ignore Version:"));
			jlIgnoreV.setFont(src.GUI.stdFont);
		}
		return jlIgnoreV;
	}

	private JTextField getJtfIgnoreV() {
		if (jtfIgnoreV == null) {
			jtfIgnoreV = new JTextField();

			Globals.programSettings.readSettings();
			jtfIgnoreV.setText(Globals.programSettings.settings.get(Constants.ProgramSettings.ignoreNewVersion));

			jtfIgnoreV.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					setVersion(jtfIgnoreV.getText());
				}
			});

			jtfIgnoreV.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) { }
				public void focusLost(FocusEvent e) {
					setVersion(jtfIgnoreV.getText());
				}
			});

		}
		return jtfIgnoreV;
	}

	private JEditorPane getJtaNotes() {
		if (jtaNotes == null) {
			jtaNotes = new JEditorPane("text/html", "");
			jtaNotes.setBackground(Container_Colors.lightGrey);
		}
		return jtaNotes;
	}

	private void setVersion(String nr) {
		if (Globals.programSettings.readSettings()) {
			Globals.programSettings.set(Constants.ProgramSettings.ignoreNewVersion, nr);
			Globals.programSettings.writeSettings();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DialoguePreferences dp = new DialoguePreferences();
		dp.getWindow().setVisible(true);
	}

}
