package com.yhxx.common.utils.redisToolUtils;

import org.springframework.util.StringUtils;

/**
 * 缓存代理对象，封装实际的缓存对象。
 * 
 * @author zsp
 *
 * @param <T>
 */
public final class CachedObject<T> {
	
	private final String key;
	private boolean empty;
	private T data;
	
	/**
	 * 
	 * @param key
	 */
	public CachedObject(String key) {
		if(!StringUtils.hasText(key)) {
			throw new IllegalArgumentException();
		}
		this.key = key;
	}
	
	/**
	 * 
	 * @param key
	 * @param data
	 */
	public CachedObject(String key, T data) {
		this(key);
		this.data = data;
	}
	
	/**
	 * 缓存键
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 作为空值缓存，用于防止缓存击穿
	 * 
	 * @return
	 */
	public CachedObject<T> asEmpty() {
		this.empty = true;
		return this;
	}
	
	/**
	 * 获取实际的缓存对象
	 * 
	 * @return
	 */
	public T getData() {
		return data;
	}
	
	/**
	 * 判断是否是空值缓存
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return empty;
	}
	
	/**
	 * 判断实际缓存的对象是否为null
	 * @return
	 */
	public boolean isNull() {
		return !empty && data == null;
	}

}
