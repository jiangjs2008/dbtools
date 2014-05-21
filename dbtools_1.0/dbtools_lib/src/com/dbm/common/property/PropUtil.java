package com.dbm.common.property;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbm.common.log.LoggerWrapper;
import com.dbm.common.util.SecuUtil;
import com.dbm.common.util.StringUtil;

/**
 * [name]<br>
 * 配置信息类<br><br>
 * [function]<br>
 * 读取配置信息<br><br>
 * [history]<br>
 * 2012/02/11 ver1.0 JiangJusheng<br>
 */
public class PropUtil {

	/**
	 * instances of the log class
	 */
	protected static LoggerWrapper logger = new LoggerWrapper(PropUtil.class); 

	private static ArrayList<ConnBean> connList = new ArrayList<ConnBean>();
	private static ArrayList<FavrBean> favrList = new ArrayList<FavrBean>();

	private static Properties appenv = new Properties();

	private static Properties favrPrp = new Properties();
	private static Properties drivPrp = new Properties();

	/**
	 * 初期化
	 */
	public static void load() {
		InputStream is = null;
		try {
			is = new FileInputStream(System.getProperty("user.dir") + "/conf/__favrinfo.properties");
			favrPrp.load(is);
			is.close();
			//favrPrp.storeToXML(new FileOutputStream(PropUtil.class.getClassLoader().getResource("_favrinfo.xml").getFile()), "");
			//favrPrp.store(new FileOutputStream(PropUtil.class.getClassLoader().getResource("_favrinfo.p1").getFile()), "");

			is = new FileInputStream(System.getProperty("user.dir") + "/conf/__driver.properties");
			drivPrp.load(is);
			is.close();

			is = new FileInputStream(System.getProperty("user.dir") + "/conf/config.properties");
			appenv.load(is);
			is.close();

			is = new FileInputStream(System.getProperty("user.dir") + "/conf/message.properties");
			Properties p = new Properties();
			p.load(is);
			is.close();

			for (Map.Entry<Object, Object> m : p.entrySet()) {
				LoggerWrapper.addMessage(StringUtil.parseInt((String) m.getKey()), (String) m.getValue());
			}

			for (Map.Entry<Object, Object> m : drivPrp.entrySet()) {

				ConnBean connBean = new ConnBean();
				connBean.driverid = StringUtil.parseInt((String) m.getKey());
				
				JSONObject json = JSON.parseObject((String) m.getValue());
				connBean.dbType = json.getString("name");
				connBean.description = json.getString("description");
				connBean.action = json.getString("action");
				connBean.driver = json.getString("driver");
				connBean.sampleUrl = json.getString("sampleurl");
				connList.add(connBean);
			}

			for (Map.Entry<Object, Object> m : favrPrp.entrySet()) {
				// read the result set
				JSONObject json = JSON.parseObject((String) m.getValue());
				if (json.getIntValue("useflg") == 1) {
					FavrBean favrBean = new FavrBean();
					favrBean.name = json.getString("name");
					favrBean.driverId = json.getIntValue("driverid");
					favrBean.description = json.getString("description");
					favrBean.url = json.getString("url");
					favrBean.user = SecuUtil.decryptBASE64(json.getString("user"));
					favrBean.password = SecuUtil.decryptBASE64(json.getString("password"));
					favrList.add(favrBean);
				}
			}

			logger.debug("app prop file init success");

		} catch (Exception exp) {
			logger.error(exp);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioex) {
					logger.error(ioex);
				}
			}
		}
	}

	/**
	 * 取得最常用数据库列表
	 *
	 * @return ArrayList<FavrBean> 最常用数据库列表
	 */
	public static ArrayList<FavrBean> getFavrInfo() {
		return favrList;
	}

	/**
	 * 根据数据库类型Id取得数据库驱动信息
	 *
	 * @param id 数据库类型Id
	 *
	 * @return ConnBean 数据库驱动信息
	 */
	public static ConnBean getDbConnInfo(int id) {
		for (ConnBean cbInfo : connList) {
			if (cbInfo.driverid == id) {
				return cbInfo;
			}
		}
		return null;
	}

	/**
	 * 取得数据库驱动信息列表
	 *
	 * @return ArrayList<ConnBean> 数据库驱动信息列表
	 */
	public static ArrayList<ConnBean> getDbConnInfo() {
		return connList;
	}

	/**
	 * 取得应用程序设置信息
	 *
	 * @param key 设置信息KEY
	 *
	 * @return String 设置信息
	 */
	public static String getAppConfig(String key) {
		return appenv.getProperty(key);
	}

}
