package com.yhxx.common.utils.redisToolUtils.moduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

/**
 * 提供一个基本模块管理功能。
 * 
 * @date 2015年9月7日
 * @author zsp
 */
public class BasicModule implements Module {

	private static final Logger logger = LoggerFactory.getLogger(BasicModule.class);

	protected volatile boolean initialized;
	protected String name;
	
	public BasicModule() {
		
	}
	
	public BasicModule(String name) {
		this.name = name;
	}
	
	/**
	 * 指示模块是否初始化.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 * @return
	 */
	public final boolean isInitialized() {
		return initialized;
	}
	
	/**
	 * 设置模块的状态
	 * 此方法用于内部状态维护，建议不要外部调用；
	 * 用public修饰，是因为用于在动态代理的情况下，代理类能够正确设置状态。
	 * 
	 * @param initialized
	 */
	public final void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	/**
	 * 获取模块名称.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 * @return
	 */
	@Override
	public String getName() {
		if(StringUtils.hasText(name)) {
			return name;
		}
		return this.getClass().getName();
	}

	/**
	 * 初始化模块.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 */
	@Override
	public final void initialize() {
		if (isInitialized()) {
			return;
		}
		synchronized (this) {
			if (!isInitialized()) {
				final String _name = getName();
				try {
					if(logger.isInfoEnabled()) {
						logger.info(MessageFormat.format("<== {0} is initializing... ==>", _name));
					}
					setInitialized(true);
					doInitialize();
					if(logger.isInfoEnabled()) {
						logger.info(MessageFormat.format("<== {0} is initialized. ==>", _name));
					}
				} catch (Throwable t) {
					logger.error(MessageFormat.format("Exception occured when {0} is initialize.", _name), t);
				}
			}
		}
	}

	/**
	 * 销毁模块.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 */
	@Override
	public final void destroy() {
		if (!isInitialized()) {
			return;
		}
		synchronized (this) {
			if (isInitialized()) {
				final String _name = getName();
				try {
					if(logger.isInfoEnabled()) {
						logger.info(MessageFormat.format("<== {0} is destroying... ==>", _name));
					}
					doDestroy();
				} catch (Throwable t) {
					logger.error(MessageFormat.format("Exception occured when {0} is destroy.", _name), t);
				} finally {
					setInitialized(false);
					if(logger.isInfoEnabled()) {
						logger.info(MessageFormat.format("<== {0} is destroyed. ==>", _name));
					}
				}
			}
		}
	}

	/**
	 * 初始化模块要做的操作.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 */
	protected void doInitialize() {
		// do nothing.
	}

	/**
	 * 销毁模块要做的操作.
	 *
	 * @date 2015年9月7日
	 * @author zsp
	 */
	protected void doDestroy() {
		// do nothing.
	}

}
