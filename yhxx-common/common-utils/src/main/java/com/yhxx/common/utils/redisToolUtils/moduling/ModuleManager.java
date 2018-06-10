package com.yhxx.common.utils.redisToolUtils.moduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 模块管理
 * 
 * @author zsp
 * @date 2016-12-14
 */
public class ModuleManager implements SmartLifecycle {

	private static final Logger logger = LoggerFactory.getLogger(ModuleManager.class);
	private static final AtomicInteger accumulator = new AtomicInteger(10);
	
	private volatile boolean running;
	private final String name;
	private final int phase;
	private final long delaySeconds;
	private final Set<Module> modules;
	private final Set<Service> services;
	
	public ModuleManager(String name) {
		this(name, new CopyOnWriteArraySet<Module>(), new CopyOnWriteArraySet<Service>(), 0);
	}
	
	public ModuleManager(String name, int delaySeconds) {
		this(name, new CopyOnWriteArraySet<Module>(), new CopyOnWriteArraySet<Service>(), delaySeconds);
	}
	
	public ModuleManager(String name, Set<Module> modules) {
		this(name, modules, new CopyOnWriteArraySet<Service>(), 0);
	}
	
	public ModuleManager(String name, Set<Module> modules, int delaySeconds) {
		this(name, modules, new CopyOnWriteArraySet<Service>(), delaySeconds);
	}
	
	public ModuleManager(String name, Set<Module> modules, Set<Service> services) {
		this(name, modules, services, 0);
	}
	
	public ModuleManager(String name, Set<Module> modules, Set<Service> services, int delaySeconds) {
		if(name == null || Objects.equals("", name.trim())) {
			name = "未命名";
		}
		this.name = name;
		this.phase = accumulator.incrementAndGet();
		if(modules == null) {
			modules = new CopyOnWriteArraySet<Module>();
		}
		this.modules = modules;
		if(services == null) {
			services = new CopyOnWriteArraySet<Service>();
		}
		this.services = services;
		this.delaySeconds = delaySeconds;
	}
	
	public final void addModule(Module module) {
		if(this.services.contains(module)) {
			throw new IllegalArgumentException("The module " + module.getName() + " is already exists in services.");
		}
		this.modules.add(module);
	}
	
	public final void removeModule(Module module) {
		this.modules.remove(module);
	}
	
	public final void addService(Service service) {
		if(this.modules.contains(service)) {
			throw new IllegalArgumentException("The service " + service.getName() + " is already exists in modules.");
		}
		this.services.add(service);
	}
	
	public final void removeService(Service service) {
		this.services.remove(service);
	}
	
	@Override
	public final boolean isRunning() {
		return running;
	}
	
	private final void setRunning(boolean running) {
		this.running = running;
	}
	
	public final String getName() {
		return name;
	}

	@Override
	public void start() {
		final String _name = getName();
		if(logger.isInfoEnabled()) {
			logger.info("========== module manager " + _name + " startup ==========");
		}
		if(isRunning()) {
			if(logger.isWarnEnabled()) {
				logger.warn("========== module manager " + _name + " is running. ==========");
			}
			return;
		}
		synchronized (this) {
			if(!isRunning()) {
				setRunning(true);
				final long _delaySeconds = delaySeconds;
				if(_delaySeconds > 0) {
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							doStart();
						}
					}, _delaySeconds * 1000);
				} else {
					doStart();
				}	
			}
		}
	}
	
	private void doStart() {
		for(Module module : modules) {
			module.initialize();
		}
		for(Service service : services) {
			service.start();
		}
	}

	@Override
	public void stop() {
		final String _name = getName();
		if(!isRunning()) {
			if(logger.isWarnEnabled()) {
				logger.warn("========== module manager " + _name +" has stopped. ==========");
			}
			return;
		}
		synchronized (this) {
			if(isRunning()) {
				for(Service service : services) {
					service.stop();
				}
				for(Module module : modules) {
					module.destroy();
				}
				setRunning(false);
			}
		}
		if(logger.isInfoEnabled()) {
			logger.info("========== module manager " + _name +" shutdown ==========");
		}
	}

	@Override
	public int getPhase() {
		return phase;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable runnable) {
		if(runnable != null) {
			runnable.run();
		}
		stop();
	}

}
