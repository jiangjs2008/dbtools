package com.dbm.client.ui;

import java.net.URL;

import javax.swing.SwingUtilities;

import org.apache.log4j.xml.DOMConfigurator;

import com.dbm.client.error.handler.ExceptionHandlerFactory;
import com.dbm.client.util.AppPropUtil;

/**
 * [name]<br>
 * main frame class<br><br>
 * [function]<br>
 * frame initial, add each component's event action listener<br>
 * menubar initial<br><br>
 * [history]<br>
 * 2013/05/10 ver1.0 JiangJusheng<br>
 */
public class MainApp {

	 static {
		 // log4j初期化
		 final URL url = MainApp.class.getClassLoader().getResource("log4j.xml");  
		 DOMConfigurator.configure(url);
	 }

	private static SplashScreen ss = null;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		ss = new SplashScreen("Splash.png");
		ss.showScreen();

		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandlerFactory.getExceptionHandler());
		Thread.currentThread().setUncaughtExceptionHandler(ExceptionHandlerFactory.getExceptionHandler());
		AppPropUtil.load();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainAppJFrame inst = new MainAppJFrame();
				inst.setLocationRelativeTo(null);
				ss.close();
				inst.setVisible(true);
			}
		});
	}

}
