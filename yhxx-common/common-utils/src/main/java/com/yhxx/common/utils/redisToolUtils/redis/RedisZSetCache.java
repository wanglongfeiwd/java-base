package com.yhxx.common.utils.redisToolUtils.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhxx.common.bean.ZSetTypedTuple;
import com.yhxx.common.utils.jsonToolUtils.JsonUtils;
import com.yhxx.common.utils.redisToolUtils.CachedObject;
import com.yhxx.common.utils.redisToolUtils.ZSetCache;
import com.yhxx.common.utils.redisToolUtils.util.KeyBuilder;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 定义了缓存的操作
 * 
 * @author zsp
 *
 * @param <T>	缓存的数据类型
 */
public  class RedisZSetCache<T> extends RedisAbstractCache<Set<ZSetTypedTuple<T>>> implements ZSetCache<T> {

	private final ZSetOperations<String, String> zsetOps;

	private final Class<T> clazz;

	protected ObjectMapper objectMapper = new ObjectMapper();

	public RedisZSetCache(RedisCacheFactory factory,
					 String name, String keyPrefix, Class<T> clazz) {
		super(factory, name, keyPrefix);
		this.clazz = clazz;
		this.zsetOps = stringRedisTemplate.opsForZSet();
	}

	@Override
	protected void addToCache(String key, Set<ZSetTypedTuple<T>> value) {
		addToCache(key, value, getExpiredSeconds());
	}

	@Override
	protected void addToCache(Map<String, Set<ZSetTypedTuple<T>>> values) {
		addToCache(values, getExpiredSeconds());
	}

	@Override
	protected void addToCache(String key, Set<ZSetTypedTuple<T>> value, int expiredSeconds) {
		if(value != null) {
			String _key = buildKey(key);
			Set<ZSetOperations.TypedTuple<String>> _value = buildValue(value);
			zsetOps.add(_key, _value);
			int seconds = expiredSeconds;
			if(seconds > 0) {
				stringRedisTemplate.expire(_key, seconds, TimeUnit.SECONDS);
			}
		}
	}

	@Override
	protected void addToCache(Map<String, Set<ZSetTypedTuple<T>>> values, int expiredSeconds) {
		for(Map.Entry<String, Set<ZSetTypedTuple<T>>> entry : values.entrySet()) {
			addToCache(entry.getKey(), entry.getValue(), expiredSeconds);
		}
	}

	@Override
	protected void clearCache() {
		throw new UnsupportedOperationException("redis cannot support clear cache!");
	}

	@Override
	protected void addEmptyToCache(String key) {
		String _key = buildKey(key);
		zsetOps.add(_key, new TreeSet<ZSetOperations.TypedTuple<String>>() {});
	}

	/**
	 * 添加数据至redis中（注意这里没有接入框架的统计等功能需要改进）
	 */
	@Override
	public Boolean add(String key, T value, long rank) {
		return zsetOps.add(buildKey(key), JsonUtils.toJson(objectMapper, value), rank);
	}


	@Override
	protected CachedObject<Set<ZSetTypedTuple<T>>> getFromCache(String key) {
		String _key = buildKey(key);
		Set<ZSetOperations.TypedTuple<String>> all = zsetOps.rangeWithScores(_key, 0L, Long.MAX_VALUE);
		return asCachedObject(key, recoverValue(all), null);
	}

	@Override
	protected List<CachedObject<Set<ZSetTypedTuple<T>>>> getFromCache(Collection<String> keys) {
		List<CachedObject<Set<ZSetTypedTuple<T>>>> list = new ArrayList<>();
		for(String key : keys) {
			list.add(getFromCache(key));
		}
		return list;
	}

	/**
	 * 从redis中获取反序的数据（注意这里没有接入框架的统计等功能需要改进）
	 */
	@Override
	public Set<T> reverseRangeByScore(String key, long startScore, long endScore) {
		Set<String> strSet = zsetOps.reverseRangeByScore(buildKey(key), startScore, endScore);

		Set<T> rawSet = new TreeSet<>();
		for (String each : strSet) {
			rawSet.add(JsonUtils.parse(objectMapper, each, clazz));
		}
		return rawSet;
	}


	@Override
	public Set<T> reverseRangeByScore(String key, long startScore, long endScore, long offect, long count) {
		Set<String> strSet = zsetOps.reverseRangeByScore(buildKey(key), startScore, endScore, offect-1, count);

		Set<T> rawSet = new TreeSet<>();
		for (String each : strSet) {
			rawSet.add(JsonUtils.parse(objectMapper, each, clazz));
		}
		return rawSet;
	}

	@Override
	public Set<T> rangeByScore(String key, long startScore, long endScore) {

		Set<String> strSet = zsetOps.rangeByScore(buildKey(key), startScore, endScore);
		Set<T> rawSet = new TreeSet<>();
		for (String each : strSet) {
			rawSet.add(JsonUtils.parse(objectMapper, each, clazz));
		}
		return rawSet;

	}

	@Override
	public Set<T> rangeByScore(String key, long startScore, long endScore, long offect, long count) {
		Set<String> strSet = zsetOps.rangeByScore(buildKey(key), startScore, endScore, offect, count);
		Set<T> rawSet = new TreeSet<>();
		for (String each : strSet) {
			rawSet.add(JsonUtils.parse(objectMapper, each, clazz));
		}
		return rawSet;
	}

	@Override
	public Long removeRange(String key, long startScore, long endScore) {
		return zsetOps.removeRange(buildKey(key),startScore,endScore);
	}


	private Set<ZSetTypedTuple<T>> recoverValue(Set<ZSetOperations.TypedTuple<String>> value) {
		Set<ZSetTypedTuple<T>> set = new TreeSet<>();
		for (ZSetOperations.TypedTuple<String> each : value) {
			set.add(new ZSetTypedTuple<T>(JsonUtils.parse(objectMapper, each.getValue(), clazz), each.getScore()));
		}

		return set;
	}

	private Set<ZSetOperations.TypedTuple<String>> buildValue(Set<ZSetTypedTuple<T>> value) {
		Set<ZSetOperations.TypedTuple<String>> set = new TreeSet<>();
		for (ZSetTypedTuple<T> each : value) {
			set.add(new DefaultTypedTuple(JsonUtils.toJson(objectMapper, each.getValue()), each.getScore()));
		}

		return set;
	}

	@Override
	protected String buildKey(String key) {
		String prefix = keyPrefix == null ? name : keyPrefix;
		return new KeyBuilder().build(factory.getAppName(),
				prefix, key).toString();
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
}
