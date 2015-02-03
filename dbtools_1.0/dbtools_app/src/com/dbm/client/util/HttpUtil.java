package com.dbm.client.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dbm.client.util.HttpUtil;



public class HttpUtil {

	/**
	 * instances of the log class
	 */
	private static Logger logger = Logger.getLogger(HttpUtil.class);

	/**
	 * 从输入的数据流中得到JSON字符串<br>
	 * 不能取到时返回Null
	 * 
	 * @param request Http请求对象
	 * 
	 * @return String JSON字符串
	 */
	private static String getBodyFromRequest(HttpServletRequest request) {
		ServletInputStream input = null;
		ByteArrayOutputStream outStream = null;
		String inputstr = null;
		try {
			input = request.getInputStream();
			outStream = new ByteArrayOutputStream();
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = input.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			inputstr = new String(outStream.toByteArray(), "utf-8");

		} catch (IOException ioexp) {
			logger.error("解析Http请求时异常", ioexp);
			return null;
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException ioexp) {
					logger.error(ioexp.getMessage());
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException ioexp) {
					logger.error(ioexp.getMessage());
				}
			}
		}

		if (StringUtils.isBlank(inputstr)) {
			logger.error("无应答内容");
			return null;
		}
		return inputstr;
	}

	/**
	 * 从输入的数据流中得到指定的JSON对象<br>
	 * 不能取到时返回Null<br>
	 * 注意，此方法只能获取以HTTP POST方式提交的请求数据
	 * 
	 * @param request Http请求对象
	 *
	 * @return JSONObject JSON对象
	 */
	public static JSONObject getJsonObjFromRequest(HttpServletRequest request) {
		String inputstr = getBodyFromRequest(request);
		if (inputstr == null) {
			return null;
		}
		try {
			return JSON.parseObject(inputstr);
		} catch (JSONException jxp) {
			logger.error(jxp.getMessage() + "输入数据："  + inputstr);
			return null;
		}
	}

	/**
	 * 从输入的数据流中得到指定的JSON数组<br>
	 * 不能取到时返回Null<br>
	 * 注意，此方法只能获取以HTTP POST方式提交的请求数据
	 * 
	 * @param request Http请求对象
	 *
	 * @return JSONArray JSON数组
	 */
	public static JSONArray getJsonArrayFromRequest(HttpServletRequest request) {
		String inputstr = getBodyFromRequest(request);
		if (inputstr == null) {
			return null;
		}
		try {
			return JSON.parseArray(inputstr);
		} catch (JSONException jxp) {
			logger.error(jxp.getMessage() + "输入数据："  + inputstr);
			return null;
		}
	}

	/**
	 * 发送普通HTTP POST请求
	 *
	 * @param webUrl HTTP请求URL
	 * @param obj 请求参数
	 *
	 * @return String 请求结果
	 */
	public static String getBody4Post(String webUrl, JSONObject obj) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = null;
		try {
			httppost = new HttpPost(webUrl);

			// 设置连接超时
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httppost.setConfig(requestConfig);

			if (obj != null) {
				httppost.setEntity(new StringEntity(obj.toString(), "utf-8"));
			}

			HttpResponse response = httpclient.execute(httppost);
			// 检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String rev = EntityUtils.toString(response.getEntity());
				return rev;
			} else {
				logger.error("发送http post 请求时异常: " + webUrl + " 参数 " + obj.toJSONString() + "http 响应码: " + code);
				return null;
			}

		} catch (Exception e) {
			logger.error("发送http post 请求时异常: " + webUrl + " 参数 " + obj.toJSONString(), e);
			return null;
		} finally {
			if (httppost != null) {
				httppost.releaseConnection();
			}
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException ioexp) {
					logger.error("关闭http get连接时异常: " + webUrl, ioexp);
				}
			}
		}
	}

	/**
	 * 发送普通HTTP GET请求
	 *
	 * @param webUrl HTTP请求URL
	 *
	 * @return String 请求结果
	 */
	public static String getBody4Get(String webUrl) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = null;
		try {

			httpGet = new HttpGet(webUrl);

			// 设置连接超时
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpGet.setConfig(requestConfig);

			HttpResponse response = httpclient.execute(httpGet);
			// 检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String rev = EntityUtils.toString(response.getEntity());
				return rev;
			} else {
				logger.error("发送http get 请求时异常: " + webUrl + "http 响应码: " + code);
				return null;
			}

		} catch (Exception e) {
			logger.error("发送http get 请求时异常: " + webUrl, e);
			return null;
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException ioexp) {
					logger.error("关闭http get连接时异常: " + webUrl, ioexp);
				}
			}
		}
	}

}
