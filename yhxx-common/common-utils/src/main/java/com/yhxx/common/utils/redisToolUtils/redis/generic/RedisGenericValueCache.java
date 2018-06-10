package com.yhxx.common.utils.redisToolUtils.redis.generic;

import com.yhxx.common.utils.redisToolUtils.ValueCache;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

/**
 * Created by zp on 2018/4/16.
 */
public class RedisGenericValueCache<V> extends RedisGenericAbstractCache implements ValueCache<V> {

    private ValueOperations<String, V> opsForValue;

    public RedisGenericValueCache(RedisGenericCacheFactory factory, String name, String keyPrefix) {
        super(factory, keyPrefix, name);
        this.opsForValue = factory.getRedisTemplate().opsForValue();
    }

    @Override
    public void setValue(String key, V value) {
        String _key = buildKey(key);
        opsForValue.set(_key, value);
    }

    @Override
    public void setValue(String key, V value, long time, TimeUnit timeUnit) {
        String _key = buildKey(key);
        opsForValue.set(_key, value, time, timeUnit);
    }

    @Override
    public Boolean setIfAbsent(String key, V value) {
        String _key = buildKey(key);
        return opsForValue.setIfAbsent(_key, value);
    }

    @Override
    public V getAndSet(String key, V value) {
        String _key = buildKey(key);
        return opsForValue.getAndSet(_key, value);
    }

    @Override
    public Long increment(String key, long value) {
        String _key = buildKey(key);
        return opsForValue.increment(_key, value);
    }

    @Override
    public Double increment(String key, double value) {
        String _key = buildKey(key);
        return opsForValue.increment(_key, value);
    }

    @Override
    public Integer append(String key, String value) {
        String _key = buildKey(key);
        return opsForValue.append(_key, value);
    }

    @Override
    public V getValue(String key) {
        String _key = buildKey(key);
        return opsForValue.get(_key);
    }

    @Override
    public Long size(String key) {
        String _key = buildKey(key);
        return opsForValue.size(_key);
    }
}
