package com.yhxx.common.utils.redisToolUtils.redis.generic;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhxx.common.utils.jsonToolUtils.JsonUtils;
import com.yhxx.common.utils.redisToolUtils.Cache;
import com.yhxx.common.utils.redisToolUtils.moduling.BasicService;
import com.yhxx.common.utils.redisToolUtils.util.KeyBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author mlc
 * @date 2018/1/29 16:51
 * @description By:泛型的缓存抽象类
 */
public abstract class RedisGenericAbstractCache extends BasicService implements Cache<String> {

    /**
     * 缓存key的前缀，通常redis中的key的格式为:appName:keyPrefix:objectId
     */
    protected String keyPrefix;

    /**
     * 缓存应用名
     */
    protected String appName;

    /**
     * 缓存描述
     */
    private String description;

    /**
     * 过期时间(秒)
     */
    private int expiredSeconds = 0;

    /**
     * 是否开启统计缓存的命中次数
     */
    private boolean enableStat;
    /**
     * 是否开启防止缓存击穿
     */
    private boolean enableBreakdownPrevent;

    /**
     * 防止缓存击穿的空值的缓存失效时间（秒）
     */
    private int breakdownPreventExpiredSeconds;

    /**
     * redis 字符串模板
     */
    private StringRedisTemplate stringRedisTemplate;

    /**
     * value 类型结构
     */
    private ValueOperations<String, String> valueOps;

    /**
     * json 转换
     */
    protected ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param factory
     * @param keyPrefix
     * @param description
     */
    public RedisGenericAbstractCache(RedisGenericCacheFactory factory,
                                     String keyPrefix,
                                     String description
    ) {
        this.stringRedisTemplate = factory.getStringRedisTemplate();
        this.appName = factory.getAppName();
        this.valueOps = stringRedisTemplate.opsForValue();
        this.keyPrefix = keyPrefix;
        this.description = description;
    }

    protected String buildKey(String key) {
        String prefix = keyPrefix == null ? name : keyPrefix;
        return new KeyBuilder().build(appName,
                prefix, key).toString();
    }

    protected void expire(Collection<String> keys, int seconds) {
        for (String key : keys) {
            expire(key, seconds);
        }
    }

    protected void expire(String key, int seconds) {
        if (seconds > 0) {
            stringRedisTemplate.expire(buildKey(key), seconds, TimeUnit.SECONDS);
        }
    }

    protected void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return "redis";
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setExpiredSeconds(int seconds) {
        this.expiredSeconds = seconds;
    }

    @Override
    public int getExpiredSeconds() {
        return expiredSeconds;
    }

    @Override
    public void setEnableStat(boolean enabled) {
        this.enableStat = enabled;
    }

    @Override
    public boolean isEnableStat() {
        return enableStat;
    }

    @Override
    public void setEnableBreakdownPrevent(boolean enableBreakdownPrevent) {
        this.enableBreakdownPrevent = enableBreakdownPrevent;
    }

    @Override
    public boolean isEnableBreakdownPrevent() {
        return enableBreakdownPrevent;
    }

    @Override
    public void setBreakdownPreventExpiredSeconds(int seconds) {
        this.breakdownPreventExpiredSeconds = seconds;
    }

    @Override
    public int getBreakdownPreventExpiredSeconds() {
        return breakdownPreventExpiredSeconds;
    }

    @Override
    public void setKeyMapper(Function<String, String> keyMapper) {
        //do nothing
    }

    @Override
    public void add(String key, String value) {
        add(key, value, getExpiredSeconds());
    }

    @Override
    public void add(Map<String, String> values) {
        add(values, getExpiredSeconds());
    }

    @Override
    public void add(String key, String value, int expiredSeconds) {
        if (null != value) {
            String _key = buildKey(key);
            String _value = JsonUtils.toJson(objectMapper, value);
            int seconds = expiredSeconds;
            if (seconds > 0) {
                valueOps.set(_key, _value, seconds, TimeUnit.SECONDS);
            } else {
                valueOps.set(_key, _value);
            }
        }
    }

    @Override
    public void add(Map<String, String> values, int expiredSeconds) {
        if (values != null && values.size() > 0) {
            List<String> keys = new ArrayList<String>();
            Map<String, String> map = new HashMap<String, String>();
            for (Map.Entry<String, String> item : values.entrySet()) {
                keys.add(item.getKey());
                map.put(buildKey(item.getKey()),
                        JsonUtils.toJson(objectMapper, item.getValue()));
            }
            valueOps.multiSet(map);
            int seconds = expiredSeconds;
            if (seconds > 0) {
                expire(keys, seconds);
            }
        }
    }

    @Override
    public String get(String key) {
        String _key = buildKey(key);
        String val = valueOps.get(_key);
        return val;
    }

    @Override
    public List<String> get(Collection<String> keys) {
        List<String> list = new ArrayList<>();
        keys.forEach(key -> {
            list.add(get(key));
        });
        return list;
    }

    @Override
    public String getAndFetch(String key, Function<String, String> fetcher) {
        return null;
    }

    @Override
    public List<String> getAndFetch(Collection<String> keys, Function<String, String> fetcher, Supplier<List<String>> multiFetcher) {
        return null;
    }

    @Override
    public void remove(String key) {
        stringRedisTemplate.delete(buildKey(key));
    }

    @Override
    public void remove(Collection<String> keys) {
        keys.forEach(key -> {
            remove(key);
        });
    }

    @Override
    public long ttl(String key) {
        return stringRedisTemplate.getExpire(buildKey(key));
    }

    @Override
    public void clear() {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public long getHits() {
        return 0;
    }

    @Override
    public long getEmptyHits() {
        return 0;
    }

    @Override
    public long getMisses() {
        return 0;
    }
}
