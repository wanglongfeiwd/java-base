package com.yhxx.common.utils.redisToolUtils.util;


import com.yhxx.common.utils.redisToolUtils.DelayEvictionCache;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延迟删除缓存的队列
 * 
 * @author zsp
 *
 */
public class DelayEvictionQueue {
	
	private final static String THREAD_NAME = "cache-delay-eviction";
	private final static String COMMAND_STOP = "$stop";
	
	private final DelayQueue<CacheEvictionMessage> messageQueue;
	private final DelayEvictionCache<?> cache;
	private DefaultThreadFactory workerThreadFactory;

	public DelayEvictionQueue(DelayEvictionCache<?> cache) {
		this.cache = cache;
		this.messageQueue = new DelayQueue<CacheEvictionMessage>();
	}
	
	public DelayEvictionQueue(DelayEvictionCache<?> cache, 
			DefaultThreadFactory workerThreadFactory) {
		this(cache);
		this.workerThreadFactory = workerThreadFactory;
	}
	
	void start() {
		final Thread masterThread = new Thread(() -> {
			for(;;) {
				CacheEvictionMessage message;
				try {
					message = messageQueue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
				final String key = message.getKey();
				if(COMMAND_STOP.equals(key)) {
					break;
				}
				if(workerThreadFactory != null) {
					workerThreadFactory.execute(() -> {
						cache.remove(message.getKey());
					});
				} else {
					cache.remove(message.getKey());
				}
			}
		});
		masterThread.setDaemon(true);
		masterThread.setName(THREAD_NAME);
		masterThread.start();
	}
	
	void stop() {
		messageQueue.put(new CacheEvictionMessage(COMMAND_STOP, cache.getDelayEvictMillis()));
	}
	
	public void evict(String key) {
		if(COMMAND_STOP.equals(key)) {
			throw new IllegalArgumentException(COMMAND_STOP + " is a reserve key");
		}
		messageQueue.put(new CacheEvictionMessage(key, cache.getDelayEvictMillis()));
	}
	
	static class CacheEvictionMessage implements Delayed {

		final private String key;
		final private long timestamp;
		
		CacheEvictionMessage(String key, long timeout) {
			this.key = key;
			this.timestamp = System.currentTimeMillis() + timeout;;
		}

		String getKey() {
			return key;
		}
		
		long getTimestamp() {
			return timestamp;
		}

		@Override
		public int compareTo(Delayed other) {
			 return (int)(timestamp - ((CacheEvictionMessage)other).getTimestamp());
		}

		@Override
		public long getDelay(TimeUnit unit) {
			long result = timestamp - System.currentTimeMillis();
	        return result;
		}
		
	}
	
}
