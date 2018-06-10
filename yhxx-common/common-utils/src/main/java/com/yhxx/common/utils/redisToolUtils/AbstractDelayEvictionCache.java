package com.yhxx.common.utils.redisToolUtils;


import com.yhxx.common.utils.redisToolUtils.util.DelayEvictionQueue;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * 定义延迟删除缓存的策略
 * 
 * @author zsp
 *
 * @param <T>
 */
public abstract class AbstractDelayEvictionCache<T> 
		extends AbstractCache<T> implements DelayEvictionCache<T> {

	 /**
     * 延迟删除缓存的时间（毫秒）
     */
    private int delayEvictMillis = 1000;
    
    private final DelayEvictionQueue delayEvictionQueue;
    
    public AbstractDelayEvictionCache(String name, CacheFactory cacheFactory) {
        super(name);
        this.delayEvictionQueue = new DelayEvictionQueue(this, 
        		cacheFactory.getDelayEvictionThreadFactory());
    }
    
    @Override
    public final void delayRemove(String key, Runnable updater) {
        if (isStarted()) {
            removeFromCache(key);
        }
        if(updater != null) {
        	updater.run();
        }
	    if (isStarted()) {
	    	delayEvictionQueue.evict(key);
	    }
    }
    
    @Override
    public final <R> R delayRemove(String key, Supplier<R> updater) {
    	if (isStarted()) {
    		removeFromCache(key);
    	}
    	R result = null;
    	if(updater != null) {
    		result = updater.get();
    	}
    	if (isStarted()) {
	    	delayEvictionQueue.evict(key);
	    }
    	return result;
    }
    
    @Override
    public void delayRemove(String key) {
    	if (isStarted()) {
	    	delayEvictionQueue.evict(key);
	    }
    }
    
    @Override
    public void delayRemove(Collection<String> keys) {
    	if (isStarted() && keys != null && keys.size() > 0) {
    		for(String key : keys) {
    			delayEvictionQueue.evict(key);
    		}
	    }
    }
    
    @Override
    public int getDelayEvictMillis() {
		return delayEvictMillis;
	}

    @Override
	public void setDelayEvictMillis(int delayEvictMillis) {
		this.delayEvictMillis = delayEvictMillis;
	}
	
}
