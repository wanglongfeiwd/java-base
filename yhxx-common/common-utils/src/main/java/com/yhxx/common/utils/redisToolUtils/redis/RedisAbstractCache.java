package com.yhxx.common.utils.redisToolUtils.redis;


import com.yhxx.common.utils.redisToolUtils.AbstractDelayEvictionCache;
import com.yhxx.common.utils.redisToolUtils.CachedObject;
import com.yhxx.common.utils.redisToolUtils.util.KeyBuilder;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * 
 * @author zsp
 *
 * @param <T>
 */
public abstract class RedisAbstractCache<T> extends AbstractDelayEvictionCache<T> {
	
	/**
	 * 缓存key的前缀，通常redis中的key的格式为:appName:keyPrefix:objectId
	 */
	protected final String keyPrefix;
	
	/**
	 * 缓存工厂
	 */
	protected RedisCacheFactory factory;

	/**
	 * 缓存应用名
	 */
	protected String appName;

	/**
	 * 缓存模板
	 */
	protected StringRedisTemplate stringRedisTemplate;

	/**
	 * 缓存客户端
	 */
	protected RedissonClient redissonClient;
	
	/**
	 * 从缓存数据获取键值的代理
	 */
	protected Function<T, String> keyMapper;

	/**
	 * 值操作对象
	 */
	protected ValueOperations<String, String> valueOps;
	
	/**
	 * 设置从缓存数据获取键值的代理
	 * 
	 * @param keyMapper
	 */
	public void setKeyMapper(Function<T, String> keyMapper) {
		this.keyMapper = keyMapper;
	}
	
	public RedisAbstractCache(RedisCacheFactory factory,
			String name,
			String keyPrefix) {
		super(name, factory);
		this.keyPrefix = keyPrefix;
		this.factory = factory;
		this.appName = factory.getAppName();
		this.stringRedisTemplate = factory.getStringRedisTemplate();
		this.redissonClient = factory.getRedissonClient();
		this.valueOps = stringRedisTemplate.opsForValue();
	}
	
	@Override
	protected void removeFromCache(String key) {
		stringRedisTemplate.delete(buildKey(key));
	}
	
	@Override
	protected void removeFromCache(Collection<String> keys) {
		List<String> _keys = keys.stream().map(e -> buildKey(e)).collect(Collectors.toList());
		stringRedisTemplate.delete(_keys);
	}
	
	@Override
	protected void expire(String key, int seconds) {
		stringRedisTemplate.expire(buildKey(key), seconds, TimeUnit.SECONDS);
	}
	
	@Override
	protected void expire(Collection<String> keys, int seconds) {
		for(String key : keys) {
			expire(key, seconds);
		}
	}
	
	@Override
	public long ttl(String key) {
		return stringRedisTemplate.getExpire(buildKey(key));
	}
	
	@Override
	protected CachedObject<T> fetchObjectWithLock(String key,
												  Function<String, T> fetcher) {
		CachedObject<T> cacheObj;
		synchronized(this) {
			cacheObj = getFromCache(key);
			if(cacheObj.isNull()) {
				if(redissonClient != null) {
					/*
					 * 在jvm的锁的基础上（防止同一个jvm实例的多个线程为争用分布式锁而频繁访问redis），
					 * 增加redis分布式锁，同一时间只允许一个进程访问redis的共享缓存资源
					 */
					RLock lock = redissonClient.getLock(buildRedisLockKey(key));
					try {
						lock.lock();
						cacheObj = getFromCache(key);
						if(cacheObj.isNull()) {
							cacheObj = fetchObject(key, fetcher);
						}
					} finally {
						lock.unlock();
					}
					if (cacheObj.isEmpty()) {
			    		incrEmptyHit();
			    	} else if(cacheObj.getData() != null) {
			    		handleHit(key);
			    	}
				} else {
					cacheObj = fetchObject(key, fetcher);
				}
			}
		}
		if (cacheObj.isEmpty()) {
    		incrEmptyHit();
    	} else if(cacheObj.getData() != null) {
    		handleHit(key);
    	}
		return cacheObj;
	}
	
	@Override
	protected void hit() {
		valueOps.increment(buildHitKey(), 1);
	}
	
	@Override
	protected void emptyHit() {
		valueOps.increment(buildEmptyHitKey(), 1);
	}

	@Override
	protected void miss() {
		valueOps.increment(buildMissKey(), 1);
	}

	@Override
	public long getHits() {
		String value = valueOps.get(buildHitKey());
		if(value != null) {
			return Long.parseLong(value);
		} else {
			return 0;
		}
	}
	
	@Override
	public long getEmptyHits() {
		String value = valueOps.get(buildEmptyHitKey());
		if(value != null) {
			return Long.parseLong(value);
		} else {
			return 0;
		}
	}

	@Override
	public long getMisses() {
		String value = valueOps.get(buildMissKey());
		if(value != null) {
			return Long.parseLong(value);
		} else {
			return 0;
		}
	}
	
	@Override
	protected void addEmptyToCache(String key) {
		valueOps.set(buildKey(key), EMPTY_VALUE);
	}

	@Override
	protected <V> boolean isEmptyValue(V value) {
		return EMPTY_VALUE.equals(value);
	}
	
	private final String buildHitKey() {
		return new KeyBuilder().build(factory.getAppName(),
				"cache", name, "hit").toString();
    }
	
	private final String buildEmptyHitKey() {
		return new KeyBuilder().build(factory.getAppName(), 
				"cache", name, "ehit").toString();
    }
	
	private final String buildMissKey() {
		return new KeyBuilder().build(factory.getAppName(), 
				"cache", name, "mis").toString();
    }
	
	private final <K> String buildRedisLockKey(K key) {
		String lockKey = null;
		if(key instanceof Collection) {
			StringBuilder builder = new StringBuilder();
			Iterator<?> it = ((Collection<?>)key).iterator();
			for(;it.hasNext();) {
				builder.append(it.next());
			}
			lockKey = String.valueOf(builder.toString().hashCode());
		} else {
			lockKey = key.toString();
		}
		return new KeyBuilder().build("dislock", "key", lockKey).toString();
    }
	
	protected String buildKey(String key) {
		String prefix = keyPrefix == null ? name : keyPrefix;
    	return new KeyBuilder().build(factory.getAppName(), 
    			prefix, key).toString();
    }

	@Override
	public String getType() {
		return "redis";
	}
	
}
