package com.dbm.client.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

}
