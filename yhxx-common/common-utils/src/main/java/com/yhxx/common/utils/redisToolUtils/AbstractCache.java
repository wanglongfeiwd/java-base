package com.yhxx.common.utils.redisToolUtils;

import com.yhxx.common.utils.redisToolUtils.moduling.BasicService;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 实现了缓存的基本操作，具体的缓存存储访问由子类实现；
 * 
 * 对getAndFetch方法的缓存写进行加锁，以保证多线程并发下数据的安全性；
 * 其它方法，如add,get,remove,clear等未提供对缓存读写方法的互斥，实现子类自己保证数据多线程并发下的安全性。
 * 
 * 更新数据库数据时，建议直接淘汰缓存，而不是更新缓存，这样可避免在多线程并发下数据不安全的问题，例如：
 * 假如A线程在更新数据库后，直接更新缓存；B线程在A线程更新数据库前从数据库取到旧数据然后更新缓存，
 * 有可能覆盖A对缓存的更新结果；因此直接更新缓存是多余的操作。
 * 建议A线程在更新数据库时，做一个2次删除操作，顺序是：先删除缓存->更新数据库->延时删除缓存（根据经验设置延时时间），
 * 这样一定程度上避免B线程在A线程更新数据库前从数据库加载到旧数据去更新缓存，缓存存储的数据与数据库不一致。
 * 另外，对缓存设置时效，也可一定程度上保证缓存存储的数据与数据库的一致。
 * 
 * @author zsp
 *
 * @param <T>	缓存的数据类型
 */
public abstract class AbstractCache<T> extends BasicService implements Cache<T> {
	
	/**
	 * 空值，用于设置不存在的缓存键的值，以防止缓存击穿
	 */
	protected final static String EMPTY_VALUE = ".";
    
    /**
     * 缓存描述
     */
	private String description;

    /**
     * 过期时间(秒)
     */
    private int expiredSeconds = 0;
    
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
    private int breakdownPreventExpiredSeconds;
    
    /**
     * 
     * @param name
     */
    public AbstractCache(String name) {
        super(name);
        if (name == null || "".equals(name.trim())) {
            throw new IllegalArgumentException("The name should not be null or empty");
        }
        this.description = name;
    }
    
    /**
     * 添加数据到缓存
     *
     * @param key   缓存键
     * @param value 数据
     */
    @Override
    public final void add(String key, T value) {
        add(key, value, getExpiredSeconds());
    }
    
    /**
	 * 添加数据到缓存
	 * 
	 * @param values 数据
	 */
    @Override
	public final void add(Map<String, T> values) {
    	add(values, getExpiredSeconds());
	}
    
    /**
     * 添加数据到缓存，留给子类实现
     *
     * @param key   缓存键
     * @param value 数据
     */
    protected abstract void addToCache(String key, T value);
    
    /**
     * 添加数据到缓存，留给子类实现
     *
     * @param values 数据
     */
    protected abstract void addToCache(Map<String, T> values);


    /**
     * 添加数据到缓存
     *
     * @param key   缓存键
     * @param value 数据
     * @param expiredSeconds 过期秒值
     */
    @Override
    public final void add(String key, T value, int expiredSeconds) {
        if (isStarted()) {
            validateKey(key);
            validateValue(value);
            addToCache(key, value, expiredSeconds);
        }
    }

    /**
     * 添加数据到缓存
     *
     * @param values 数据
     * @param expiredSeconds 过期秒值
     */
    @Override
    public final void add(Map<String, T> values, int expiredSeconds) {
        if (isStarted()) {
            if(values == null || values.size() == 0) {
                throw new IllegalArgumentException("The values is null.");
            }
            for(Map.Entry<String, T> entry : values.entrySet()) {
                validateKey(entry.getKey());
                validateValue(entry.getValue());
            }
            addToCache(values, expiredSeconds);
        }
    }

    /**
     * 添加数据到缓存，留给子类实现
     *
     * @param key   缓存键
     * @param value 数据
     */
    protected abstract void addToCache(String key, T value, int expiredSeconds);

    /**
     * 添加数据到缓存，留给子类实现
     *
     * @param values 数据
     */
    protected abstract void addToCache(Map<String, T> values, int expiredSeconds);

    /**
     * 从缓存获取数据
     *
     * @param key 缓存键
     * @return 数据
     */
    @Override
    public final T get(String key) {
    	if (isStarted()) {
    		validateKey(key);
    		CachedObject<T> cacheObj = getFromCache(key);
            if (cacheObj.isNull()) {
                incrMiss();
            } else {
            	if(cacheObj.isEmpty()) {
            		incrEmptyHit();
            	} else if(cacheObj.getData() != null) {
            		handleHit(key);
            	}
            }
            return cacheObj.getData();
        } else {
            return null;
        }
    }
    
    /**
	 * 从缓存获取数据
	 *
	 * @param keys 缓存键
	 */
    @Override
	public final List<T> get(Collection<String> keys) {
    	List<T> list = new ArrayList<T>();
    	if (isStarted()) {
    		if(keys == null || keys.size() == 0) {
        		throw new IllegalArgumentException("The keys is null.");
        	}
        	for(String key : keys) {
        		validateKey(key);
        	}
            //从缓存中获取对象
			List<CachedObject<T>> cacheObjList = getFromCache(keys); 
			for(CachedObject<T> cacheObj : cacheObjList) {
	            if (cacheObj.isNull()) {
	                incrMiss();
	            } else {
	            	if(cacheObj.isEmpty()) {
	            		incrEmptyHit();
	            	} else if(cacheObj.getData() != null) {
		                incrHit();
		                list.add(cacheObj.getData());
	            	}
	            }
			}
        } 
    	return list;
	}

    /**
     * 从缓存获取数据；若缓存中没有数据，则从指定的数据获取代理中来返回数据，并置入缓存。
     *
     * @param key     缓存键
     * @param fetcher 数据获取代理（一般从数据库获取数据）
     * @return 数据
     */
    @Override
    public final T getAndFetch(String key,
			Function<String, T> fetcher) {
    	if (fetcher == null) {
            throw new IllegalArgumentException("fetcher");
        }
        if (isStarted()) {
        	validateKey(key);
            CachedObject<T> cacheObj = getFromCache(key);
            if (cacheObj.isNull()) {
                incrMiss();
                //从数据获取代理中查找对象，并更新缓存
                cacheObj = fetchObjectWithLock(key, fetcher);
            } else {
            	if (cacheObj.isEmpty()) {
            		incrEmptyHit();
            	} else if(cacheObj.getData() != null) {
            		handleHit(key);
            	}
            }
            return cacheObj.getData();
        } else {
            return fetcher.apply(key);
        }
    }
    
    /**
	 * 从缓存获取数据；若缓存中没有数据，则从指定的代理来获取数据，并更新缓存。
	 * 
	 * @param keys	缓存键
	 * @param fetcher	数据获取的代理（一般从数据库获取数据）
	 * @param multiFetcher	数据获取的代理（一般从数据库获取数据）
	 * @return
	 */
    public final List<T> getAndFetch(Collection<String> keys, 
			Function<String, T> fetcher,
    		Supplier<List<T>> multiFetcher) {
    	if (fetcher == null) {
            throw new IllegalArgumentException("fetcher");
        }
        if (isStarted()) {
        	if(keys == null || keys.size() == 0) {
        		throw new IllegalArgumentException("The keys is null.");
        	}
        	for(String key : keys) {
        		validateKey(key);
        	}
        	List<T> list = new ArrayList<T>();
            List<CachedObject<T>> cacheObjList = getFromCache(keys);
            Iterator<CachedObject<T>> it = cacheObjList.iterator();
            while(it.hasNext()) {
            	CachedObject<T> cacheObj = it.next();
            	if (cacheObj.isNull()) {
            		incrMiss();
            		//从数据获取代理中查找对象，并更新缓存
            		cacheObj = fetchObjectWithLock(cacheObj.getKey(), fetcher);
            	} else {
            		if (cacheObj.isEmpty()) {
            			incrEmptyHit();
            		} else if(cacheObj.getData() != null) {
            			handleHit(cacheObj.getKey());
            		}
            	}
            	T data = cacheObj.getData();
            	if(data != null) {
            		list.add(data);
            	}
            }
            return list;
        } else {
        	if(multiFetcher != null) {
        		return multiFetcher.get();
        	} else {
        		return new ArrayList<T>();
        	}
        }
    }
    
    /**
     * 加锁获取缓存对象，子类可以重写此方法，例如当需要引入分布式锁的场景
     * 
     * @param key		缓存键
     * @param fetcher	数据获取代理，例如从数据库获取数据
     * @return
     */
    protected CachedObject<T> fetchObjectWithLock(String key,
    		Function<String, T> fetcher) {
    	CachedObject<T> cacheObj;
        synchronized (this) {//加锁防止多线程并发下的重复多次更新缓存
        	//再次访问缓存，即double check，防止多线程并发下，多次访问数据库
            cacheObj = getFromCache(key);
            if (cacheObj.isNull()) {
                cacheObj = fetchObject(key, fetcher);
            } 
        }
        if (cacheObj.isEmpty()) {
        	incrEmptyHit();
    	} else if(cacheObj.getData() != null) {
    		handleHit(key);
    	}
        return cacheObj;
    }
    
    /**
     * 获取缓存对象
     * @param key		缓存键
     * @param fetcher  	数据获取代理，例如从数据库获取数据
     * @return
     */
    protected final CachedObject<T> fetchObject(String key, 
    		Function<String, T> fetcher) {
    	CachedObject<T> cacheObj = null;
    	//从数据库获取数据
        final T data = fetcher.apply(key);
        if(data != null) {
        	cacheObj = new CachedObject<T>(key, data);
        	addCachedObject(key, cacheObj);
        	int expiredSeconds = getExpiredSeconds();
            if (expiredSeconds > 0) {
            	expire(key, expiredSeconds);
            }
        } else {
        	if (isEnableBreakdownPrevent()) {
            	//空数据处理，防止缓存击穿
            	cacheObj = new CachedObject<T>(key).asEmpty();
            	addCachedObject(key, cacheObj);
            	int expiredSeconds = getBreakdownPreventExpiredSeconds();
                if (expiredSeconds > 0) {
                	expire(key, expiredSeconds);
                } else {
                	expire(key, 1);
                }
            } else {
            	cacheObj = new CachedObject<T>(key);
            }
        }
        return cacheObj;
    }
    
    protected final void handleHit(String key) {
    	incrHit();
        int expiredSeconds = getExpiredSeconds();
        if (expiredSeconds > 0) {
        	//延长缓存的生命周期
        	expire(key, expiredSeconds);
        }
    }
    
    private void addCachedObject(String key, CachedObject<T> value) {
    	if(value.isEmpty()) {
    		addEmptyToCache(key);
		} else {
			addToCache(key, value.getData());
		}
    }
    
    @SuppressWarnings("unchecked")
	protected final <V> CachedObject<T> asCachedObject(String key, 
			V value, 
    		Function<V, T> converter) {
    	if(value == null) {
			return new CachedObject<T>(key);
		} else if(isEmptyValue(value)) {
			return new CachedObject<T>(key).asEmpty();
		} else {
			if(converter != null) {
				return new CachedObject<T>(key, converter.apply(value));
			} else {
				return new CachedObject<T>(key, (T)value);
			}
		}
    }
    
    /**
     * 添加空值，防止缓存击穿，留给子类实现
     * 
     * @param key
     */
    protected abstract void addEmptyToCache(String key);
    
    /**
     * 判断实际缓存的对象是否是空值，留给子类实现
     * 
     * @param value
     * @return
     */
    protected abstract <V> boolean isEmptyValue(V value);
    
    /**
     * 从缓存获取数据，留给子类实现
     *
     * @param key 缓存键
     * @return 数据
     */
    protected abstract CachedObject<T> getFromCache(String key);
    
    /**
     * 从缓存获取数据，留给子类实现
     *
     * @param keys 			缓存键
     * @param keyMapper 	从对象获取缓存键的映射代理
     * @return 				数据
     */
    protected abstract List<CachedObject<T>> getFromCache(Collection<String> keys);
    
    /**
     * 延长缓存的过期时间（秒），留给子类实现
     *
     * @param key     缓存键
     * @param seconds 过期的时间（秒）
     */
    protected abstract void expire(String key, int seconds);
    
    /**
     * 延长缓存的过期时间（秒），留给子类实现
     *
     * @param keys     缓存键
     * @param seconds 过期的时间（秒）
     */
    protected abstract void expire(Collection<String> keys, int seconds);
    
    /**
     * 从缓存删除数据
     *
     * @param key 缓存键
     */
    @Override
    public final void remove(String key) {
        if (isStarted()) {
        	validateKey(key);
        	removeFromCache(key);
        }
    }
    
    /**
	 * 从缓存删除数据
	 * 
	 * @param keys 缓存键
	 */
    @Override
	public final void remove(Collection<String> keys) {
    	if (isStarted()) {
    		if(keys == null || keys.size() == 0) {
        		throw new IllegalArgumentException("The keys is null.");
        	}
        	for(String key : keys) {
        		validateKey(key);
        	}
        	removeFromCache(keys);
        }
	}
    
    /**
     * 从缓存删除数据，留给子类实现
     *
     * @param key 缓存键
     */
    protected abstract void removeFromCache(String key);
    
    /**
     * 从缓存删除数据，留给子类实现
     *
     * @param keys 缓存键
     */
    protected abstract void removeFromCache(Collection<String> keys);
    
    /**
     * 清空缓存
     */
    @Override
    public final void clear() {
        if (isStarted()) {
        	clearCache();
        }
    }
    
    /**
     * 清空缓存，留给子类实现
     */
    protected abstract void clearCache();

    /**
     * 缓存大小
     */
    @Override
    public final int size() {
        if (isStarted()) {
            return sizeOfCache();
        }
        return 0;
    }
    
    /**
     * 缓存大小
     */
    protected int sizeOfCache() {
        return 0;
    }
    
    /**
     * 增加缓存命中成功次数，留给子类实现
     */
    protected abstract void hit();

    protected final void incrHit() {
        if (isEnableStat()) {
            hit();
        }
    }
    
    /**
     * 增加空值缓存命中成功次数，留给子类实现
     */
    protected abstract void emptyHit();

    protected final void incrEmptyHit() {
        if (isEnableStat()) {
        	emptyHit();
        }
    }

    /**
     * 增加缓存命中失败次数，留给子类实现
     */
    protected abstract void miss();

    protected final void incrMiss() {
        if (isEnableStat()) {
            miss();
        }
    }

    /**
     * 获取缓存命中成功次数
     * 
     * @return
     */
    @Override
    public abstract long getHits();
    
    /**
	 * 获取空值缓存访问命中次数
	 * 
	 * @return
	 */
    @Override
    public abstract long getEmptyHits();

    /**
     * 获取缓存命中失败次数
     * 
     * @return
     */
    @Override
    public abstract long getMisses();
    
    private void validateKey(String key) {
    	if(!StringUtils.hasText(key)) {
    		throw new IllegalArgumentException("The key is null or empty!");
    	}
    }
    
    private void validateValue(T value) {
    	if(value == null) {
    		throw new IllegalArgumentException("The value is null!");
    	}
    	if(EMPTY_VALUE.equals(value)) {
    		throw new IllegalArgumentException("invalid value");
    	}
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Cache) {
        	Cache<?> target = (Cache<?>) obj;
            return Objects.equals(this.name, target.getName());
        }

        return false;
    }

    @Override
    public final int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String getDescription() {
		return description;
	}
    
    @Override
    public void setDescription(String description) {
    	this.description = description;
    }

    @Override
	public int getExpiredSeconds() {
		return expiredSeconds;
	}

    @Override
	public void setExpiredSeconds(int expiredSeconds) {
		this.expiredSeconds = expiredSeconds;
	}

	@Override
	public boolean isEnableStat() {
		return enableStat;
	}

    @Override
	public void setEnableStat(boolean enableStat) {
		this.enableStat = enableStat;
	}

    @Override
	public boolean isEnableBreakdownPrevent() {
		return enableBreakdownPrevent;
	}

    @Override
	public void setEnableBreakdownPrevent(boolean enableBreakdownPrevent) {
		this.enableBreakdownPrevent = enableBreakdownPrevent;
	}

    @Override
	public int getBreakdownPreventExpiredSeconds() {
		return breakdownPreventExpiredSeconds;
	}

    @Override
	public void setBreakdownPreventExpiredSeconds(int breakdownPreventExpiredSeconds) {
    	if(breakdownPreventExpiredSeconds < 1) {
			throw new IllegalArgumentException("The value should be larger than 0.");
		}
		this.breakdownPreventExpiredSeconds = breakdownPreventExpiredSeconds;
	}
	
}
