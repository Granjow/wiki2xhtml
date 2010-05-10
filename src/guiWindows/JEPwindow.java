package src.guiWindows;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import src.Constants;
import src.Container_Resources;


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
 * A window that contains an HTML page
 *
 * @author Simon Eugster
 */
public final class JEPwindow {

	private JFrame jf = null;
	public final Dimension screenSize = Toolkit.getDefaultToolkit()	.getScreenSize();


	public JEPwindow() { }
	public JEPwindow(final String title, final URL url) {
		jf = bla(title, 400, 500, url);
	}
	public JEPwindow(final String title, final int width, final int height, final URL url)
	{
		jf = bla(title, width, height, url);
	}

	public JFrame bla(final String title, final int width, final int height, final URL url) {
		jf = new JFrame(title);
		jf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		jf.setResizable(true);
		jf.setFocusable(true);
		jf.setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
		jf.setIconImage(Container_Resources.getInstance().getIcon());

		AbstractAction closeAction = new AbstractAction() {

			private static final long serialVersionUID = 4083726891748513328L;

			public void actionPerformed(ActionEvent e) {
				jf.setVisible(false);
			}

		};
		jf.getRootPane().getActionMap().put("close", closeAction);
		InputMap im = jf.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		im.put(Constants.Keys.quit, "close");
		im.put(Constants.Keys.close, "close");
		im.put(Constants.Keys.esc, "close");


		JEditorPane jl = new JEditorPane();
		jl.setEditable(false);
		jl.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent arg0) {
				if (arg0.getEventType().equals( HyperlinkEvent.EventType.ACTIVATED )) {
					/* User has clicked a link */

					// TODO 4 links ...
//					System.out.println(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE));
//
//					try {
//						Desktop.getDesktop().browse(new URI(arg0.getURL().toString()));
//					} catch (IOException e) {
////							e.printStackTrace();
//					} catch (URISyntaxException e) {
////							e.printStackTrace();
//					}
				}

			}
		});

		try {
			jl.setPage(url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		jl.setFocusable(true);

		JScrollPane sp = new JScrollPane(jl);
		sp.setBounds(0, 0, width, height);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setPreferredSize(new Dimension(width, height));
		sp.setMinimumSize(new Dimension(10, 10));

		jf.add(sp);
		jf.setVisible(true);

		return jf;

	}

	public void show() {
		jf.setVisible(true);
	}
}
