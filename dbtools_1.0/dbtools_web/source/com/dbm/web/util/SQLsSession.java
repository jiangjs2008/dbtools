package com.dbm.web.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class SQLsSession {

	private static LRULinkedHashMap<String, LRULinkedHashMap<String, String>>
		_sqlListMap = new LRULinkedHashMap<String, LRULinkedHashMap<String, String>>(1000);

	public static void saveSQLsSession(String clientId, String sqlStrs) {
		LRULinkedHashMap<String, String> info = _sqlListMap.get(clientId);
		if (info == null) {
			info = new LRULinkedHashMap<String, String>(50);
			_sqlListMap.put(clientId, info);
		}
		info.put(sqlStrs, null);
	}

}

class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	/** serialVersionUID */
	private static final long serialVersionUID = -5933045562735378538L;

	/** 存储数据容量 */
	private int capacity = 0;

	/**
	 * 带参数构造方法
	 *
	 * @param lruCapacity     lru存储数据容量
	 */
	public LRULinkedHashMap(int lruCapacity) {
		super(lruCapacity);
		this.capacity = lruCapacity;
	}

	/**
	 * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
	 */
	@Override
	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		if (size() > capacity) {
			return true;
		}
		return false;
	}
}
