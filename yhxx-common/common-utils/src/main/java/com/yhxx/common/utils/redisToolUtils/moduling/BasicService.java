package com.yhxx.common.utils.redisToolUtils.moduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 提供一个基本服务管理功能。
 * 
 * @date 2015年9月7日
 * @author zsp
 */
public class BasicService extends BasicModule implements Service {

	private static final Logger logger = LoggerFactory.getLogger(BasicService.class);
	
	private volatile boolean started;
	private final Set<ServiceEventListener> listeners = new CopyOnWriteArraySet<ServiceEventListener>();
	
	public BasicService() {
		super();
	}
	
	public BasicService(String name) {
		super(name);
	}
	
	/**
	 * 指示模块是否已启动.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 * @return
	 */
	public final boolean isStarted() {
		return started;
	}
	
	/**
	 * 设置模块的状态
	 * 此方法用于内部状态维护，建议不要外部调用；
	 * 用public修饰，是因为用于在动态代理的情况下，代理类能够正确设置状态。
	 * 
	 * @param started
	 */
	public final void setStarted(boolean started) {
		this.started = started;
	}
	
	/**
	 * 注册模块事件.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 * @param listener
	 *            模块事件侦听器。
	 */
	public final void addListener(ServiceEventListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		this.listeners.add(listener);
	}

	/**
	 * 注销模块事件
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 * @param listener
	 *            模块事件侦听器。
	 */
	public final void removeListener(ServiceEventListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * 启动模块.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 */
	@Override
	public final void start() {
		if (!isInitialized()) {
			initialize();
		}
		if (isStarted()) {
			return;
		}
		synchronized(this) {
			if (!isStarted()) {
				final String _name = getName();
				try {
					if(logger.isInfoEnabled()) {
						logger.info(MessageFormat.format("<== {0} is starting... ==>", _name));
					}
					setStarted(true);
					doStart();
					for (ServiceEventListener listener : listeners) {
						listener.started(this);
					}
					if(logger.isInfoEnabled()) {
						logger.info(MessageFormat.format("<== {0} is started. ==>", _name));
					}
				} catch(Throwable t) {
					logger.error(MessageFormat.format("Exception occured when {0} is start.", _name), t);
				} 
			}
		}
	}
	
	
	@Override
	public final void startAsync() {
		if (!isInitialized()) {
			initialize();
		}
		if (isStarted()) {
			return;
		}
		synchronized(this) {
			if (!isStarted()) {
				final String _name = getName();
				if(logger.isInfoEnabled()) {
					logger.info(MessageFormat.format("<== {0} is starting... ==>", _name));
				}
				setStarted(true);
				CompletableFuture.runAsync(() -> {
					doStart();
					for (ServiceEventListener listener : listeners) {
						listener.started(this);
					}
				}).whenComplete((v, e) -> {
					if(e == null) {
						if(logger.isInfoEnabled()) {
							logger.info(MessageFormat.format("<== {0} is started. ==>", _name));
						}
					} else {
						logger.error(MessageFormat.format("Exception occured when {0} is start.", _name), e);
					}
				});
			} 
		}
	}
	
	/**
	 * 停止模块.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 */
	@Override
	public final void stop() {
		if (!isStarted()) {
			return;
		}
		synchronized(this) {
			if (isStarted()) {
				final String _name = getName();
				try {
					if(logger.isInfoEnabled()) {
						logger.info(MessageFormat.format("<== {0} is stoping... ==>", _name));
					}
					for (ServiceEventListener listener : listeners) {
						listener.stoping(this);
					}
					doStop();
				} catch (Throwable t) {
					logger.error(MessageFormat.format("Exception occured when {0} is stop.", _name), t);
				} finally {
					setStarted(false);
					if(logger.isInfoEnabled()) {
						logger.info(MessageFormat.format("<== {0} is stoped. ==>", _name));
					}
				}
			}
		}
		if (isInitialized()) {
			destroy();
		}
	}
	
	/**
	 * 启动模块要做的操作.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 */
	protected void doStart() {
		// do nothing.
	}

	/**
	 * 停止模块要做的操作.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 */
	protected void doStop() {
		// do nothing.
	}
	
}
