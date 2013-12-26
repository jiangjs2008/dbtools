package com.dbm.client.ui;

import java.util.HashMap;

import javax.swing.JTextField;

import com.dbm.common.util.StringUtil;

/**
 * 画面对象(JButton,JPanel,JTable...)与数据之间的适配器<br>
 *
 */
public class AppUIAdapter {

	private static HashMap<String, Object> uiObjMap = new HashMap<String, Object>();

	/**
	 * Create a new adapter.
	 */
	private AppUIAdapter() {
	}

	public static void setUIObj(String objKey, Object objValue) {
		uiObjMap.put(objKey, objValue);
	}

	public static Object getUIObj(String objKey) {
		return uiObjMap.get(objKey);
	}

	// 主界面
	public final static String AppMainGUI = "AppMainGUI";

	// [更新]按钮
	public final static String BTN_UPDATE = "BTN_UPDATE";
	// [删除]按钮
	public final static String BTN_DELETE = "BTN_DELETE";

	// 数据表示用画面对象
	public final static String TableDataUIObj = "TableDataUIObj";

	// 数据库信息表示用画面对象
	public final static String TableTreeUIObj = "TableTreeUIObj";

	// 数据库URL信息表示用画面对象(TextField)
	public final static String DbUrlTxtField = "DbUrlTxtField";

	// 翻页处理
	public final static String PageAction = "PageAction";
	// 翻页组件
	public final static String PagePanel = "PagePanel";

	/**
	 * 重新设置数据库URL信息<br>
	 * 目前只在访问手机上的sqlite时才使用该方法
	 *
	 * @param targetIpAddr 目标数据库所在服务器IP(如果是目标对象是手机，那么是该手机的IP地址)
	 */
	public static void setGuiDbUrl(String targetIpAddr) {
		JTextField txtField = (JTextField) AppUIAdapter.getUIObj(AppUIAdapter.DbUrlTxtField);
		if (txtField != null) {
			String[] addr = StringUtil.str2Array(txtField.getText(), ":");
			if (addr == null || addr.length == 0 || addr.length == 1) {
				// input error, have not db info
				txtField.setText(targetIpAddr + ":");
			} else {
				txtField.setText(targetIpAddr + ":" + addr[1]);
			}
		}
	}


}

