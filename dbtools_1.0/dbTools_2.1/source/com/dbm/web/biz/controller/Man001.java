package com.dbm.web.biz.controller;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.property.ConnBean;
import com.dbm.common.property.PropUtil;
import com.dbm.common.util.SecuUtil;

/**
 * [name]<br>
 * Man001 Controller<br><br>
 * [function]<br>
 * 数据库登录<br><br>
 * [history]<br>
 * 2014/05/05 ver1.00 JiangJusheng<br>
 */
@Controller
public class Man001 extends DefaultController {


	@RequestMapping(value="/login.do", method=RequestMethod.GET)
	@ResponseBody
	public String login(@RequestParam Map<String,String> requestParam, HttpServletRequest request) {
		JSONObject rsObj = new JSONObject();

		String userId = StringUtils.trimToNull(request.getHeader("s1"));
		String passwd = StringUtils.trimToNull(request.getHeader("s2"));
		String url = StringUtils.trimToNull(requestParam.get("s1"));
		int driverId = NumberUtils.toInt(requestParam.get("s2"));

		if (url == null || userId == null || passwd == null || driverId == 0) {
			rsObj.put("ecd", 1);
			return rsObj.toJSONString();
		}

		HttpSession session = request.getSession();
		session.invalidate();

		// 连接信息
		userId = SecuUtil.decryptBASE64(userId);
		passwd = SecuUtil.decryptBASE64(passwd);
		ConnBean connInfo = PropUtil.getDbConnInfo(driverId);

		// 登陆到数据库
		try {
			DbClient dbClient = DbClientFactory.createDbClient(connInfo.action);
			session.setAttribute("dbclient", dbClient);
			int dataLimit = NumberUtils.toInt(PropUtil.getAppConfig("page.data.count"));
			dbClient.setPageSize(dataLimit);

			if (!dbClient.start(new String[] { connInfo.driver, url, userId, passwd })) {
				rsObj.put("ecd", 2);
				return rsObj.toJSONString();
			}

			try {
				DatabaseMetaData dbMeta = dbClient.getConnection().getMetaData();

				rsObj.put("procInfo", dbMeta.getDatabaseProductName() + " ## " + dbMeta.getDatabaseProductVersion() + " ## " + dbMeta.getDatabaseMajorVersion() + " ## " + dbMeta.getDatabaseMinorVersion());
				rsObj.put("driverInfo", dbMeta.getDriverName() + " ## " + dbMeta.getDriverVersion() + " ## " + dbMeta.getDriverMajorVersion() + " ## " + dbMeta.getDriverMinorVersion());
				rsObj.put("jdbcInfo", dbMeta.getJDBCMajorVersion() + " ## " + dbMeta.getJDBCMinorVersion());

				if (dbMeta.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY)) {
					// 默认的cursor 类型，仅仅支持结果集forward ，不支持backforward ，random ，last，first 等操作。
					rsObj.put("scrollType", ResultSet.TYPE_FORWARD_ONLY);
				} else if (dbMeta.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
					// 支持结果集backforward ，random ，last ，first 等操作，对其它session对数据库中数据做出的更改是不敏感的。
					rsObj.put("scrollType", ResultSet.TYPE_SCROLL_INSENSITIVE);
				} else if (dbMeta.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
					// 支持结果集backforward ，random ，last ，first 等操作，对其它session对数据库中数据做出的更改是敏感的，
					// 即其他session 修改了数据库中的数据，会反应到本结果集中。
					rsObj.put("scrollType", ResultSet.TYPE_SCROLL_SENSITIVE);
				}

				if (dbMeta.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
					rsObj.put("updateType", 1);
				} else if (dbMeta.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
					rsObj.put("updateType", 2);
				} else if (dbMeta.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
					rsObj.put("updateType", 3);
				} else if (dbMeta.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
					rsObj.put("updateType", 4);
				} else if (dbMeta.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
					rsObj.put("updateType", 5);
				} else if (dbMeta.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
					rsObj.put("updateType", 6);
				}

				// 判断ResultSetHoldability需要注意的地方：
				// 1 ：Oracle 只支持HOLD_CURSORS_OVER_COMMIT 。
				// 2 ：当Statement 执行下一个查询，生成第二个ResultSet 时，第一个ResultSet会被关闭，这和是否支持支持HOLD_CURSORS_OVER_COMMIT 无关。
				if (dbMeta.supportsResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT)) {
					// 在事务commit 或rollback 后，ResultSet 仍然可用。
					rsObj.put("commitType", ResultSet.HOLD_CURSORS_OVER_COMMIT);
				} else if (dbMeta.supportsResultSetHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT)) {
					// 在事务commit 或rollback 后，ResultSet 被关闭
					rsObj.put("commitType", ResultSet.CLOSE_CURSORS_AT_COMMIT);
				}
			} catch (Exception exp) {
				logger.error(exp);
			}

			rsObj.put("ecd", 0);
			return rsObj.toJSONString();

		} catch (Exception exp) {
			logger.error("登陆到数据库时发生错误", exp);
			rsObj.put("ecd", 9);
			return rsObj.toJSONString();
		}
	}

	@RequestMapping("/getcatalog.do")
	@ResponseBody
	public String getTableTypes(HttpServletRequest request) {

		JSONObject rsObj = new JSONObject();
		// 显示数据库内容：表、视图等等
		HttpSession session = request.getSession();
		DbClient dbClient = (DbClient) session.getAttribute("dbclient");
		List<String> objList = dbClient.getCatalogList();

		rsObj.put("ecd", 0);
		rsObj.put("dbInfo", objList);
		return rsObj.toJSONString();
	}

	@RequestMapping("/logout.do")
	@ResponseBody
	public String logout(HttpServletRequest request) {
		// 关闭数据库连接
		HttpSession session = request.getSession();
		DbClient dbClient = (DbClient) session.getAttribute("dbclient");
		if (dbClient != null) {
			dbClient.close();
		}
		JSONObject rsObj = new JSONObject();
		rsObj.put("ecd", 0);
		return rsObj.toJSONString();
	}

}
