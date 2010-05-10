package src;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

import javax.swing.*;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import src.Args.GetPolicyE;
import src.commentator.CommentAtor;
import src.commentator.Logger;
import src.commentator.CommentAtor.CALevel;
import src.guiWindows.GuiFunctions;
import src.guiWindows.GuiMenuBar;
import src.guiWindows.MiscWindows;
import src.tools.ScriptExport;
import src.update.UpdateChecker;
import src.utilities.FileDialogues;
import src.utilities.IORead_Stats;
import src.utilities.IOUtils;
import src.utilities.IOWrite_Stats;
import src.utilities.StringTools;


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
 * The GUI for wiki2xhtml
 *
 * @author Simon Eugster
 */
public class GUI {

	private I18n i18n = I18nFactory.getI18n(GUI.class, "bin.l10n.Messages", src.Globals.getLocale());

	private static final CommentAtor ca = CommentAtor.getInstance();
	private static final Container_Files fc = Container_Files.getInstance();
	private static final Container_Resources cr = Container_Resources.getInstance();

	private GuiMenuBar gm = new GuiMenuBar();

	public static final Font stdFont = new Font("Verdana", Font.PLAIN, 11),
	bigFont = new Font("Verdana", Font.PLAIN, 22);

	private Threads thread = new Threads();

	private final short wWidth = 700, wHeight = 660, firstIndent = 20,
								 lastIndent = 260, firstWidth = 200, lastWidth = 400;
	private int height = 20, yc = 0, tHeight = wHeight, tWidth = wWidth;

	private final String className = "GUI: ";

	private String time = "";

	public Dimension screenSize;

	public JFrame jFp = null, jFc = null;
	JFrame f = null;

	private Container c = null, contFiles = null, contSettings = null, contParse = null;

	private JLabel lblTitle = null;
	private JLabel lblTime = null;

	private JTabbedPane jTab = null;

	private JCheckBox cbStd = null, cbClear = null,
							  cbHelp = null, cbNoLinebreaks = null,
															  cbIncremental = null;

	private JTextArea jtaP = null, jtaC = null, jtaParse = null;
	private JTextField jtfCommon = null, jtfFooter = null,
								   jtfFiles = null, jtfTargetDir = null, //jtfParse = null,
																   jtfMenu = null;

	private JButton bMenuFile = null, bCommonFile = null,
								bFooterFile = null, bAddFile = null,
															   bTargetDir = null, bParse = null, bExit = null, bSave = null, bNext = null, bNext2 = null,
																						   bOpen = null, bSaveSettings = null, bOpenSettings = null;
	private JToggleButton jtbOnlyCode = null, jtbCode = null, jtbBigfont = null;

	private JScrollPane jspPaste = null, jspCode = null;
	private String unicode = new String();
	private boolean shift, ctrl, numpad;

	private JProgressBar jpbP = null;

	private JComboBox jcbStyle = null;

	private ExtendedUndoManager undo = null;

	public GUI() throws java.awt.HeadlessException {
		screenSize = new Dimension(0,0);
		try {
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		}
		catch (AWTError e) {}
//		catch (HeadlessException e) {
//			// If e.g. working in a tty
//		}
	}


	private static final void setPathText(JTextField jtf, File f) {
		jtf.setText(f == null ? "" : f.getPath());
	}

	public void updateSettings() {
		jtbOnlyCode.setSelected(fc.sc.onlyCode);
		cbNoLinebreaks.setSelected(fc.sc.removeLineBreaks);

		setPathText(jtfMenu, fc.cont.menuFile);
		setPathText(jtfCommon, fc.cont.commonFile);
		setPathText(jtfFooter, fc.cont.footerFile);

		jtfFiles.setText(fc.fileList().getArgs(GetPolicyE.AllArgs).toString());
//TODO 1 fix
		if (fc.cont.targetDir() != null && fc.cont.targetDir().exists()) {
			jtfTargetDir.setText(fc.cont.targetDir().getPath());
		}
		jtaParse.setText(fc.allArguments().append(fc.fileList()).getArgs(GetPolicyE.AllArgs).toString());
		if (fc.cont.styleDir() != null)
			if (fc.has.style == false)
				getJcbStyle(0).setSelectedIndex(0);
			else
				getJcbStyle(0).setSelectedItem(fc.cont.styleDir().getName());
		if (fc.filesNumber() > 0) {
			getBSaveSettings(0).setEnabled(true);
			getBParse(0).setEnabled(true);
		} else {
			getBParse(0).setEnabled(false);
			getBSaveSettings(0).setEnabled(false);
		}

		if (XHTML.baseStyleDir != null && XHTML.baseStyleDir.exists() && XHTML.baseStyleDir.isDirectory())
			getJcbStyle(0).setEditable(true);
		else
			getJcbStyle(0).setEnabled(false);

		if (fc.sc.incremental) getCbIncremental(0).setSelected(true);

		if (Globals.programSettings.readSettings()) {
			Globals.programSettings.set(Constants.ProgramSettings.lastArgs, jtaParse.getText());
		}
	}

	private void updateTimerDisplay() {
		getLblTime(0).setText("Time taken: " + time);
	}

	private static final PrintStream o = System.out;

	private void lbl(Container c, String text, int x, int y, int width) {
		JLabel jl = new JLabel(text);
		jl.setBounds(x, y, width, height);
		c.add(jl);
	}

	private static JButton bi(Icon i, int x, int y, int width, int height) {
		JButton btn = new JButton();
		btn.setIcon(i);
		btn.setBounds(x, y, width, height);
		return btn;
	}

	public KeyListener getklEsc() {
		return new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 27)
					updateSettings();
			}
			public void keyReleased(KeyEvent arg0) { }
			public void keyTyped(KeyEvent arg0) {}
		};
	}

	private AbstractAction toggleCode = new AbstractAction() {
		private static final long serialVersionUID = 1;

		public void actionPerformed(ActionEvent e) {
			getJtbCode(0, 0, 0).setSelected(!getJtbCode(0, 0, 0).isSelected());
			toggleCodeWindow(jtbCode.isSelected());
		}
	};


	private JTabbedPane getJTab(int y) {
		if (jTab == null) {
			jTab = new JTabbedPane();
			tWidth = wWidth - 11;
			tHeight = wHeight - y - 50;
			jTab.setBounds(0, y, tWidth, tHeight);
			jTab.addTab(i18n.tr("Files"), getCt1());
			jTab.addTab(i18n.tr("Settings"), getCt2());
			jTab.addTab(i18n.tr("Parse"), getCt3());
		}
		return jTab;
	}

	private Container getCt1() {
		if (contFiles == null) {
			contFiles = new Container();
			contFiles.setLayout(null);
		}
		return contFiles;
	}

	private Container getCt2() {
		if (contSettings == null) {
			contSettings = new Container();
			contSettings.setLayout(null);
		}
		return contSettings;
	}

	private Container getCt3() {
		if (contParse == null) {
			contParse = new Container();
			contParse.setLayout(null);
		}
		return contParse;
	}

	private JFrame getJFPaste() {
		if (jFp == null) {
			jFp = new JFrame(Constants.Wiki2xhtml.progName + ": " + i18n.tr("Code Window (leave with Esc)"));
			jFp.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			jFp.setResizable(true);
			jFp.setFocusable(true);
			jFp.setBounds(0, wHeight, screenSize.width, screenSize.height
						  - wHeight - 40);


			final short y = 0;

			jFp.addComponentListener(new ComponentListener() {
				public void componentHidden(ComponentEvent e) {}

				public void componentMoved(ComponentEvent e) {}

				public void componentResized(ComponentEvent e) {
					jtaP.setBounds(0, y, e.getComponent().getWidth() - 4, e
								   .getComponent().getHeight() - 4 - 2 * height);
					jspPaste.setBounds(0, y + 2*height, e.getComponent().getWidth() - 4, e
									   .getComponent().getHeight() - 4 - 4 * height);

				}

				public void componentShown(ComponentEvent e) {};
			});
			jFp.addWindowListener(new WindowListener() {
				public void windowActivated(WindowEvent e) {}

				public void windowClosing(WindowEvent e) {
					getJFCode().dispose();
					jtbCode.setSelected(false);
				};

				public void windowDeactivated(WindowEvent e) {}

				public void windowDeiconified(WindowEvent e) {}

				public void windowIconified(WindowEvent e) {}

				public void windowOpened(WindowEvent e) {}

				public void windowClosed(WindowEvent e) {}
			});

			jFp.getRootPane().getActionMap().put("toggleCode", toggleCode);
			InputMap im = jFp.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			im.put(Constants.Keys.toggleCodeWindow, "toggleCode");

			Container con = jFp.getContentPane();
			con.setLayout(null);

			/* Add the component(s): Scrollable text field */
			getJtaPaste();
			con.add(getJspPaste());
			jtaP.setBounds(0, 0, jFp.getWidth() - 4, jFp.getHeight() - 4);
			jspPaste.setBounds(0, 2*height, jFp.getWidth() - 4, jFp.getHeight() - 4);

			con.add(getBOpen());
			con.add(getBSave());
		}
		return jFp;

	}

	private JFrame getJFCode() {
		if (jFc == null) {
			jFc = new JFrame(Constants.Wiki2xhtml.progName + ": " + i18n.tr("Code Output Window"));
			jFc.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			jFc.setResizable(true);
			jFc.setFocusable(false);
			jFc.setBounds(wWidth, 0, screenSize.width - wWidth, wHeight);

			jFc.addWindowListener(new WindowListener() {
				public void windowActivated(WindowEvent e) {
					getJFPaste().setEnabled(true);
					getJFPaste().requestFocus();
					getJtaPaste().requestFocus();
				}

				public void windowClosed(WindowEvent e) {};

				public void windowClosing(WindowEvent e) {
					getJFPaste().dispose();
					jtbCode.setSelected(false);
				}

				public void windowDeactivated(WindowEvent e) {};

				public void windowDeiconified(WindowEvent e) {}

				public void windowIconified(WindowEvent e) {};

				public void windowOpened(WindowEvent e) {}
			});
			jFc.addComponentListener(new ComponentListener() {
				public void componentHidden(ComponentEvent e) {}

				public void componentMoved(ComponentEvent e) {};

				public void componentResized(ComponentEvent e) {
					jtaC.setBounds(0, 0, e.getComponent().getWidth() - 4, e
								   .getComponent().getHeight() - 4);
					jspCode.setBounds(0, 0, jFc.getWidth() - 4, jFc.getHeight() - 4);
				}

				public void componentShown(ComponentEvent e) {};
			});

			Container con = jFc.getContentPane();
			con.setLayout(null);

			/* Add the scroll bar */
			getJtaCode();
			con.add(getJspCode());
			jtaC.setBounds(0, 0, jFc.getWidth() - 4, jFc.getHeight() - 4);
			jspCode.setBounds(0, 0, jFc.getWidth() - 4, jFc.getHeight() - 4);
		}
		return jFc;
	}

	private JComboBox getJcbStyle(int y) {
		if (jcbStyle == null) {
			jcbStyle = new JComboBox();
			jcbStyle.setBounds(firstIndent, y, 220, height);

			jcbStyle.addItem(i18n.tr("random"));

			if (!XHTML.baseStyleDir.exists())
				XHTML.baseStyleDir = MiscWindows.getBaseStyleDir();

			if (XHTML.baseStyleDir != null && XHTML.baseStyleDir.isDirectory()) {
				File[] fl = getDirectoryList(XHTML.baseStyleDir);

				for (File newStyle : fl)
					jcbStyle.addItem(StringTools.quoteIfContainingSpace(newStyle.getName()));

			} else
				jcbStyle.setEnabled(false);


			jcbStyle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					fc.setStyleDirectory(XHTML.baseStyleDir.getPath() + File.separatorChar + jcbStyle.getSelectedItem());
					if (jcbStyle.getSelectedIndex() == 0)
						fc.has.style = false;
					updateSettings();
				}
			});
			jcbStyle.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) { }
				public void focusLost(FocusEvent arg0) {
					fc.setStyleDirectory(XHTML.baseStyleDir.getPath() + File.separatorChar + jcbStyle.getSelectedItem());
					if (jcbStyle.getSelectedIndex() == 0)
						fc.has.style = false;
					updateSettings();
				}
			});
		}
		return jcbStyle;
	}

	private JLabel getLblTitle(int y) {
		if (lblTitle == null) {
			lblTitle = new JLabel(Constants.Wiki2xhtml.progName);
			lblTitle.setFont(new Font(stdFont.getFamily(), Font.BOLD, 14));
			lblTitle.setHorizontalAlignment(JLabel.CENTER);
			lblTitle.setBounds(250, y, 100, height);
			c.add(lblTitle);
		}
		return lblTitle;
	}

	private JLabel getLblTime(int y) {
		if (lblTime == null) {
			lblTime = new JLabel();
			lblTime.setBounds(firstIndent, y, wWidth - 2 * firstIndent, height);
			lblTime.setFont(stdFont);
		}
		return lblTime;
	}

	private JButton getBMenuFile(int y) {
		if (bMenuFile == null) {
			bMenuFile = bi(cr.rOpen, firstIndent + firstWidth, y, 20, height);
			bMenuFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File indFile = FileDialogues.getFile(i18n.tr("Open menu File \u2026"),
														 new File(Constants.Directories.workingDir));
					if (indFile == null)
						;
					else {
						if (indFile != null
						&& indFile.exists()) {
							if (indFile.isFile()) {
								String filename = IOUtils.getShortPath(indFile,
																	   Constants.Directories.workingDir + File.separatorChar);
								fc.setMenuFile(filename);
								o.println(className + String.format(i18n.tr("Menu file is now %s"), filename));
								updateSettings();
							} else
								o.println(className + String.format(i18n.tr("Is not a file: %s"), indFile.getPath()) );
						}
					}
				}

			});
		}
		return bMenuFile;
	}

	private JButton getBCommonFile(int y) {
		if (bCommonFile == null) {
			bCommonFile = bi(cr.rOpen, firstIndent + firstWidth, y, 20, height);

			bCommonFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File indFile = FileDialogues.getFile(i18n.tr("Open title File \u2026"),
														 new File(Constants.Directories.workingDir));
					if (indFile == null)
						;
					else {
						if (indFile != null
						&& indFile.exists()) {
							if (indFile.isFile()) {
								String filename = IOUtils.getShortPath(indFile,
																	   Constants.Directories.workingDir + File.separatorChar);
								fc.setCommonFile(filename);
								o.println(className + String.format(i18n.tr("Title file is now %s"), filename));
								updateSettings();
							} else
								o.println(className + String.format(i18n.tr("Is not a file: %s"), indFile.getPath()) );
						}
					}
				}

			});
		}
		return bCommonFile;
	}

	private JButton getBFooterFile(int y) {
		if (bFooterFile == null) {
			bFooterFile = bi(cr.rOpen, firstIndent + firstWidth, y, 20, height);
			bFooterFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File footFile = FileDialogues.getFile(
										i18n.tr("Open footer file \u2026"), new File(Constants.Directories.workingDir));
					if (footFile == null)
						;
					else {
						if (footFile != null) {
							if (fc.setFooterFile(footFile.toString())) {
								o.println(className + String.format(i18n.tr("Footer file is now %s"), fc.cont.footerFile.getPath()));
							} else
								o.println(className + String.format(i18n.tr("Is not a file: %s"), footFile.getPath()) );
							updateSettings();
						}
					}
				}

			});
		}
		return bFooterFile;
	}

	private JButton getBAddFiles(int y) {
		if (bAddFile == null) {
			bAddFile = bi(cr.rMOpen, firstIndent + firstWidth + 200, y, 20, height);
			bAddFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File[] indFiles = FileDialogues
									  .getFiles(i18n.tr("Open Files to parse \u2026"), new File(Constants.Directories.workingDir), fc
												.fileArray());
					if (indFiles != null && indFiles.length > 0) {
						Args a = fc.allArguments();
						a.append(fc.fileList());
						for (File f: indFiles) {
							if (f.getAbsolutePath().startsWith(Constants.Directories.workingDir + File.separatorChar)) {
								a.add(f.getAbsolutePath().substring(Constants.Directories.workingDir2.length()));
							} else {
								a.add(f.getAbsolutePath());
							}
						}

						Handler_Arguments.handleCommandLineArguments(a, false);

						updateSettings();
					}
				}

			});
			bAddFile.setBackground(Container_Colors.tabBackground);

		}
		return bAddFile;
	}

	private JButton getBTargetDir(int y) {
		if (bTargetDir == null) {
			bTargetDir = new JButton();
			bTargetDir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File dir;
					if (fc.has.target && fc.cont.targetDir().exists())
						dir = fc.cont.targetDir();
					else
						dir = new File(Constants.Directories.workingDir);
					File tdir = FileDialogues.getDirectory(
									i18n.tr("Open Target Directory"), dir);
					if (tdir != null) {
						if (tdir.exists()) {
							if (fc.setTargetDirectory(tdir.getPath())) {
								o.println(className + String.format(i18n.tr("Target directory set: %s"), fc.cont.targetDir().getPath()));
								o
								.println(className
										 + i18n.tr("Note: If you want to create a new directory, add the name in the text field."));
								updateSettings();
							} else
								o.println(className + String.format(i18n.tr("Strange: %s"), tdir.getPath()));
						} else {
							fc.removeTargetDir();
							updateSettings();
						}
					}
				}
			});
			bTargetDir.setIcon(cr.rDir);
			bTargetDir.setBounds(firstIndent + firstWidth + 200, y, height,
								 height);
		}
		return bTargetDir;
	}

	private JButton getBParse(int y) {
		if (bParse == null) {
			bParse = new JButton(i18n.tr("Go!"));
			bParse.setToolTipText(i18n.tr("Parse"));
			bParse.setMnemonic('g');
			if (fc.filesNumber() <= 0)
				bParse.setEnabled(false);
			bParse.setBounds(wWidth/2 - 50, y, 140, height);
			y += height + 5;
			bParse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getJpbP(0).setValue(0);
					if (getJcbStyle(0).getSelectedIndex() == 0)
						// random selected
						fc.has.style = false;
					thread = new Threads();
					thread.start();

					ca.ol(fc.cont.menuFile, CALevel.V_MSG);
					ca.ol(i18n.tr("Tried to process the following expression: ") + fc.allArguments()
						  + fc.fileList(), CALevel.V_MSG);
				}
			});

		}
		return bParse;
	}

	private JButton getBNext(int y) {
		if (bNext == null) {
			bNext = new JButton(i18n.tr("Next"), cr.rNext);
			bNext.setToolTipText(i18n.tr("Next"));
			bNext.setMnemonic('n');
			bNext.setBounds(wWidth/2 - 50, y, 140, height);
			bNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getJTab(0).setSelectedIndex(
						getJTab(0).getSelectedIndex() + 1);
				}

			});
		}
		return bNext;
	}
	private JButton getBNext2(int y) {
		if (bNext2 == null) {
			bNext2 = new JButton(i18n.tr("Next"), cr.rNext);
			bNext2.setToolTipText(i18n.tr("Next"));
			bNext2.setMnemonic('n');
			bNext2.setBounds(wWidth/2 - 50, y, 140, height);
			bNext2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getJTab(0).setSelectedIndex(
						getJTab(1).getSelectedIndex() + 1);
				}

			});
		}
		return bNext2;
	}

	private JButton getBExit(int y) {
		if (bExit == null) {
			bExit = new JButton(i18n.tr("Quit"), cr.rClose);
			bExit.setToolTipText(i18n.tr("Quit") + getCtrlShortcut('q', true, " "));
			bExit.setBounds(wWidth/2 + 100, y, 140, height);
			bExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}

			});
		}
		return bExit;
	}

	private JButton getBSaveSettings(int y) {
		if (bSaveSettings == null) {
			bSaveSettings = new JButton(i18n.tr("Save"), cr.rSaveDisk);
			bSaveSettings.setToolTipText(i18n.tr("Save settings") + getCtrlShortcut('s', true, " "));
			if (fc.filesNumber() <= 0)
				bSaveSettings.setEnabled(false);
			bSaveSettings.setBounds(wWidth/2 - 200, y, 140, height);
			bSaveSettings.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ScriptExport.writeFile(ScriptExport.Filetype.wx);
				}
			});
		}
		return bSaveSettings;
	}

	private JButton getBOpenSettings(int y) {
		if (bOpenSettings == null) {
			bOpenSettings = new JButton(i18n.tr("Open"), cr.rOpen);
			bOpenSettings.setToolTipText(i18n.tr("Open settings file") + getCtrlShortcut('o', true, " "));
			bOpenSettings.setBounds(wWidth/2 - 200, y, 140, height);
			bOpenSettings.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ScriptExport.readFile();
					updateSettings();
				}
			});
		}
		return bOpenSettings;
	}

	/**
	 * @return The button to read code from a file into the Paste Window.
	 */
	private JButton getBOpen() {
		if (bOpen == null) {
			/* Create the button */
			bOpen = bi(cr.rOpen, 10, 10, 25, 25);
			bOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					/*
					 * Opens a file which contains code.
					 */
					File f = IOUtils.openInFileDialog("txt", new File(Constants.Directories.workingDir), null, false);
					if (f != null && f.exists() && f.canRead() && f.isFile()) {
						/*
						 * If the file exists etc.: Use the code
						 */
						boolean okay = true;

						if (getJtaPaste().getText().length() > 0)
							/* Don't overwrite already written text if not wanted */
							if (JOptionPane.showConfirmDialog(null, i18n.tr("Discard changes?")) != JOptionPane.YES_OPTION)
								okay = false;
						if (okay) {
							/* Read the text */
							String s = "ERROR";
							try {
								s = IORead_Stats.readSBuffer(f).toString();
							}
							catch (IOException e) {
								Logger.getInstance().log.append(e);
								e.printStackTrace();
							}

							getJtaPaste().setText(s);
							fc.setPastedText(s);
							getJtaCode().setText(fc.cont.pastedTextCode);
						}
					}
					getJtaPaste().requestFocus();
				}
			});
		}
		return bOpen;
	}

	/**
	 * @return The button to save the content of the Paste Window.
	 */
	private JButton getBSave() {
		if (bSave == null) {
			/* Create the button */
			bSave = bi(cr.rSaveDisk, 40, 10, 25, 25);
			bSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					File f = IOUtils.openOutFileDialog(false, new File(Constants.Directories.workingDir));
					if (f != null && !f.isDirectory()) {
						boolean confirmed = true;
						if (f.exists()) {
							/* Overwrite the old file? */
							if (JOptionPane.showConfirmDialog(null, String.format(i18n.tr("Overwrite file \u201c%s\u201d?"), f.getName()))
									!= JOptionPane.YES_OPTION)
								confirmed = false;
						}
						if (confirmed) {
							/* Write the file */
							try {
								IOWrite_Stats.writeString(f, getJtaPaste().getText().toString(), false);
							}
							catch (IOException e) {
								Logger.getInstance().log.append(e);
								e.printStackTrace();
							}
						}
					}
					getJtaPaste().requestFocus();
				}
			});
		}
		return bSave;
	}

	private JCheckBox getCbHelp(int y, int firstIndent, int firstWidth,
								int lastIndent, int lastWidth) {
		if (cbHelp == null) {
			cbHelp = new JCheckBox(i18n.tr("Help files"));
			cbHelp.setBounds(firstIndent, y, firstWidth, height);
			cbHelp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cbHelp.isSelected()) {
						cbHelp.setSelected(false);
						if (src.Globals.getLocale().equals(new Locale("de")) || src.Globals.getLocale().equals(new Locale("de_CH")))
							Handler_Arguments.handleCommandLineArguments(
								Constants.Arguments.stdArgsHelpDe, false);
						else
							Handler_Arguments.handleCommandLineArguments(
								Constants.Arguments.stdArgsHelpEn, false);
						updateSettings();
					}
				}

			});
			cbHelp.setOpaque(false);
		}
		return cbHelp;
	}

	private JCheckBox getCbStd(int firstIndent, int y, int firstWidth) {
		if (cbStd == null) {
			cbStd = new JCheckBox(i18n.tr("Standard Arguments"));
			cbStd.setBounds(firstIndent, y, firstWidth, height);
			cbStd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cbStd.isSelected()) {
						cbStd.setSelected(false);
						Handler_Arguments.handleCommandLineArguments(
							Constants.Arguments.stdArgs, false);
						updateSettings();
					}
				}
			});
			cbStd.setOpaque(false);

		}
		return cbStd;
	}

	private JCheckBox getCbClear(int firstIndent, int y, int firstWidth) {
		if (cbClear == null) {
			cbClear = new JCheckBox(i18n.tr("Clear Settings"));
			cbClear.setBounds(firstIndent, y, firstWidth, height);
			cbClear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (cbClear.isSelected()) {
						cbClear.setSelected(false);
						Handler_Arguments.handleCommandLineArguments(
							new Args(), false);
						updateSettings();
					}
				}

			});
			cbClear.setOpaque(false);
		}
		return cbClear;
	}

	private JToggleButton getJtbCode(int firstIndent, int firstWidth, int y) {
		if (jtbCode == null) {
			jtbCode = new JToggleButton(i18n.tr("Code Window"));
			jtbCode.setToolTipText(i18n.tr("Open the code window"));

			jtbCode.setBounds(firstIndent, y, firstWidth, height);
			c.add(jtbCode);
			jtbCode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					toggleCodeWindow(jtbCode.isSelected());
				}
			});
			jtbCode.setOpaque(false);
		}
		return jtbCode;
	}
	private void toggleCodeWindow(boolean show) {
		if (show) {
			getJFCode().setVisible(true);
			getJFPaste().setVisible(true);
			getJFPaste().setEnabled(true);
			jtaP.setText(fc.cont.pastedText);
		} else {
			getJFPaste().setVisible(false);
			getJFCode().setVisible(false);
		}
	}

	private JToggleButton getJtbBigfont(int firstIndent, int firstWidth, int y) {
		if (jtbBigfont == null) {
			jtbBigfont = new JToggleButton(i18n.tr("Big Font"));
			jtbBigfont.setToolTipText(i18n.tr("Big font in the GUI"));

			jtbBigfont.setBounds(firstIndent, y, firstWidth, height);
			c.add(jtbBigfont);
			jtbBigfont.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (jtbBigfont.isSelected()) {
						getJtaPaste().setFont(bigFont);
						getJtaCode().setFont(bigFont);
						getJtaPaste().requestFocus();
					} else {
						getJtaPaste().setFont(stdFont);
						getJtaCode().setFont(stdFont);
						getJtaPaste().requestFocus();
					}
				}
			});
			jtbBigfont.setBackground(Container_Colors.tabBackground);
		}
		return jtbBigfont;
	}

	private JToggleButton getCbOnlycode(int y) {
		if (jtbOnlyCode == null) {
			jtbOnlyCode = new JToggleButton(i18n.tr("Only Code"));
			jtbOnlyCode.setBounds(firstIndent, y, firstWidth, height);

			jtbOnlyCode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					fc.setOnlyCode(jtbOnlyCode.isSelected());
					jtaParse.setText(fc.allArguments().getArgs(GetPolicyE.AllArgs) + " " + fc.fileList().getArgs(GetPolicyE.AllArgs));

				}

			});
			jtbOnlyCode.setBackground(Container_Colors.tabBackground);
		}
		return jtbOnlyCode;
	}

	private JCheckBox getCbIncremental(int y) {
		if (cbIncremental == null) {
			cbIncremental = new JCheckBox(ConstantTexts.incrementalMode);
			cbIncremental.setMnemonic('i');
			cbIncremental.setBounds(firstIndent, y, lastWidth, height);
			cbIncremental.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					fc.setIncremental(cbIncremental.isSelected());
					jtaParse.setText(fc.allArguments().getArgs(GetPolicyE.AllArgs) + " " + fc.fileList().getArgs(GetPolicyE.AllArgs));
				}
			});
			cbIncremental.setOpaque(false);
		}
		return cbIncremental;
	}

	private JCheckBox getCbNolinebreaks(int y) {
		if (cbNoLinebreaks == null) {
			cbNoLinebreaks = new JCheckBox(i18n.tr("Remove Linebreaks"));
			cbNoLinebreaks.setBounds(firstIndent, y, firstWidth, height);
			cbNoLinebreaks.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					fc.setNoLinebreaks(cbNoLinebreaks.isSelected());
					jtaParse.setText(fc.allArguments().getArgs(GetPolicyE.AllArgs) + " " + fc.fileList().getArgs(GetPolicyE.AllArgs));

					fc.setPastedText(jtaP.getText());
					getJtaCode().setText(fc.cont.pastedTextCode);
				}

			});
			cbNoLinebreaks.setBackground(Container_Colors.lightGrey);
		}
		return cbNoLinebreaks;
	}

	private JTextField getJtfCommon(int y) {
		if (jtfCommon == null) {
			jtfCommon = new JTextField();

			jtfCommon.setBounds(firstIndent, y, firstWidth, height);
			jtfCommon.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					File f = new File(jtfCommon.getText());
					if (f.exists() && f.isFile()) {
						fc.setCommonFile(jtfCommon.getText());
						ca.ol(i18n.tr("Title file is now") + ": "
							  + jtfCommon.getText(), CALevel.V_MSG);
					} else {
						fc.removeCommon();
						ca.ol(i18n.tr("No title file"), CALevel.V_MSG);
					}
					updateSettings();
				}

			});
			jtfCommon.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) { }
				public void focusLost(FocusEvent arg0) {
					Handler_Arguments.handleCommandLineArguments(
						fc.allArguments().addMulti(jtfFiles.getText()), false);
					updateSettings();
				}
			});
			jtfCommon.addKeyListener(getklEsc());
		}
		return jtfCommon;
	}

	private JTextField getJtfMenu(int y) {
		if (jtfMenu == null) {
			jtfMenu = new JTextField();
			jtfMenu.setBounds(firstIndent, y, firstWidth, height);
			jtfMenu.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					File f = new File(jtfMenu.getText());
					if (f.exists() && f.isFile()) {
						fc.setMenuFile(jtfMenu.getText());
						ca.ol(i18n.tr("Menu file is now") + ": "
							  + jtfMenu.getText(), CALevel.V_MSG);
					} else {
						fc.removeMenu();
						ca.ol(i18n.tr("No menu file"), CALevel.V_MSG);
					}
					updateSettings();
				}

			});
			jtfMenu.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) { }
				public void focusLost(FocusEvent arg0) {
					Handler_Arguments.handleCommandLineArguments(
						fc.allArguments().addMulti(jtfFiles.getText()), false);
					updateSettings();
				}
			});
			jtfMenu.addKeyListener(getklEsc());
		}
		return jtfMenu;
	}

	private Component getJtfFooter(int y) {
		if (jtfFooter == null) {
			jtfFooter = new JTextField();
			jtfFooter.setBounds(firstIndent, y, firstWidth, height);
			jtfFooter.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					if (fc.setFooterFile(jtfFooter.getText()))
						ca.ol(i18n.tr("Footer file is now") + ": "
							  + jtfFooter.getText(), CALevel.V_MSG);
					else {
						fc.removeFooter();
						ca.ol(i18n.tr("No footer file"), CALevel.V_MSG);
					}
					updateSettings();
				}

			});
			jtfFooter.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) { }
				public void focusLost(FocusEvent arg0) {
					Handler_Arguments.handleCommandLineArguments(
						fc.allArguments().addMulti(jtfFiles.getText()), false);
					updateSettings();
				}
			});
			jtfFooter.addKeyListener(getklEsc());
		}
		return jtfFooter;
	}

	private JTextField getJtfFiles(int y) {
		if (jtfFiles == null) {
			jtfFiles = new JTextField();
			jtfFiles.setBounds(firstIndent, y, firstWidth + 200, height);
			jtfFiles.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					Handler_Arguments
					.handleCommandLineArguments(
						fc.allArguments().addMulti(jtfFiles.getText()), false);

					updateSettings();
				}

			});
			jtfFiles.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) { }
				public void focusLost(FocusEvent arg0) {
					Handler_Arguments.handleCommandLineArguments(
						fc.allArguments().addMulti(jtfFiles.getText()), false);
					updateSettings();
				}
			});
			jtfFiles.addKeyListener(getklEsc());
		}
		return jtfFiles;
	}

	private JTextField getJtfTargetDir(int y) {
		if (jtfTargetDir == null) {
			jtfTargetDir = new JTextField();
			jtfTargetDir.setBounds(firstIndent, y, firstWidth + 200, height);
			jtfTargetDir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GuiFunctions.targetDir(jtfTargetDir);
					updateSettings();
				}
			});
			jtfTargetDir.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent arg0) { }
				public void focusLost(FocusEvent arg0) {
					GuiFunctions.targetDir(jtfTargetDir);
					updateSettings();
				}
			});
			jtfTargetDir.addKeyListener(getklEsc());
		}
		return jtfTargetDir;
	}

	private JTextArea getJtaParse(int y) {

		if (jtaParse == null) {
			jtaParse = new JTextArea();
			jtaParse.setEditable(true);
			jtaParse.setBounds(firstIndent, y, wWidth - 2 * firstIndent, 3 * height);
			jtaParse.setLineWrap(true);
			jtaParse.setAutoscrolls(true);
			jtaParse.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent arg0) {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						arg0.setKeyCode(KeyEvent.VK_SHIFT);
						Handler_Arguments.handleCommandLineArguments(
							new Args(Handler_Arguments.handleReplaceArguments(jtaParse.getText())), false);
						jtaParse.setText(fc.allArguments().getArgs(GetPolicyE.AllArgs).append(fc.fileList().getArgs(GetPolicyE.AllArgs)).toString());
						updateSettings();
					}
				}
				public void keyReleased(KeyEvent arg0) {
				}
				public void keyTyped(KeyEvent arg0) {
				}
			});
			jtaParse.addFocusListener(new FocusListener() {

				public void focusGained(FocusEvent arg0) { }

				public void focusLost(FocusEvent arg0) {
					Handler_Arguments.handleCommandLineArguments(
						fc.allArguments().addMulti(jtfFiles.getText()), false);
					updateSettings();
				}

			});
			jtaParse.addKeyListener(getklEsc());
		}

		return jtaParse;
	}

	private JScrollPane getJspPaste() {
		if (jspPaste == null) {
			jspPaste = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			jspPaste.getViewport().setView(getJtaPaste());
		}
		return jspPaste;
	}

	private JScrollPane getJspCode() {
		if (jspCode == null) {
			jspCode = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			jspCode.getViewport().setView(getJtaCode());
		}
		return jspCode;
	}

	private JTextArea getJtaPaste() {
		if (jtaP == null) {
			jtaP = new JTextArea();
			jtaP.setLineWrap(true);
			jtaP.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {
				}

				public void keyReleased(KeyEvent e) {
					if ((int) e.getKeyChar() == 27) {
						getJFPaste().dispose();
						getJFCode().dispose();
						jtbCode.setSelected(false);
					}

					shift = false;
					ctrl = false;
					numpad = e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD;
					int code = e.getKeyCode();

					if ((e.getModifiers() & (InputEvent.SHIFT_DOWN_MASK|InputEvent.SHIFT_MASK)) != 0) {
						shift = true;
					}
					if ((e.getModifiers() & (InputEvent.CTRL_DOWN_MASK|InputEvent.CTRL_MASK)) != 0) {
						ctrl = true;
					}

					if (shift && ctrl) {
						if (numpad) {
							switch (code) {
							case 155:
								unicode += 0;
								break;
							case 35:
								unicode += 1;
								break;
							case 225:
								unicode += 2;
								break;
							case 34:
								unicode += 3;
								break;
							case 226:
								unicode += 4;
								break;
							case 65368:
								unicode += 5;
								break;
							case 227:
								unicode += 6;
								break;
							case 36:
								unicode += 7;
								break;
							case 224:
								unicode += 8;
								break;
							case 33:
								unicode += 9;
								break;
							default:
								break;
							}
						} else
							if (code >= 65 && code <= 70)
								unicode += "ABCDEF".charAt(code - 65);
					} else
						if (unicode.length() > 0) {
							jtaP.setText(jtaP.getText() + (char) Integer.parseInt(unicode, 16));
							unicode = "";
						}

					if (unicode.length() >= 5) {
						jtaP.setText(jtaP.getText() + (char) Integer.parseInt(unicode, 16));
						unicode = "";
					}

					fc.setPastedText(jtaP.getText());

					getJtaCode().setText(fc.cont.pastedTextCode);
				}

				public void keyTyped(KeyEvent e) {
				}
			});
			jtaP.getDocument().addUndoableEditListener(getUndo());
		}
		return jtaP;
	}

	private JTextArea getJtaCode() {
		if (jtaC == null) {
			jtaC = new JTextArea();
			jtaC.setFocusable(true);
			jtaC.setLineWrap(true);
			jtaC.setEditable(false);
			jtaC.addMouseListener(new MouseListener() {
				private Clipboard clip = Toolkit.getDefaultToolkit()
										 .getSystemClipboard();

				public void mouseClicked(MouseEvent e) {
					StringSelection ss = new StringSelection(jtaC.getText());
					ClipboardOwner co = new ClipboardOwner() {
						public void lostOwnership(Clipboard clipboard,
						Transferable contents) {
						}
					};
					clip.setContents(ss, co);
				}

				public void mouseEntered(MouseEvent e) {}

				public void mouseExited(MouseEvent e) {}

				public void mousePressed(MouseEvent e) {}

				public void mouseReleased(MouseEvent e) {}

			});
		}
		return jtaC;
	}

	private ExtendedUndoManager getUndo() {
		if (undo == null) {
			undo = new ExtendedUndoManager();
			undo.setLimit(1000);
		}
		return undo;
	}

	private JProgressBar getJpbP(int y) {
		if (jpbP == null) {
			jpbP = new JProgressBar();
			jpbP.setBounds(wWidth/2 - 200, y, 400, height);
			jpbP.setValue(0);
			jpbP.setString("0 %");
			jpbP.setStringPainted(true);
		}
		return jpbP;
	}

	public JFrame gui(boolean wasRestarted) {
		if (f == null) {
			fc.sc.gui = true;
			height = 20;
			yc = 10;

			fc.currentFilename = "virtualFile.txt";

			i18n.setLocale(Globals.getLocale());

			UIDefaults uiDefaults = UIManager.getDefaults();
			uiDefaults.put("Label.font", stdFont);

			f = new JFrame(Constants.Wiki2xhtml.progName + " " + Constants.Wiki2xhtml.versionNumber);
			f.setIconImage(cr.getIcon());
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(wWidth, wHeight);
			c = f.getContentPane();
			c.setBackground(Container_Colors.lightGrey);
			c.setLayout(null);

			AbstractAction nextTab = new AbstractAction() {
				private static final long serialVersionUID = 1;

				public void actionPerformed(ActionEvent e) {
					if (getJTab(0).getSelectedIndex() >= getJTab(0).getTabCount()-1) {
						getJTab(0).setSelectedIndex(0);
					} else {
						getJTab(0).setSelectedIndex(getJTab(0).getSelectedIndex() + 1);
					}
				}
			};
			AbstractAction prevTab = new AbstractAction() {
				private static final long serialVersionUID = 1;

				public void actionPerformed(ActionEvent e) {
					if (getJTab(0).getSelectedIndex() == 0) {
						getJTab(0).setSelectedIndex(getJTab(0).getTabCount()-1);
					} else {
						getJTab(0).setSelectedIndex(getJTab(0).getSelectedIndex() - 1);
					}
				}
			};

			f.getRootPane().getActionMap().put("nextTab", nextTab);
			f.getRootPane().getActionMap().put("prevTab", prevTab);
			f.getRootPane().getActionMap().put("toggleCode", toggleCode);

			InputMap im = f.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			im.put(Constants.Keys.ctrlNext, "nextTab");
			im.put(Constants.Keys.ctrlPgDown, "nextTab");
			im.put(Constants.Keys.ctrlPrev, "prevTab");
			im.put(Constants.Keys.ctrlPgUp, "prevTab");
			im.put(Constants.Keys.toggleCodeWindow, "toggleCode");


			f.setJMenuBar(gm.getMenuBar(this));

			c.add(getLblTitle(yc));
			yp();
			lbl(
				c,
				i18n.tr(String.format("Please report bugs at %s. Thanks!", "http://forum.granjow.net")),
				100, yc, 600);
			yp();
			yp(10);


			c.add(
				getCbHelp(yc, firstIndent, firstWidth, lastIndent,
						  lastWidth));
			lbl(c, i18n.tr("Standard settings to generate the help files"),
				lastIndent, yc, lastWidth);
			yp();

			c.add(getCbStd(firstIndent, yc, firstWidth));
			lbl(c, i18n.tr("Set the standard settings"), lastIndent, yc,
				lastWidth);
			yp();

			c.add(getCbClear(firstIndent, yc, firstWidth));
			lbl(c, i18n.tr("Clear the settings"), lastIndent, yc, lastWidth);
			yp();

			c.add(getCbNolinebreaks(yc));
			lbl(c, i18n.tr("Remove all Line Breaks"),
				lastIndent, yc, lastWidth);
			yp();
			yp();


			c.add(getJTab(yc));

			height = 20;
			yc = 25;

			getCt1().add(getJtfFiles(yc));
			getCt1().add(getBAddFiles(yc));
			lbl(getCt1(), i18n.tr("Select/Add files"), lastIndent + 220, yc, lastWidth - 200);
			yc += height;
			lbl(
				getCt1(),
				i18n.tr("The files cannot end with .html. Wildcards (e.g. *.txt) are not yet supported."),
				firstIndent, yc, 600);
			yp();

			getCt1().add(getBTargetDir(yc));
			getCt1().add(getJtfTargetDir(yc));

			lbl(getCt1(), i18n.tr("Target directory"), lastIndent + 220, yc, lastWidth);
			yc += height;
			lbl(
				getCt1(),
				i18n.tr("To create a new directory in the text field, simply write down the path."),
				firstIndent, yc, 600);
			yp();
			yp();

			lbl(getCt1(), i18n.tr("Alternatively: Code Windows"), firstIndent, yc, 600);
			yp();
			getCt1().add(getJtbCode(firstIndent, firstWidth, yc));
			getCt1().add(getJtbBigfont(lastIndent, firstWidth, yc));

			getCt1().add(getBOpenSettings(tHeight - 5 * height));
			getCt1().add(getBNext(tHeight - 5 * height));
			yp();

			/*
			 * ********************************************** ****************
			 * ********* Next tab **********************************************
			 * ****************
			 */

			yc = 25;

			getCt2().add(getJtfMenu(yc));
			getCt2().add(getBMenuFile(yc));
			lbl(getCt2(), i18n.tr("Set the menu file"), lastIndent, yc, lastWidth);
			yp();

			getCt2().add(getJtfCommon(yc));
			getCt2().add(getBCommonFile(yc));
			lbl(getCt2(), i18n.tr("Set the common file"), lastIndent, yc, lastWidth);
			yp();

			getCt2().add(getJtfFooter(yc));
			getCt2().add(getBFooterFile(yc));
			lbl(getCt2(), i18n.tr("Set the footer file"), lastIndent, yc, lastWidth);
			yp();

			getCt2().add(getJcbStyle(yc));
			lbl(getCt2(), i18n.tr("Select the design"), lastIndent, yc, lastWidth);
			yp();
			yp();

			lbl(getCt2(), i18n.tr("If you want to give the arguments directly."),
				firstIndent, yc, lastWidth);
			yp();
			getCt2().add(getJtaParse(yc));
			lbl(
				getCt2(),
				i18n.tr("That are all the arguments the program uses. You can change them either here or above."),
				firstIndent, yc, 600);
			yp();
			yp();
			yp();
			yp();

			getCt2().add(getCbOnlycode(yc));
			lbl(
				getCt2(),
				i18n.tr("Write plain code without the reck (no header, CSS etc.; for PN)"),
				lastIndent, yc, lastWidth);
			yp();
			yp();

			yc += 10;
			getCt2().add(getBNext2(tHeight - 5 * height));
			yp();

			/*
			 * ********************************************** ****************
			 * ********* Next tab **********************************************
			 * ****************
			 */
			yc = 25;

			getCt3().add(getCbIncremental(yc));
			yp();
			yp();

			getCt3().add(getJpbP(yc));
			yp();

			getCt3().add(getBSaveSettings(tHeight - 5 * height));
			getCt3().add(getBParse(tHeight - 5 * height));
			getCt3().add(getBExit(tHeight - 5 * height));
			yp();

			getCt3().add(getLblTime(yc));
			yp();

			if (Globals.programSettings.readSettings()) {

				if (!wasRestarted && Globals.programSettings.isTrue( Constants.ProgramSettings.autoUpdate ) && !fc.sc.noUpdateCheck) {
					UpdateChecker.refresh();

					boolean ignore = UpdateChecker.isPropertyEqualTo(
										 Constants.Updater.currentVersion,
										 Globals.programSettings.get(Constants.ProgramSettings.ignoreNewVersion));

					if (!ignore && UpdateChecker.isNewerVersionAvailable()) {
						JOptionPane.showMessageDialog(null, i18n.tr("New version available: {0}\nYou can download it at {1}.\n\n" +
								"If you don't wish to be notified anymore about this version please check the preferences dialog.", 
								UpdateChecker.getProperty(Constants.Updater.currentVersionFullname), Constants.Wiki2xhtml.webpage));
					}
				}

				String args;
				if ((args = Globals.programSettings.settings.get( Constants.ProgramSettings.lastArgs )).length() > 0) {
					Handler_Arguments.handleCommandLineArguments(
						new Args(Handler_Arguments.handleReplaceArguments(args)), false);
					updateSettings();
				}

			}

		}
		return f;
	}

	private void yp() {
		yc += height + 5;
	}
	private void yp(int i) {
		yc += i;
	}

	public void setProgress(int i) {
		getJpbP(0).setValue(i);
		getJpbP(0).setString(i + " %");
	}

	public void setTime(long l) {
		time = StringTools.formatTimeNanoseconds(l, StringTools.Precision.MILLISECOND);
		updateTimerDisplay();
	}

	private File[] getDirectoryList(File f) {
		if (f.exists() && f.isDirectory()) {
			File[] fl = XHTML.baseStyleDir.listFiles(new FilenameFilter() {
				public boolean accept(File f, String arg1) {
					return new File(f.getPath() + File.separatorChar + arg1).isDirectory() && !arg1.equals(".svn");
				}
			});
			return fl;
		} else
			return null;
	}

	/**
	 * @param key
	 * @param brackets
	 * @param pre
	 * @return Translated shortcut combination
	 */
	public String getCtrlShortcut(char key, boolean brackets, String pre) {
		StringBuffer sb = new StringBuffer(pre);
		if (brackets)
			sb.append("(");
		sb.append(i18n.tr("Ctrl") + "-" + key);
		if (brackets)
			sb.append(")");
		return sb.toString();
	}

}
