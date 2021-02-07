package com.github.lixiang2114.netty.context;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.lixiang2114.netty.scope.HttpSession;
import com.github.lixiang2114.netty.scope.ServletContext;

/**
 * @author Lixiang
 * @description 会话调度器
 */
public class SessionScheduler {
	/**
	 * Http服务器配置
	 */
	private ServerConfig serverConfig;
	
	/**
	 * 会话调度句柄
	 */
	private ScheduledFuture<?> scheduledFuture;
	
	/**
	 * 单线程调度器
	 */
	private ScheduledExecutorService scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
	
	public SessionScheduler(ServerConfig serverConfig){
		this.serverConfig=serverConfig;
		this.scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
	}
	
	/**
	 * 启动会话调度器
	 * @return 会话调度句柄
	 */
	public ScheduledFuture<?> startupScheduler() {
		return this.scheduledFuture=scheduledExecutorService.scheduleWithFixedDelay(()->{
			Collection<HttpSession> httpSessions=ServletContext.getInstance().getAllSessions();
			for(HttpSession session:httpSessions) session.maySelfDestruction(serverConfig.sessionExpire);
		}, serverConfig.sessionSchedulerInitDelay, serverConfig.sessionSchedulerIntervalMillis, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 停止会话调度器
	 */
	public boolean shutdownScheduler() {
		return scheduledFuture.cancel(true);
	}
	
	/**
	 * 销毁会话调度器
	 */
	public void destoryScheduler() {
		if(null!=scheduledFuture ) scheduledFuture.cancel(true);
		scheduledExecutorService.shutdown();
	}

	public ScheduledFuture<?> getScheduledFuture() {
		return scheduledFuture;
	}
}
