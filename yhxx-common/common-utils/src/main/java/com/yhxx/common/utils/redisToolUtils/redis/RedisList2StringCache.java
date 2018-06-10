package com.yhxx.common.utils.redisToolUtils.redis;



import com.yhxx.common.utils.jsonToolUtils.JsonUtils;
import com.yhxx.common.utils.redisToolUtils.CachedObject;

import java.util.Arrays;
import java.util.List;

public class RedisList2StringCache<T> extends RedisCache<T> {
	
	private final Class<?> listElementClazz;
	
	@SuppressWarnings("unchecked")
	public RedisList2StringCache(RedisCacheFactory factory,
			String name, String keyPrefix, Class<?> listElementClazz) {
		super(factory, name, keyPrefix, (Class<T>)List.class);
		this.listElementClazz = listElementClazz;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected CachedObject<T> getFromCache(String key) {
		String _key = buildKey(key);
		String value = valueOps.get(_key);
		if(value != null && !"".equals(value.trim())) {
			if(isEmptyValue(value)) {
				return new CachedObject<T>(key).asEmpty();
			} else {
				Object[] array = JsonUtils.parseArrayByElementClass(objectMapper, value, listElementClazz);
				T cacheObj = (T) Arrays.asList(array);
				return new CachedObject<T>(key, cacheObj);
			}
		}
		return new CachedObject<T>(key);
	}

}
