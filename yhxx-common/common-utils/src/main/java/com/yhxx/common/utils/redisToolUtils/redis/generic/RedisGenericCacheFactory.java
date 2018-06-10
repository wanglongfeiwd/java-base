package com.yhxx.common.utils.redisToolUtils.redis.generic;

import com.yhxx.common.utils.redisToolUtils.redis.RedisCacheFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author mlc
 * @date 2018/1/29 21:06
 * @description By:
 */
public class RedisGenericCacheFactory<T> extends RedisCacheFactory {

    RedisTemplate<String, T> redisTemplate;

    public RedisTemplate<String, T> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
