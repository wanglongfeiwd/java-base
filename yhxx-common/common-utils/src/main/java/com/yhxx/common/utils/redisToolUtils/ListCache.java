package com.yhxx.common.utils.redisToolUtils;

import java.util.List;

/**
 * @Author: Wanglf
 * @Date: Created in 20:46 2017/11/27
 * @modified By:
 */
public interface ListCache<T> extends Cache<List<T>> {

    void leftPush(String key, List<T> values);

    void leftPush(String key, T value);
}
