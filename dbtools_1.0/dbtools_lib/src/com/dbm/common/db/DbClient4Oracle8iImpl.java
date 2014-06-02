/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;


/**
 * 缺省数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4Oracle8iImpl extends DbClient4DefaultImpl {

	@Override
	protected String getLimitString(String sql, int pageNum) {
		boolean hasOffset = pageNum == 1;

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
			pagingSelect.append( " ) row_ ) where rownum_ <= " );
			pagingSelect.append( end );
			pagingSelect.append( " and rownum_ > " );
			pagingSelect.append( beg );

		} else {
			pagingSelect.append( " ) where rownum <= " );
			pagingSelect.append( _pageSize);
		}

		return pagingSelect.toString();
	}

}
