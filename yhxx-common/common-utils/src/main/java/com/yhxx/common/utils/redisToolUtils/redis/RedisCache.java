package com.yhxx.common.utils.redisToolUtils.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhxx.common.utils.jsonToolUtils.JsonUtils;
import com.yhxx.common.utils.redisToolUtils.CachedObject;
import com.yhxx.common.utils.redisToolUtils.util.KeyBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 基于Redis的String数据结构实现的缓存
 * 
 * @author zsp
 *
 * @param <T>
 */
public class RedisCache<T> extends RedisAbstractCache<T> {

	protected ObjectMapper objectMapper = new ObjectMapper();
	protected final Class<T> clazz;
	private final Function<String, T> objectConverter;
	
	public RedisCache(RedisCacheFactory factory,
			String name, String keyPrefix, Class<T> clazz) {
		super(factory, name, keyPrefix);
		this.clazz = clazz;
		this.objectConverter = (e) -> {
			return JsonUtils.parse(objectMapper, e, clazz);
		};
	}

	public RedisCache(RedisCacheFactory factory,
					  String name, String keyPrefix, Class<T> clazz, Class... parameterClasses) {
		super(factory, name, keyPrefix);
		this.clazz = clazz;
		this.objectConverter = (e) -> {
			return JsonUtils.parse(objectMapper, e, JsonUtils.constructParametricType(clazz, parameterClasses));
		};
	}

	@Override
	protected void addToCache(String key, T value) {
		addToCache(key, value, getExpiredSeconds());
	}
	
	@Override
	protected void addToCache(Map<String, T> values) {
		addToCache(values, getExpiredSeconds());
	}

	@Override
	protected void addToCache(String key, T value, int expiredSeconds) {
		if(value != null) {
			String _key = buildKey(key);
			String _value = JsonUtils.toJson(objectMapper, value);
			int seconds = expiredSeconds;
			if(seconds > 0) {
				valueOps.set(_key, _value, seconds, TimeUnit.SECONDS);
			} else {
				valueOps.set(_key, _value);
			}
		}
	}

	@Override
	protected void addToCache(Map<String, T> values, int expiredSeconds) {
		if(values != null && values.size() > 0) {
			List<String> keys = new ArrayList<String>();
			Map<String, String> map = new HashMap<String, String>();
			for(Map.Entry<String, T> item : values.entrySet()) {
				keys.add(item.getKey());
				map.put(buildKey(item.getKey()),
						JsonUtils.toJson(objectMapper, item.getValue()));
			}
			valueOps.multiSet(map);
			int seconds = expiredSeconds;
			if(seconds > 0) {
				expire(keys, seconds);
			}
		}
	}

	
	@Override
	protected CachedObject<T> getFromCache(String key) {
		return asCachedObject(key, valueOps.get(buildKey(key)), objectConverter);
	}
	
	@Override
	protected List<CachedObject<T>> getFromCache(Collection<String> keys) {
		List<CachedObject<T>> list = new ArrayList<CachedObject<T>>();
		if(keyMapper != null) {
			List<String> _keys = keys.stream().map((e)->buildKey(e)).collect(Collectors.toList());
			List<String> jsonList = valueOps.multiGet(_keys);
			if(jsonList != null && jsonList.size() > 0) {
				/*
				 * 因为无法从null或空值中分析出缓存键的信息，因此只能先从缓存返回的结果构建已缓存的对象，
				 * 然后从全部查询键里过滤掉已缓存的，即剩下的是“未缓存”的（包含空值的情况）。
				 */
				Set<String> keySet = new HashSet<String>();
				for(String json : jsonList) {
					if(json != null && !isEmptyValue(json)) {
						T obj = objectConverter.apply(json);
						String key = keyMapper.apply(obj);
						list.add(new CachedObject<T>(key, obj));
						keySet.add(key);
					}
				}
				Set<String> uncachedKeySet = new HashSet<String>(keys);
				uncachedKeySet.removeAll(keySet);
				if(uncachedKeySet.size() > 0) {
					for(String key : uncachedKeySet) {
						list.add(new CachedObject<T>(key));
					}
				}
			}
		} else {
			for(String key : keys) {
				list.add(getFromCache(key));
			}
		}
		
		return list;
	}

	@Override
	protected void clearCache() {
		throw new UnsupportedOperationException("redis cannot support clear cache!");
	}

	protected String buildKey(String key) {
		String prefix = keyPrefix == null ? name : keyPrefix;
    	return new KeyBuilder().build(factory.getAppName(),
    			prefix, key).toString();
    }

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
}
