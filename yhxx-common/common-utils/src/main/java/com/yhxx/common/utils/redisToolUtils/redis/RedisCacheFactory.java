package com.yhxx.common.utils.redisToolUtils.redis;

import com.yhxx.common.utils.redisToolUtils.GenericCacheFactory;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis缓存工厂
 * 
 * @author zsp
 *
 */
public class RedisCacheFactory extends GenericCacheFactory {

	/**
	 * 缓存应用名
	 */
	private String appName;

	/**
	 * 缓存模板
	 */
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 缓存客户端
	 */
	private RedissonClient redissonClient;
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public StringRedisTemplate getStringRedisTemplate() {
		return stringRedisTemplate;
	}

	public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	public RedissonClient getRedissonClient() {
		return redissonClient;
	}

	public void setRedissonClient(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}
	
}
