package com.yhxx.common.utils.redisToolUtils.moduling;

/**
 * Logical, server-managed entities must implement this interface. A module
 * represents an operational unit and may contain zero or more services
 * and rely on zero or more services that may be hosted by the container.
 * <p>
 * The Module interface is intended to provide the simplest mechanism
 * for creating, deploying, and managing server modules.
 * </p>
 *
 * @date 2015年9月7日
 * @author zsp
 */
public interface Module {

	/**
     * Returns the name of the module for display in administration interfaces.
     *
     * @return The name of the module.
     */
    String getName();

    /**
     * Initialize the module.
     */
    void initialize();

    /**
     * Module should free all resources and prepare for deallocation.
     */
    void destroy();
	
}
