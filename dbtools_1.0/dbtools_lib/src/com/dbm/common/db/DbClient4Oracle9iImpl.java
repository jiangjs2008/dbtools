/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

import java.sql.SQLException;
import java.util.List;

import oracle.sql.Datum;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;


/**
 * 缺省数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4Oracle9iImpl extends DbClient4DefaultImpl {

	@Override
	protected String getLimitString(String tblName, int pageNum) {
		
		String sql = "select rowid rowidex,t.* from " + tblName + " t";
		boolean hasOffset = pageNum > 1;

		final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
		if (hasOffset) {
			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
		} else {
			pagingSelect.append( "select * from ( " );
		}
		pagingSelect.append( sql );
		if (hasOffset) {
			int beg = ( pageNum - 1) * _pageSize;
			int end = ( pageNum ) * _pageSize;
			pagingSelect.append( " ) row_ where rownum <= " );
			pagingSelect.append( end );
			pagingSelect.append( " ) where rownum_ > " );
			pagingSelect.append( beg );

		} else {
			pagingSelect.append( " ) where rownum <= " );
			pagingSelect.append( _pageSize);
		}

		return pagingSelect.toString();
	}

	@Override
	public String procCellData(Object obj) {
		if (obj == null) {
			return "";
		}
		if (obj instanceof STRUCT) {
			// 如果是空间数据类型
			try {
				Object[] sps = ((STRUCT) ((STRUCT) obj).getAttributes()[2]).getAttributes();
				return sps[0].toString() + ", " + sps[1].toString() ;
			} catch (SQLException ex) {
				logger.error(ex);
				return ((STRUCT) obj).debugString();
			}

		} else if (obj instanceof ROWID) {
			return ((ROWID) obj).stringValue();
		} else if (obj instanceof Datum) {
			try {
				return ((Datum) obj).stringValue();
			} catch (SQLException ex) {
				logger.error(ex);
				return "";
			}
		} else {
			return obj.toString();
		}
	}

	/**
	 * 取得DB所属对象一览，如表、视图一览
	 *
	 * @param catalog
	 * @param schemaPattern
	 * @param tableNamePattern
	 * @param types
	 *
	 * @return
	 */
	@Override
	public List<String> getDbObjList(String catalog, String schemaPattern, String tableNamePattern, String[] types) {
		String schema = null;
		try {
			schema = getConnection().getMetaData().getUserName();
		} catch (SQLException ex) {
			logger.error(ex);
		}
		return super.getDbObjList(catalog, schema, tableNamePattern, types);
	}
}