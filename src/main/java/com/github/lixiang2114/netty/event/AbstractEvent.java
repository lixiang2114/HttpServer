package com.github.lixiang2114.netty.event;

import com.github.lixiang2114.netty.context.ServerConfig;

/**
 * @author Lixiang
 * @description 抽象事件
 */
public abstract class AbstractEvent implements Event{
	/**
	 * 服务器配置
	 */
	protected ServerConfig serverConfig;
}
