package com.yhxx.common.utils.redisToolUtils.moduling;


/**
 * 服务事件侦听器。
 * 
 * @date 2015年9月7日
 * @author zsp
 */
public interface ServiceEventListener {

	/**
	 * 模块启动后引发的事件
	 * 
	 * @date 2015年9月7日
	 * @author zsp
	 * @param service
	 */
	void started(Service service);
	
	/**
	 * 模块正在结束引发的事件
	 * 
	 * @date 2015年9月7日
	 * @author zsp
	 * @param service
	 */
	void stoping(Service service);
	
}
