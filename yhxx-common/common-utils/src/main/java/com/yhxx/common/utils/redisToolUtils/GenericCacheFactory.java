package com.yhxx.common.utils.redisToolUtils;


import com.yhxx.common.utils.redisToolUtils.util.DefaultThreadFactory;

/**
 * 缓存工厂
 * 
 * @author zsp
 *
 */
public class GenericCacheFactory  implements CacheFactory {
	
	private DefaultThreadFactory delayEvictionThreadFactory;
	
	@Override
	public DefaultThreadFactory getDelayEvictionThreadFactory() {
		return delayEvictionThreadFactory;
	}

	public void setDelayEvictionThreadFactory(DefaultThreadFactory delayEvictionThreadFactory) {
		this.delayEvictionThreadFactory = delayEvictionThreadFactory;
	}
	
}
