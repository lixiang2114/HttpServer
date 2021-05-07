package com.github.lixiang2114.netty.event;

import com.github.lixiang2114.netty.context.ServerConfig;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Lixiang
 * @description TCP事件适配器
 */
public abstract class AbstractTcpEventAdapter implements TcpEvent{
	/**
	 * 服务器配置
	 */
	protected ServerConfig serverConfig;

	@Override
	public void onActived(ChannelHandlerContext context) throws Exception {}

	@Override
	public void onExceptioned(ChannelHandlerContext context, Throwable cause) {}

	@Override
	public void onMessaged(ChannelHandlerContext context, String message) throws Exception {}

	@Override
	public void onInactive(ChannelHandlerContext context) throws Exception {}

	@Override
	public void onDisconnected(ChannelHandlerContext context) throws Exception {}
}
