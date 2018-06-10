package com.yhxx.common.utils.redisToolUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 定义了缓存的操作
 * 
 * @author zsp
 *
 * @param <T>	缓存的数据类型
 */
public interface Cache<T> {
	
	/**
	 * 获取缓存名
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 获取缓存描述
	 * 
	 * @return
	 */
	String getDescription();
	
	/**
	 * 设置缓存描述
	 * 
	 * @param description
	 */
	void setDescription(String description);
	
	/**
	 * 获取缓存分类，例如injvm, redis等
	 * 
	 * @return
	 */
	String getType();
	
	/**
	 * 缓存是否启用
	 * 
	 * @return
	 */
	boolean isStarted();
	
	/**
	 * 设置缓存失效时间（秒）
	 * 
	 * @param seconds
	 */
	void setExpiredSeconds(int seconds);

	/**
	 * 获取缓存失效时间（秒），0表示缓存不失效。
	 * 
	 * @return
	 */
	int getExpiredSeconds();
	
	/**
	 * 设置是否开启统计缓存的命中次数
	 * 
	 * @param enabled
	 */
	void setEnableStat(boolean enabled);
	
	/**
	 * 获取是否开启了统计缓存的命中次数
	 * 
	 * @return
	 */
	boolean isEnableStat();
	
	/**
	 * 设置是否开启防止缓存击穿
	 * @param enableBreakdownPrevent
	 */
	void setEnableBreakdownPrevent(boolean enableBreakdownPrevent);
	
	/**
	 * 获取是否开启防止缓存击穿
	 * @return
	 */
	boolean isEnableBreakdownPrevent();
	
	/**
	 * 设置防止缓存击穿的空值的缓存失效时间（秒），必须大于0。
	 * 
	 * @param seconds
	 */
	void setBreakdownPreventExpiredSeconds(int seconds);

	/**
	 * 获取防止缓存击穿的空值的缓存失效时间（秒）。
	 * 
	 * @return
	 */
	int getBreakdownPreventExpiredSeconds();
	
	/**
	 * 设置从缓存数据获取键值的代理
	 * 
	 * @param keyMapper
	 */
	void setKeyMapper(Function<T, String> keyMapper);

	/**
	 * 启动缓存
	 */
	void start();

	/**
	 * 关闭缓存
	 */
	void stop();
	
	/**
	 * 添加数据到缓存
	 * 
	 * @param key 	缓存键
	 * @param value 数据
	 */
	void add(String key, T value);
	
	/**
	 * 添加数据到缓存
	 * 
	 * @param values 键-值数据
	 */
	void add(Map<String, T> values);

	/**
	 * 添加数据到缓存
	 *
	 * @param key 	缓存键
	 * @param value 数据
	 * @param expiredSeconds 过期秒值
	 */
	void add(String key, T value, int expiredSeconds);

	/**
	 * 添加数据到缓存
	 *
	 * @param values 键-值数据
	 * @param expiredSeconds 过期秒值
	 */
	void add(Map<String, T> values, int expiredSeconds);

	/**
	 * 从缓存获取数据
	 *
	 * @param key 	缓存键
	 * @return 		数据
	 */
	T get(String key);
	
	/**
	 * 从缓存获取数据
	 *
	 * @param keys 		缓存键
	 * @return 数据
	 */
	List<T> get(Collection<String> keys);

	/**
	 * 从缓存获取数据；若缓存中没有数据，则从指定的代理来获取数据，并更新缓存。
	 *
	 * @param key 		缓存键
	 * @param fetcher 	数据获取的代理（从数据库获取数据）
	 * @return 数据
	 */
	T getAndFetch(String key, Function<String, T> fetcher);
	
	/**
	 * 从缓存获取数据；若缓存中没有数据，则从指定的代理来获取数据，并更新缓存。
	 * 
	 * @param keys			缓存键
	 * @param fetcher		数据获取的代理（从数据库获取数据）
	 * @param multiFetcher	数据获取的代理（从数据库获取数据），用于一次从数据库获取多条记录
	 * @return
	 */
	List<T> getAndFetch(Collection<String> keys,
                        Function<String, T> fetcher,
                        Supplier<List<T>> multiFetcher);
	
	/**
	 * 从缓存删除数据
	 * 
	 * @param key 缓存键
	 */
	void remove(String key);
	
	/**
	 * 从缓存删除数据
	 * 
	 * @param keys 缓存键
	 */
	void remove(Collection<String> keys);
	
	/**
	 * 获取缓存剩余的时间（秒）
	 * 
	 * @param key 缓存键
	 * @return
	 */
	long ttl(String key);
	
	/**
	 * 清空缓存
	 */
	void clear();
	
	/**
	 * 缓存大小
	 */
	int size();

	/**
	 * 获取缓存访问命中次数
	 */
	long getHits();
	
	/**
	 * 获取空值缓存访问命中次数
	 */
	long getEmptyHits();

	/**
	 * 获取缓存访问失败次数
	 */
	long getMisses();
	
}
