package com.dbm.common.property;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbm.common.log.LoggerWrapper;
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
	protected static Logger logger = Logger.getLogger(PropUtil.class); 

	private static ConnBean[] connList = null;
	private static FavrBean[] favrList = null;

	private static Properties appenv = new Properties();

	/**
	 * 初期化
	 * 注* 在Jboss AS 7.1环境下只能使用Spring提供的ClassPathXmlApplicationContext获取property属性文件的路径
	 *     直接用xxx.class.getResource()或ResourceBundle都不能取得属性文件的路径
	 */
	public static void load() {
		ClassPathXmlApplicationContext cnt = new ClassPathXmlApplicationContext();
		InputStream is = null;
		try {
			Properties p = new Properties();
			Resource rs = cnt.getResource("classpath:__favrinfo.properties");
			is = new FileInputStream(rs.getFile());
			p.load(is);
			is.close();

			favrList = new FavrBean[p.size()];
			for (Map.Entry<Object, Object> m : p.entrySet()) {

				JSONObject json = JSON.parseObject((String) m.getValue());

				FavrBean favrBean = new FavrBean();
				favrBean.favrId = StringUtil.parseInt((String) m.getKey());
				favrBean.name = json.getString("name");
				favrBean.driverId = json.getIntValue("driverid");
				favrBean.description = json.getString("description");
				favrBean.url = json.getString("url");
				favrBean.user = json.getString("user");
				favrBean.password = json.getString("password");
				favrBean.useFlg = StringUtil.parseInt(json.getString("useflg"), 0) == 1;

				favrList[favrBean.favrId] = favrBean;
			}
			p.clear();
			//favrPrp.storeToXML(new FileOutputStream(PropUtil.class.getClassLoader().getResource("_favrinfo.xml").getFile()), "");
			//favrPrp.store(new FileOutputStream(PropUtil.class.getClassLoader().getResource("_favrinfo.p1").getFile()), "");

			rs = cnt.getResource("classpath:__driver.properties");
			is = new FileInputStream(rs.getFile());
			p.load(is);
			is.close();

			connList = new ConnBean[p.size()];
			for (Map.Entry<Object, Object> m : p.entrySet()) {

				ConnBean connBean = new ConnBean();
				connBean.driverid = StringUtil.parseInt((String) m.getKey());

				JSONObject json = JSON.parseObject((String) m.getValue());
				connBean.name = json.getString("name");
				connBean.description = json.getString("description");
				connBean.action = json.getString("action");
				connBean.driver = json.getString("driver");
				connBean.sampleUrl = json.getString("sampleurl");

				connList[connBean.driverid] = connBean;
			}
			p.clear();

			rs = cnt.getResource("classpath:config.properties");
			is = new FileInputStream(rs.getFile());
			appenv.load(is);
			is.close();

			rs = cnt.getResource("message.properties");
			is = new FileInputStream(rs.getFile());
			p.load(is);
			is.close();
			for (Map.Entry<Object, Object> m : p.entrySet()) {
				LoggerWrapper.addMessage(StringUtil.parseInt((String) m.getKey()), (String) m.getValue());
			}
			p.clear();
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
	 * 初期化
	 */
	public static void load(String cfgPath) {
		InputStream is = null;
		try {
			Properties p = new Properties();
			is = new FileInputStream(cfgPath + "__favrinfo.properties");
			p.load(is);
			is.close();

			favrList = new FavrBean[p.size()];
			for (Map.Entry<Object, Object> m : p.entrySet()) {

				JSONObject json = JSON.parseObject((String) m.getValue());

				FavrBean favrBean = new FavrBean();
				favrBean.favrId = StringUtil.parseInt((String) m.getKey());

				favrBean.name = json.getString("name");
				favrBean.driverId = json.getIntValue("driverid");
				favrBean.description = json.getString("description");
				favrBean.url = json.getString("url");
				favrBean.user = json.getString("user");
				favrBean.password = json.getString("password");
				favrBean.useFlg = StringUtil.parseInt(json.getString("useflg"), 0) == 1;
			
				favrList[favrBean.favrId] = favrBean;
			}
			p.clear();
			//favrPrp.storeToXML(new FileOutputStream(PropUtil.class.getClassLoader().getResource("_favrinfo.xml").getFile()), "");
			//favrPrp.store(new FileOutputStream(PropUtil.class.getClassLoader().getResource("_favrinfo.p1").getFile()), "");

			is = new FileInputStream(cfgPath + "__driver.properties");
			p.load(is);
			is.close();

			connList = new ConnBean[p.size()];
			for (Map.Entry<Object, Object> m : p.entrySet()) {

				ConnBean connBean = new ConnBean();
				connBean.driverid = StringUtil.parseInt((String) m.getKey());

				JSONObject json = JSON.parseObject((String) m.getValue());
				connBean.name = json.getString("name");
				connBean.description = json.getString("description");
				connBean.action = json.getString("action");
				connBean.driver = json.getString("driver");
				connBean.sampleUrl = json.getString("sampleurl");
				connList[connBean.driverid] = connBean;
			}
			p.clear();

			is = new FileInputStream(cfgPath + "config.properties");
			appenv.load(is);
			is.close();

			is = new FileInputStream(cfgPath + "message.properties");
			p.load(is);
			is.close();
			for (Map.Entry<Object, Object> m : p.entrySet()) {
				LoggerWrapper.addMessage(StringUtil.parseInt((String) m.getKey()), (String) m.getValue());
			}
			p.clear();
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
	 * 取得制定常用数据库信息
	 *
	 * @param id 数据库类型Id
	 *
	 * @return FavrBean 常用数据库信息
	 */
	public static FavrBean getFavrInfo(int id) {
		return favrList[id];
	}

	/**
	 * 取得最常用数据库列表
	 *
	 * @return FavrBean[] 最常用数据库列表
	 */
	public static FavrBean[] getFavrInfo() {
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
		return connList[id];
	}

	/**
	 * 取得数据库驱动信息列表
	 *
	 * @return ConnBean[] 数据库驱动信息列表
	 */
	public static ConnBean[] getDbConnInfo() {
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
