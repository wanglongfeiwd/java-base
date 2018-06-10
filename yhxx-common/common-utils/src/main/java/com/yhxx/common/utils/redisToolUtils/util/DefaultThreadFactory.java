package com.yhxx.common.utils.redisToolUtils.util;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.*;

/**
 * 缺省的线程工厂
 * 
 * @author zsp
 *
 */
public class DefaultThreadFactory implements InitializingBean, DisposableBean {

	//获取可用处理器的虚拟机的最大数量，
	private int maxThreads = Runtime.getRuntime().availableProcessors();
	private int maxQueues = 1024;
	private ExecutorService threadPool;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		threadPool = new ThreadPoolExecutor(1, 
				maxThreads,
	            60L, TimeUnit.SECONDS,
	            new LinkedBlockingQueue<Runnable>(maxQueues));
	}
	
	@Override
	public void destroy() throws Exception {
		threadPool.shutdown();
	}
	
	public void execute(Runnable command) {
		if(command == null) {
			return;
		}
		threadPool.execute(command);
	}
	
	public <T> Future<T> submit(Callable<T> task) {
		if(task == null) {
			return null;
		}
		return threadPool.submit(task);
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public int getMaxQueues() {
		return maxQueues;
	}

	public void setMaxQueues(int maxQueues) {
		this.maxQueues = maxQueues;
	}
	
}
