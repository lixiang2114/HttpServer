package com.github.lixiang2114.netty.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Lixiang
 * @description 默认驱动事件回调
 */
public class DefaultDriveAction extends AbstractTcpEventAdapter {
	/**
	 * 异常响应消息
	 */
	private String errorRespMsg;
	
	/**
	 * 请求响应消息
	 */
	private String requestRespMsg="OK";
	
	/**
	 * 连接响应消息
	 */
	private String connRespMsg="Connected:Success";
	
	public DefaultDriveAction() {}
	
	public DefaultDriveAction(String connRespMsg) {
		this.connRespMsg=connRespMsg;
	}
	
	public DefaultDriveAction(String connRespMsg,String requestRespMsg) {
		this.connRespMsg=connRespMsg;
		this.requestRespMsg=requestRespMsg;
	}
	
	public DefaultDriveAction(String connRespMsg,String errorRespMsg,String requestRespMsg) {
		this.connRespMsg=connRespMsg;
		this.errorRespMsg=errorRespMsg;
		this.requestRespMsg=requestRespMsg;
	}

	@Override
	public void onActived(ChannelHandlerContext context) throws Exception {
		System.out.println("Connection From:"+context.channel().remoteAddress());
		if(null==connRespMsg) return;
		context.channel().writeAndFlush(connRespMsg+"\n");
	}

	@Override
	public void onExceptioned(ChannelHandlerContext context, Throwable cause) {
		System.out.println("Occur Error:"+cause.getMessage());
		if(null!=errorRespMsg) {
			context.channel().writeAndFlush(errorRespMsg+":"+cause.getMessage()+"\n");
		}else{
			context.channel().writeAndFlush(cause.getMessage()+":\n");
			cause.printStackTrace();
		}
		context.close();
	}

	@Override
	public void onMessaged(ChannelHandlerContext context, String message) throws Exception {
		System.out.println(message);
		if(null==requestRespMsg) return;
		context.channel().writeAndFlush(requestRespMsg+"\n");
	}
}
