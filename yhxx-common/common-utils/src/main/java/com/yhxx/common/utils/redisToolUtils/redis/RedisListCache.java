package com.yhxx.common.utils.redisToolUtils.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhxx.common.utils.jsonToolUtils.JsonUtils;
import com.yhxx.common.utils.redisToolUtils.CachedObject;
import com.yhxx.common.utils.redisToolUtils.ListCache;
import org.springframework.data.redis.core.ListOperations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 定义了缓存的操作
 * 
 * @author zsp
 *
 * @param <T>	缓存的数据类型
 */
public class RedisListCache<T> extends RedisAbstractCache<List<T>> implements ListCache<T> {

	private final ListOperations<String, String> listOps;

	private final Class<T> clazz;

	protected ObjectMapper objectMapper = new ObjectMapper();

	public RedisListCache(RedisCacheFactory factory,
                          String name, String keyPrefix, Class<T> clazz) {
		super(factory, name, keyPrefix);
		this.clazz = clazz;
		this.listOps = stringRedisTemplate.opsForList();
	}

	@Override
	protected void addToCache(String key, List<T> value) {
		addToCache(key, value, getExpiredSeconds());
	}


	@Override
	protected void addToCache(Map<String, List<T>> values) {
		add(values,getExpiredSeconds());
	}

	@Override
	protected void addToCache(String key, List<T> value, int expiredSeconds) {
		if(value != null) {
			String _key = buildKey(key);
			String _value = buildValue(value);
			listOps.leftPush(_key,_value);
			int seconds = expiredSeconds;
			if(seconds > 0) {
				stringRedisTemplate.expire(_key, seconds, TimeUnit.SECONDS);
			}
		}

	}

	@Override
	protected void addToCache(Map<String, List<T>> values, int expiredSeconds) {

		for (Map.Entry<String,List<T>> entry:values.entrySet()) {
			add(entry.getKey(), entry.getValue(), expiredSeconds);
		}
	}


	@Override
	protected CachedObject<List<T>> getFromCache(String key) {
		String _key = buildKey(key);
		String result = listOps.rightPop(_key);
		return asCachedObject(key,recoverValue(result),null);

	}

	private T recoverValue(String result) {
		return JsonUtils.parse(objectMapper,result,clazz);
	}

	@Override
	protected List<CachedObject<List<T>>> getFromCache(Collection<String> keys) {
		List<CachedObject<List<T>>> list = new ArrayList<>();
		for (String key:keys) {
			list.add(getFromCache(key));
		}
		return list;
	}

	@Override
	protected void clearCache() {
		throw new UnsupportedOperationException("redis cannot support clear cache!");
	}

	private String buildValue(List<T> value) {
		return JsonUtils.toJson(objectMapper,value);
	}

	private String buildValue(T value) {
		return JsonUtils.toJson(objectMapper,value);
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void leftPush(String key, List<T> values) {
		addToCache(key, values);
	}

	@Override
	public void leftPush(String key, T value) {
		if(value != null) {
			String _key = buildKey(key);
			String _value = buildValue(value);
			listOps.leftPush(_key,_value);
		}
	}
}
