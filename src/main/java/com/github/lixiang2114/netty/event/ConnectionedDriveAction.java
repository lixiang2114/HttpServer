package com.github.lixiang2114.netty.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Lixiang
 * @description TCP连接就绪后响应
 */
public abstract class ConnectionedDriveAction extends AbstractTcpEventAdapter{

	@Override
	public void onActived(ChannelHandlerContext context) throws Exception {
		context.channel().writeAndFlush(getResponseMessage()+"\n");
	}
	
	public abstract String getResponseMessage();
}
