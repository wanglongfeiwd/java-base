package com.yhxx.common.utils.redisToolUtils.redis.generic;

import com.yhxx.common.utils.redisToolUtils.HashCache;
import org.springframework.data.redis.core.HashOperations;

import java.util.*;

/**
 * @author mlc
 * @date 2018/1/29 18:43
 * @description By:
 */
public class RedisGenericHashCache<HK, HV>
        extends RedisGenericAbstractCache
        implements HashCache<HK, HV> {

    private HashOperations<String, HK, HV> opsForHash;

    public RedisGenericHashCache(RedisGenericCacheFactory factory, String name, String keyPrefix) {
        super(factory, keyPrefix, name);
        this.opsForHash = factory.getRedisTemplate().opsForHash();
    }

    @Override
    public int hset(String key, HK hk, HV hv) {
        try {
            String _key = buildKey(key);
            opsForHash.put(_key, hk, hv);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void hmset(String key, Map hmap) {
        String _key = buildKey(key);
        opsForHash.putAll(_key, hmap);
    }

    @Override
    public long hdel(String key, HK hk) {
        String _key = buildKey(key);
        long cnt = opsForHash.delete(_key, hk);
        return cnt;
    }

    @Override
    public long hincrBy(String key, HK hk, long num) {
        String _key = buildKey(key);
        long inc = opsForHash.increment(_key, hk, num);
        return inc;
    }

    @Override
    public double hincrBy(String key, HK hk, double num) {
        String _key = buildKey(key);
        double inc = opsForHash.increment(_key, hk, num);
        return inc;
    }

    @Override
    public HV hget(String key, HK hk) {
        String _key = buildKey(key);
        HV hv = opsForHash.get(_key, hk);
        return hv;
    }

    @Override
    public List<HV> hmget(String key, HK... hks) {
        String _key = buildKey(key);
        List<HV> hvs = opsForHash.multiGet(_key, Arrays.asList(hks));
        return hvs;
    }

    @Override
    public long hlen(String key) {
        String _key = buildKey(key);
        Long size = opsForHash.size(_key);
        return size;
    }

    @Override
    public List<HK> hkeys(String key) {
        String _key = buildKey(key);
        Set<HK> keys = opsForHash.keys(_key);
        return new ArrayList<>(keys);
    }

    @Override
    public List<HV> hvals(String key) {
        String _key = buildKey(key);
        List<HV> hvs = opsForHash.values(_key);
        return hvs;
    }

    @Override
    public Map<HK, HV> hgetAll(String key) {
        String _key = buildKey(key);
        Map<HK, HV> entries = opsForHash.entries(_key);
        return entries;
    }
}
