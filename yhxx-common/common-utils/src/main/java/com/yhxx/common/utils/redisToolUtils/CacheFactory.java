package com.yhxx.common.utils.redisToolUtils;


import com.yhxx.common.utils.redisToolUtils.util.DefaultThreadFactory;

/**
 * 缓存工厂
 * 
 * @author zsp
 *
 */
public interface CacheFactory {
	
	/**
	 * 
	 * @return
	 */
	DefaultThreadFactory getDelayEvictionThreadFactory();
	
}
