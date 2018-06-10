package com.yhxx.common.utils.redisToolUtils;

import com.yhxx.common.utils.redisToolUtils.moduling.BasicService;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 缓存管理
 * 
 * @author zsp
 *
 */
public class CacheManager extends BasicService {
	
	/**
	 * 缺省的防止缓存击穿而设置的空值的失效时间（秒）
	 */
	private final static int DEFAULT_BREAKDOWN_PREVENT_EXPIRED_SECONDS = 10;
	
	/**
	 * 缓存对象
	 */
	private volatile List<Cache<?>> cacheList;
	
	 /**
     * 是否开启统计缓存的命中次数
     */
    private boolean enableStat;
    
    /**
     * 是否开启防止缓存击穿
     */
    private boolean enableBreakdownPrevent;
    
    /**
     * 防止缓存击穿的空值的缓存失效时间（秒）
     */
    private int breakdownPreventExpiredSeconds = DEFAULT_BREAKDOWN_PREVENT_EXPIRED_SECONDS;

	/**
	 * 增加缓存管理对象
	 * 
	 * @param cache 缓存管理对象
	 */
	public void addCache(Cache<?> cache) {
		if(cache == null) {
			return;
		}
		List<Cache<?>> _cacheList = getCacheList();
		if(!_cacheList.contains(cache)) {
			_cacheList.add(cache);
			start(cache);
		}
	}
	
	/**
	 * 删除缓存管理对象
	 * 
	 * @param cache 缓存管理对象
	 */
	public void removeCache(Cache<?> cache) {
		if(cache == null) {
			return;
		}
		stop(cache);
		List<Cache<?>> _cacheList = getCacheList();
		_cacheList.remove(cache);
	}
	
	/**
	 * 获取缓存管理对象
	 * 
	 * @param name	缓存管理对象名称
	 * @return
	 */
	public Cache<?> getCache(String name) {
		if(StringUtils.hasText(name)) {
			List<Cache<?>> _cacheList = getCacheList();
			for(Cache<?> cache : _cacheList) {
				if(name.equals(cache.getName())) {
					return cache;
				}
			}
		}
		return null;
	}
	
	@Override
	protected void doStart() {
		List<Cache<?>> _cacheList = getCacheList();
		for(Cache<?> cache : _cacheList) {
			start(cache);
		}
	}
	
	@Override
	protected void doStop() {
		List<Cache<?>> _cacheList = getCacheList();
		for(Cache<?> cache : _cacheList) {
			stop(cache);
		}
	}
	
	private void start(Cache<?> cache) {
		init(cache);
		cache.start();
	}
	
	private void init(Cache<?> cache) {
		cache.setEnableStat(enableStat);
		cache.setEnableBreakdownPrevent(enableBreakdownPrevent);
		cache.setBreakdownPreventExpiredSeconds(breakdownPreventExpiredSeconds);
	}
	
	private void stop(Cache<?> cache) {
		cache.stop();
	}
	
	public List<Cache<?>> getCacheList() {
		if(cacheList == null) {
			synchronized (this) {
				if(cacheList == null) {
					cacheList = new CopyOnWriteArrayList<Cache<?>>();
				}
			}
		}
		return cacheList;
	}

	public synchronized void setCacheList(List<Cache<?>> cacheList) {
		if(this.cacheList == null && cacheList != null && cacheList.size() > 0) {
			this.cacheList = new CopyOnWriteArrayList<Cache<?>>(cacheList);
		}
	}

	public boolean isEnableStat() {
		return enableStat;
	}

	public void setEnableStat(boolean enableStat) {
		this.enableStat = enableStat;
	}

	public boolean isEnableBreakdownPrevent() {
		return enableBreakdownPrevent;
	}

	public void setEnableBreakdownPrevent(boolean enableBreakdownPrevent) {
		this.enableBreakdownPrevent = enableBreakdownPrevent;
	}

	public int getBreakdownPreventExpiredSeconds() {
		return breakdownPreventExpiredSeconds;
	}

	public void setBreakdownPreventExpiredSeconds(int breakdownPreventExpiredSeconds) {
		if(breakdownPreventExpiredSeconds < 1) {
			throw new IllegalArgumentException("The value should be larger than 0.");
		}
		this.breakdownPreventExpiredSeconds = breakdownPreventExpiredSeconds;
	}

}
