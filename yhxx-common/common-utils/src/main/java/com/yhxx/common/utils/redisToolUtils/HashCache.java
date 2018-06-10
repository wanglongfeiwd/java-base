package com.yhxx.common.utils.redisToolUtils;

import java.util.List;
import java.util.Map;

/**
 * @author mlc
 * @date 2018/2/5 18:01
 * @description By:hash 缓存
 */
public interface HashCache<HK, HV> extends Cache<String> {

    /**
     * 用于为哈希表中的字段赋值 。
     *
     * @param key
     * @param hk
     * @param hv
     * @return
     */
    int hset(String key, HK hk, HV hv);

    /**
     * 用于同时将多个 field-value (字段-值)对设置到哈希表中。
     *
     * @param key
     * @param hmap
     */
    void hmset(String key, Map<HK, HV> hmap);

    /**
     * 用于删除哈希表 key 中的一个或多个指定字段，不存在的字段将被忽略。
     *
     * @param key
     * @param hk
     * @return
     */
    long hdel(String key, HK hk);

    /**
     * 用于为哈希表中的字段值加上指定增量值
     *
     * @param key
     * @param hk
     * @param num
     * @return
     */
    long hincrBy(String key, HK hk, long num);

    /**
     * 用于为哈希表中的字段值加上指定浮点数增量值
     *
     * @param key
     * @param hk
     * @param num
     * @return
     */
    double hincrBy(String key, HK hk, double num);

    /**
     * 用于返回哈希表中指定字段的值。
     *
     * @param key
     * @param hk
     * @return
     */
    HV hget(String key, HK hk);


    /**
     * 用于返回哈希表中，一个或多个给定字段的值。
     *
     * @param key
     * @param hks
     * @return
     */
    List<HV> hmget(String key, HK... hks);

    /**
     * 用于获取哈希表中字段的数量。
     *
     * @param key
     * @return
     */
    long hlen(String key);


    /**
     * 用于获取哈希表中的所有字段名。
     *
     * @param key
     * @return
     */
    List<HK> hkeys(String key);

    /**
     * 返回哈希表所有字段的值。
     *
     * @param key
     * @return
     */
    List<HV> hvals(String key);

    /**
     * 用于返回哈希表中，所有的字段和值。
     *
     * @param key
     * @return
     */
    Map<HK, HV> hgetAll(String key);

}
