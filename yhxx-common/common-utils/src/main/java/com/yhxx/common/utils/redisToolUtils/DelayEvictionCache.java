package com.yhxx.common.utils.redisToolUtils;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * 定义延迟删除缓存的策略
 * 
 * @author zsp
 *
 * @param <T>
 */
public interface DelayEvictionCache<T> extends Cache<T> {
	
	/**
	 * 获取延迟删除缓存的时间（毫秒），0表示不延迟删除缓存。
	 * 
	 * @return
	 */
	int getDelayEvictMillis();
	
	/**
	 * 设置延迟删除缓存的时间（毫秒）
	 * 
	 * @param millis
	 */
	void setDelayEvictMillis(int millis);
	
	/**
	 * 淘汰缓存；策略是：先删除缓存->更新数据->异步延迟删除缓存。
	 * 用于更新数据时，读写分离场景下，从数据源的数据更新有延迟；
	 * 在多线程情况下，从数据源加载到缓存的数据可能是更新前的数据，因此做一次延迟删除缓存的操作。
	 * 
	 * @param key 缓存键
	 * @param updater 更新数据的代理
	 */
	void delayRemove(String key, Runnable updater);
	
	/**
	 * 淘汰缓存；策略是：先删除缓存->更新数据->异步延迟删除缓存。
	 * 用于更新数据时，读写分离场景下，从数据源的数据更新有延迟；
	 * 在多线程情况下，从数据源加载到缓存的数据可能是更新前的数据，因此做一次延迟删除缓存的操作。
	 * 
	 * @param key 缓存键
	 * @param updater 更新数据的代理
	 * @return 返回更新结果
	 */
	<R> R delayRemove(String key, Supplier<R> updater);
	
	/**
	 * 异步延迟删除缓存。
	 * 
	 * @param key	缓存键
	 */
	void delayRemove(String key);
	
	/**
	 * 异步延迟删除缓存。
	 * 
	 * @param keys	缓存键
	 */
	void delayRemove(Collection<String> keys);

}
