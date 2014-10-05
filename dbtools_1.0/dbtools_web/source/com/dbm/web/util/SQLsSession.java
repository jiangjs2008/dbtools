package com.dbm.web.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;

import com.dbm.common.property.PropUtil;
import com.dbm.common.util.StringUtil;

public class SQLsSession {

	/**
	 * 存放每个用户的操作纪录，纪录内容包括SQL文和最近一次操作时间
	 */
	private static LRULinkedHashMap<String, LRULinkedHashMap<String, String>> _sqlListMap =
		new LRULinkedHashMap<String, LRULinkedHashMap<String, String>>(StringUtil.parseInt(PropUtil.getAppConfig("op.queue.size"), 100));

	public static void saveSQLsSession(String clientId, String sqlStrs) {
		LRULinkedHashMap<String, String> info = _sqlListMap.get(clientId);
		if (info == null) {
			int size = StringUtil.parseInt(PropUtil.getAppConfig("op.cache.size"), 100);
			info = new LRULinkedHashMap<String, String>(size);
			_sqlListMap.put(clientId, info);
		}
		info.put(sqlStrs, null);
	}

	private static Random _rd = new Random();
	/**
	 * 创建客户端识别编号，3位随机数
	 *
	 * @return String 客户端识别编号
	 */
	public static String createOpId() {
		int vs = 0;
		String opId = null;
		int limit = _sqlListMap.getCapacity();
		int len = Integer.toString(limit - 1).length();
		if (_sqlListMap.size() < limit - 1) {
			do {
				vs = _rd.nextInt(limit);
				if (vs == 0) {
					vs = _rd.nextInt(limit);
				}
				opId = StringUtil.addPreZero(vs, len);
			} while (_sqlListMap.get(opId) != null);

			// 必须判断该ID是否被用
			if (_sqlListMap.get(opId) != null) {
				opId = createOpId();
			}

		} else {
			// 超出容量限制时，取最久没有用过的一个旧编号
			opId = _sqlListMap.keySet().iterator().next();
			_sqlListMap.put(opId, null);
		}
		return opId;
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

	public int getCapacity() {
		return capacity;
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
