package com.dbm.client.ui;

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
		 String url = System.getProperty("user.dir") + "/conf/log4j.xml";
		 DOMConfigurator.configure(url);
	 }

	private static SplashScreen ss = null;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandlerFactory.getExceptionHandler());
		Thread.currentThread().setUncaughtExceptionHandler(ExceptionHandlerFactory.getExceptionHandler());

		ss = new SplashScreen("Splash.png");
		ss.showScreen();

		AppPropUtil.load(System.getProperty("user.dir") + "/conf/");

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
