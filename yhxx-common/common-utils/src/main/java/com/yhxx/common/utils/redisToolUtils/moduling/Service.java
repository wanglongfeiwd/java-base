package com.yhxx.common.utils.redisToolUtils.moduling;

/**
 * 服务
 * 
 * @author zsp
 * @date 2016-12-14
 */
public interface Service extends Module {

	/**
     * Start the module.
     */
    void start();
    
    /**
     * Start the module (must return quickly). Any long running
     * operations should spawn a thread and allow the method to return
     * immediately.
     */
    void startAsync();
    
    /**
     * Stop the module. The module should attempt to free up threads
     * and prepare for either another call to initialize (reconfigure the module)
     * or for destruction.
     */
    void stop();
	
}
