package com.github.lixiang2114.netty.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Lixiang
 * @description TCP消息到来后响应
 */
public abstract class MessageDriveAction extends AbstractTcpEventAdapter{

	@Override
	public void onMessaged(ChannelHandlerContext context, String requestMsg) throws Exception {
		context.channel().writeAndFlush(getResponseMessage(requestMsg)+"\n");
	}
	
	public abstract String getResponseMessage(String requestMsg);
}
