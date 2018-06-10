package com.yhxx.common.utils.redisToolUtils;


import com.yhxx.common.bean.ZSetTypedTuple;

import java.util.Set;

/**
 * @Author: Wanglf
 * @Date: Created in 21:23 2017/11/7
 * @modified By:
 */
public interface ZSetCache<T> extends Cache<Set<ZSetTypedTuple<T>>> {

    Boolean add(String key, T value, long rank);

    Set<T> reverseRangeByScore(String key, long startScore, long endScore);

    Set<T> reverseRangeByScore(String key, long startScore, long endScore, long offect, long count);

    Set<T> rangeByScore(String key, long startScore, long endScore);

    Set<T> rangeByScore(String key, long startScore, long endScore, long offect, long count);

    //按照key删除zset
    Long removeRange(String key, long startScore, long endScore);



}
