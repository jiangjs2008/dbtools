package com.dbm.web.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.dbm.common.property.FavrBean;
import com.dbm.common.property.PropUtil;

public class SQLsSession {

	private static FavrBean fvrinfoItem = null;

	public static FavrBean getCurrFavrInfo() {
		return fvrinfoItem;
	}

	public static void setCurrFavrInfo(FavrBean fvrInfo) {
		fvrinfoItem = fvrInfo;
	}

	private final static int _queueSize = NumberUtils.toInt(PropUtil.getAppConfig("op.queue.size"), 100);
	private final static int _cacheSize = NumberUtils.toInt(PropUtil.getAppConfig("op.cache.size"), 100);

	/**
	 * 存放客户端识别编号
	 */
	private static LRULinkedHashMap<String, String> _opidMap = new LRULinkedHashMap<String, String>(_queueSize);

	/**
	 * 存放每个用户的操作纪录，纪录内容包括SQL文和最近一次操作时间
	 */
	private static HashMap<MultiKey<String>, LRULinkedHashMap<String, String>> _sqlListMap =
		new HashMap<MultiKey<String>, LRULinkedHashMap<String, String>>(_queueSize);

	/**t
	 * 保存用户的操作纪录
	 *
	 * @param clientId 客户端识别编号
	 * @param favrId   数据库识别编号
	 * @param sqlStrs  SQL文
	 */
	public static void saveSQLsSession(String clientId, String favrId, String sqlStrs) {
		_opidMap.get(clientId);
		MultiKey<String> key = new MultiKey<String>(clientId, favrId);
		LRULinkedHashMap<String, String> info = _sqlListMap.get(key);
		if (info == null) {
			info = new LRULinkedHashMap<String, String>(_cacheSize);
			_sqlListMap.put(key, info);
			_opidMap.put(clientId, clientId);
		}
		info.put(sqlStrs, DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss SSS"));
	}

	/**
	 * 获取用户的操作纪录
	 *
	 * @param clientId 客户端识别编号
	 *
	 * @return Map<String, String> 用户的操作纪录(SQL文, 执行时间)
	 */
	public static Map<String, String> getSqlHis(String clientId, String favrId) {
		_opidMap.get(clientId);
		MultiKey<String> key = new MultiKey<String>(clientId, favrId);
		return	_sqlListMap.get(key);
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
		MultiKey<String> key = null;

		int len = Integer.toString(_cacheSize - 1).length();
		if (_sqlListMap.size() < _cacheSize - 1) {
			do {
				vs = _rd.nextInt(_cacheSize);
				if (vs == 0) {
					vs = _rd.nextInt(_cacheSize);
				}
				opId = StringUtil.addPreZero(vs, len);

				// 必须判断该ID是否被用
			} while (_opidMap.get(opId) != null);

		} else {
			// 超出容量限制时，取最久没有用过的一个旧编号
			key = _sqlListMap.keySet().iterator().next();
			_sqlListMap.put(key, null);
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

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A <code>MultiKey</code> allows multiple map keys to be merged together.
 * <p>
 * The purpose of this class is to avoid the need to write code to handle
 * maps of maps. An example might be the need to look up a file name by
 * key and locale. The typical solution might be nested maps. This class
 * can be used instead by creating an instance passing in the key and locale.
 * <p>
 * Example usage:
 * <pre>
 * // populate map with data mapping key+locale to localizedText
 * Map map = new HashMap();
 * MultiKey multiKey = new MultiKey(key, locale);
 * map.put(multiKey, localizedText);
 *
 * // later retrieve the localized text
 * MultiKey multiKey = new MultiKey(key, locale);
 * String localizedText = (String) map.get(multiKey);
 * </pre>
 *
 * @since 3.0
 * @version $Id: MultiKey.java 1477753 2013-04-30 18:24:24Z tn $
 */
class MultiKey<K> implements Serializable {
    // This class could implement List, but that would confuse it's purpose

    /** Serialisation version */
    private static final long serialVersionUID = 4465448607415788805L;

    /** The individual keys */
    private final K[] keys;
    /** The cached hashCode */
    private transient int hashCode;

    /**
     * Constructor taking two keys.
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the MultiKey.
     *
     * @param key1  the first key
     * @param key2  the second key
     */
    @SuppressWarnings("unchecked")
    public MultiKey(final K key1, final K key2) {
        this((K[]) new Object[] { key1, key2 }, false);
    }

    /**
     * Constructor taking an array of keys, optionally choosing whether to clone.
     * <p>
     * <b>If the array is not cloned, then it must not be modified.</b>
     * <p>
     * This method is public for performance reasons only, to avoid a clone.
     * The hashcode is calculated once here in this method.
     * Therefore, changing the array passed in would not change the hashcode but
     * would change the equals method, which is a bug.
     * <p>
     * This is the only fully safe usage of this constructor, as the object array
     * is never made available in a variable:
     * <pre>
     * new MultiKey(new Object[] {...}, false);
     * </pre>
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the MultiKey.
     *
     * @param keys  the array of keys, not null
     * @param makeClone  true to clone the array, false to assign it
     * @throws IllegalArgumentException if the key array is null
     * @since 3.1
     */
    private MultiKey(final K[] keys, final boolean makeClone) {
        if (keys == null) {
            throw new IllegalArgumentException("The array of keys must not be null");
        }
        if (makeClone) {
            this.keys = keys.clone();
        } else {
            this.keys = keys;
        }

        calculateHashCode(keys);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a clone of the array of keys.
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed.
     *
     * @return the individual keys
     */
    public K[] getKeys() {
        return keys.clone();
    }

    /**
     * Gets the key at the specified index.
     * <p>
     * The key should be immutable.
     * If it is not then it must not be changed.
     *
     * @param index  the index to retrieve
     * @return the key at the index
     * @throws IndexOutOfBoundsException if the index is invalid
     * @since 3.1
     */
    public K getKey(final int index) {
        return keys[index];
    }

    /**
     * Gets the size of the list of keys.
     *
     * @return the size of the list of keys
     * @since 3.1
     */
    public int size() {
        return keys.length;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this object to another.
     * <p>
     * To be equal, the other object must be a <code>MultiKey</code> with the
     * same number of keys which are also equal.
     *
     * @param other  the other object to compare to
     * @return true if equal
     */
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof MultiKey) {
            final MultiKey<?> otherMulti = (MultiKey<?>) other;
            return Arrays.equals(keys, otherMulti.keys);
        }
        return false;
    }

    /**
     * Gets the combined hash code that is computed from all the keys.
     * <p>
     * This value is computed once and then cached, so elements should not
     * change their hash codes once created (note that this is the same
     * constraint that would be used if the individual keys elements were
     * themselves {@link java.util.Map Map} keys.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Gets a debugging string version of the key.
     *
     * @return a debugging string
     */
    @Override
    public String toString() {
        return "MultiKey" + Arrays.toString(keys);
    }

    /**
     * Calculate the hash code of the instance using the provided keys.
     *
     * @param keys the keys to calculate the hash code for
     */
	private void calculateHashCode(final Object[] keys) {
		int total = 0;
		for (final Object key : keys) {
			if (key != null) {
				total ^= key.hashCode();
			}
		}
		hashCode = total;
	}

    /**
     * Recalculate the hash code after deserialization. The hash code of some
     * keys might have change (hash codes based on the system hash code are
     * only stable for the same process).
     *
     * @return the instance with recalculated hash code
     */
	private Object readResolve() {
		calculateHashCode(keys);
		return this;
	}
}
