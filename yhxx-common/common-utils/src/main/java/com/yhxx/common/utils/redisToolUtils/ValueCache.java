package com.yhxx.common.utils.redisToolUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by zp on 2018/4/16.
 * @description By:value 缓存
 */
public interface ValueCache<V> extends Cache<String>{

    void setValue(String key, V value);

    void setValue(String key, V value, long time, TimeUnit timeUnit);

    Boolean setIfAbsent(String key, V value);

    V getValue(String key);

    V getAndSet(String key, V value);

    Long increment(String key, long value);

    Double increment(String key, double value);

    Integer append(String key, String value);

    Long size(String key);
}
