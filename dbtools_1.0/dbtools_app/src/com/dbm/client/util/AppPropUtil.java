package com.dbm.client.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbm.common.log.LoggerWrapper;
import com.dbm.common.property.ConnBean;
import com.dbm.common.property.FavrBean;
import com.dbm.common.property.PropUtil;
import com.dbm.common.util.StringUtil;

/**
 * [name]<br>
 * 配置信息类<br><br>
 * [function]<br>
 * 读取配置信息<br><br>
 * [history]<br>
 * 2012/02/11 ver1.0 JiangJusheng<br>
 */
public class AppPropUtil extends PropUtil {

	private static final String _cfgPath = System.getProperty("user.dir");

	/**
	 * 初期化
	 */
	public static void load() {
		InputStream is = null;
		try {
			Properties p = new Properties();
			is = new FileInputStream(_cfgPath + "/conf/__favrinfo.properties");
			p.load(is);
			is.close();

			favrList = new FavrBean[p.size()];
			for (Map.Entry<Object, Object> m : p.entrySet()) {

				JSONObject json = JSON.parseObject((String) m.getValue());

				FavrBean favrBean = new FavrBean();
				favrBean.favrId = StringUtil.parseInt((String) m.getKey());

				favrBean.name = json.getString("name");
				favrBean.driverId = json.getIntValue("driverId");
				favrBean.description = json.getString("description");
				favrBean.url = json.getString("url");
				favrBean.user = json.getString("user");
				favrBean.password = json.getString("password");
				favrBean.useFlg = json.getBooleanValue("useFlg");

				favrList[favrBean.favrId] = favrBean;
			}
			p.clear();

			is = new FileInputStream(_cfgPath + "/conf/__driver.properties");
			p.load(is);
			is.close();

			connList = new ConnBean[p.size()];
			int driverId = 0;
			for (Map.Entry<Object, Object> m : p.entrySet()) {
				driverId = StringUtil.parseInt((String) m.getKey());
				if (driverId == 0) {
					continue;
				}

				ConnBean connBean = new ConnBean();
				connBean.driverid = driverId;

				JSONObject json = JSON.parseObject((String) m.getValue());
				connBean.name = json.getString("name");
				connBean.description = json.getString("description");
				connBean.action = json.getString("action");
				connBean.driver = json.getString("driver");
				connBean.sampleUrl = json.getString("sampleurl");
				connList[connBean.driverid] = connBean;
			}
			p.clear();

			is = new FileInputStream(_cfgPath + "/conf/config.properties");
			appenv.load(is);
			is.close();

			is = new FileInputStream(_cfgPath + "/conf/message.properties");
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
	 * 保存快捷方式一览信息
	 */
	public static void saveFavrInfo() {
		OutputStream out = null;
		try {
			Properties p = new Properties();
			for (FavrBean favrBean : favrList) {
				String json = JSON.toJSONString(favrBean);
				p.put(Integer.toString(favrBean.favrId), json);
			}

			out = new FileOutputStream(_cfgPath + "/conf/__favrinfo.properties");
			p.store(out, "");

		} catch (Exception exp) {
			logger.error(exp);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ioex) {
					logger.error(ioex);
				}
			}
		}
	}

	/**
	 * 保存数据库jdbc驱动信息
	 */
	public static void saveConnInfo() {
		OutputStream out = null;
		try {
			Properties p = new Properties();
			for (ConnBean connBean : connList) {
				String json = JSON.toJSONString(connBean);
				p.put(Integer.toString(connBean.driverid), json);
			}

			out = new FileOutputStream(_cfgPath + "/conf/__driver.properties");
			p.store(out, "");

		} catch (Exception exp) {
			logger.error(exp);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ioex) {
					logger.error(ioex);
				}
			}
		}
	}
}
