package com.yhxx.common.utils.redisToolUtils.redis;

import com.yhxx.common.utils.redisToolUtils.CachedObject;
import com.yhxx.common.utils.redisToolUtils.converter.StringConverter;
import com.yhxx.common.utils.redisToolUtils.util.KeyBuilder;
import org.springframework.data.redis.core.HashOperations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 
 * 
 * @author zsp
 *
 * @param <T>
 */
public class RedisKeyHashCache<T> extends RedisAbstractCache<T> {

	private final HashOperations<String, String, String> hashOps;
	private final Function<String, T> objectConverter;

	private int bucketSize = 1;
	private StringConverter<T> converter;
	
	@SuppressWarnings("unchecked")
	public RedisKeyHashCache(RedisCacheFactory factory, 
			String name, String keyPrefix, Class<T> clazz) {
		super(factory, name, keyPrefix);
		this.hashOps = stringRedisTemplate.opsForHash();
		this.objectConverter = (e) -> {
			T data = null;
			if(converter != null) {
				data = converter.deserialize(e, clazz);
			} else {
				data = (T)e;
			}
			return data;
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
			String _value = null;
			if(converter != null) {
				_value = converter.serialize(value);
			} else {
				_value = value.toString();
			}
			hashOps.put(_key, key, _value);
			int seconds = expiredSeconds;
			if(seconds > 0) {
				stringRedisTemplate.expire(_key, seconds, TimeUnit.SECONDS);
			}
		}
	}

	@Override
	protected void addToCache(Map<String, T> values, int expiredSeconds) {
		for(Map.Entry<String, T> entry : values.entrySet()) {
			addToCache(entry.getKey(), entry.getValue(), expiredSeconds);
		}
	}

	@Override
	protected CachedObject<T> getFromCache(String key) {
		String _key = buildKey(key);
		return asCachedObject(key, hashOps.get(_key, key), objectConverter);
	}

	@Override
	protected List<CachedObject<T>> getFromCache(Collection<String> keys) {
		List<CachedObject<T>> list = new ArrayList<CachedObject<T>>();
		for(String key : keys) {
			list.add(getFromCache(key));
		}
		return list;
	}

	@Override
	protected void clearCache() {
		throw new UnsupportedOperationException("redis cannot support clear cache!");
	}
	
	@Override
	protected void addEmptyToCache(String key) {
		String _key = buildKey(key);
		hashOps.put(_key, key, EMPTY_VALUE);
	}
	
	protected final String buildKey(String key) {
		String prefix = keyPrefix == null ? name : keyPrefix;
		int bucket = key.hashCode() % bucketSize;
    	return new KeyBuilder().build(factory.getAppName(),
    			prefix, String.valueOf(bucket)).toString();
    }

	public int getBucketSize() {
		return bucketSize;
	}
	public void setBucketSize(int bucketSize) {
		this.bucketSize = bucketSize;
	}

	public StringConverter<T> getConverter() {
		return converter;
	}
	public void setConverter(StringConverter<T> converter) {
		this.converter = converter;
	}

}
