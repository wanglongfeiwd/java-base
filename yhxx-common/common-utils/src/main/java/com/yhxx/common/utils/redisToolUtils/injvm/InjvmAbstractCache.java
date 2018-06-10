package com.yhxx.common.utils.redisToolUtils.injvm;


import com.yhxx.common.utils.redisToolUtils.AbstractCache;
import com.yhxx.common.utils.redisToolUtils.GenericCacheFactory;

import java.util.Collection;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

/**
 * 
 * 
 * @author zsp
 *
 * @param <T>
 */
public abstract class InjvmAbstractCache<T> extends AbstractCache<T> {

	private final LongAdder hits = new LongAdder();
	
	private final LongAdder emptyHits = new LongAdder();

	private final LongAdder misses = new LongAdder();
	
	/**
	 * 从缓存数据获取键值的代理
	 */
	protected Function<T, String> keyMapper;
	
	public InjvmAbstractCache(GenericCacheFactory factory, String name) {
		super(name);
	}
	
	/**
	 * 设置从缓存数据获取键值的代理
	 * 
	 * @param keyMapper
	 */
	public void setKeyMapper(Function<T, String> keyMapper) {
		this.keyMapper = keyMapper;
	}
	
	@Override
	protected void hit() {
		hits.increment();
	}
	
	@Override
	protected void emptyHit() {
		emptyHits.increment();
	}

	@Override
	protected void miss() {
		misses.increment();
	}

	@Override
	public long getHits() {
		return hits.longValue();
	}
	
	@Override
	public long getEmptyHits() {
		return emptyHits.longValue();
	}

	@Override
	public long getMisses() {
		return misses.longValue();
	}
	
	@Override
	protected void expire(String key, int seconds) {
		//do nothing
	}
	
	@Override
	protected void expire(Collection<String> keys, int seconds) {
		//do nothing
	}
	
	@Override
	public long ttl(String key) {
		return 0;
	}

	@Override
	public String getType() {
		return "injvm";
	}
	
}
