package com.yhxx.common.utils.redisToolUtils.injvm;


import com.yhxx.common.utils.redisToolUtils.CachedObject;
import com.yhxx.common.utils.redisToolUtils.GenericCacheFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InjvmCache<T> extends InjvmAbstractCache<T> {

	private final Map<String, Object> cache = new ConcurrentHashMap<String, Object>();

	public InjvmCache(GenericCacheFactory factory, String name) {
		super(factory, name);
	}

	@Override
	protected void addToCache(String key, T value) {
		cache.put(key, value);
	}

	@Override
	protected void addToCache(Map<String, T> values) {
		cache.putAll(values);
	}

	@Override
	protected void addToCache(Map<String, T> values, int expiredSeconds) {
		throw new UnsupportedOperationException("cannot supported expiredSeconds!");
	}

	@Override
	protected void addToCache(String key, T value, int expiredSeconds) {
		throw new UnsupportedOperationException("cannot supported expiredSeconds!");
	}

	@Override
	protected CachedObject<T> getFromCache(String key) {
		return asCachedObject(key, cache.get(key), null);
	}
	
	@Override
	protected List<CachedObject<T>> getFromCache(Collection<String> keys) {
		List<CachedObject<T>> list = new ArrayList<CachedObject<T>>();
		for(String key : keys) {
			list.add(asCachedObject(key, cache.get(key), null));
		}
		return list;
	}

	@Override
	protected void removeFromCache(String key) {
		cache.remove(key);
	}
	
	@Override
	protected void removeFromCache(Collection<String> keys) {
		for(String key : keys) {
			cache.remove(key);
		}
	}

	@Override
	protected void clearCache() {
		cache.clear();
	}
	
	@Override
	protected void addEmptyToCache(String key) {
		cache.put(key, EMPTY_VALUE);
	}

	@Override
	protected <V> boolean isEmptyValue(V value) {
		return EMPTY_VALUE.equals(value);
	}
	
}
