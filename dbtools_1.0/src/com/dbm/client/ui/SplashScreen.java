package com.dbm.client.ui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

public class SplashScreen extends JWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SplashScreen(String image) {
		JLabel lbl = new JLabel(new ImageIcon(getClass().getClassLoader().getResource(image)));
		getContentPane().add(lbl, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}

	public void showScreen() {
		setVisible(true);
	}

	public void close() {
		setVisible(false);
		dispose();
	}
}
