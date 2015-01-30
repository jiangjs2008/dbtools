package com.dbm.web.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbm.common.log.LoggerWrapper;
import com.dbm.common.property.ConnBean;
import com.dbm.common.property.PropUtil;

/**
 * [name]<br>
 * 配置信息类<br><br>
 * [function]<br>
 * 读取配置信息<br><br>
 * [history]<br>
 * 2012/02/11 ver1.0 JiangJusheng<br>
 */
public class WebPropUtil extends PropUtil {

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
			Resource rs = cnt.getResource("classpath:config.properties");
			is = new FileInputStream(rs.getFile());
			appenv.load(is);
			is.close();

			rs = cnt.getResource("classpath:__driver.properties");
			is = new FileInputStream(rs.getFile());
			p.load(is);
			is.close();

			connList = new ConnBean[p.size()];
			int driverId = 0;
			for (Map.Entry<Object, Object> m : p.entrySet()) {
				driverId = NumberUtils.toInt((String) m.getKey());
				if (driverId == 0) {
					continue;
				}

				ConnBean connBean = new ConnBean();
				connBean.driverid = driverId;

				JSONObject json = JSON.parseObject((String) m.getValue());
				connBean.name = json.getString("name");
				connBean.action = json.getString("action");
				connBean.driver = json.getString("driver");

				connList[connBean.driverid] = connBean;
			}
			p.clear();

			rs = cnt.getResource("message.properties");
			is = new FileInputStream(rs.getFile());
			p.load(is);
			is.close();
			for (Map.Entry<Object, Object> m : p.entrySet()) {
				LoggerWrapper.addMessage(NumberUtils.toInt((String) m.getKey()), (String) m.getValue());
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
