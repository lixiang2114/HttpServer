package com.github.lixiang2114.netty.handlers;

import com.github.lixiang2114.netty.context.ServerConfig;
import com.github.lixiang2114.netty.context.TcpEventFactory;
import com.github.lixiang2114.netty.event.TcpEvent;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Lixiang
 * @description TCP协议操作器
 * 本操作器是通道隔离的(即一个客户端通道每次请求持有本类的一个特定实例),
 * 但属于同一个IO线程的各个客户端通道可以通过ThreadLocal共享数据
 */
public class TcpChannelHandler extends SimpleChannelInboundHandler<String> {
	/**
	 * TCP事件驱动
	 */
	private TcpEvent tcpEvent;
	
	/**
	 * 服务器配置
	 */
	private ServerConfig serverConfig;
	
	/**
	 * 请求时回调1
	 * @param serverConfig 服务器配置
	 * @param tcpEvent TCP事件消费对象
	 */
	public TcpChannelHandler(ServerConfig serverConfig,TcpEvent tcpEvent) {
		this.tcpEvent=tcpEvent;
		this.serverConfig=serverConfig;
	}

	/**
	 * 请求时回调2
	 * @param context 通道操作上下文
	 * */
    @Override
    public void handlerAdded(ChannelHandlerContext context) throws Exception {
    	if(serverConfig.eventSingleton) return;
    	tcpEvent=TcpEventFactory.getEvent(serverConfig);
    }
    
    /**
	 * 请求时回调3
	 * @param context 通道操作上下文
	 * */
    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
    	tcpEvent.onActived(context);
    }
    
    /**
   	 * 请求时回调4
   	 * @param context 通道操作上下文
   	 * @param message 通道消息内容
   	 * */
    @Override
    protected void channelRead0(ChannelHandlerContext context, String message) throws Exception {
    	tcpEvent.onMessaged(context, message);
    }
    
    /**
   	 * 请求时回调5
   	 * @param context 通道操作上下文
   	 * @param cause 通道异常对象
   	 * */
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
    	tcpEvent.onExceptioned(context, cause);
    }
    
    /**
   	 * 请求时回调6
   	 * @param context 通道操作上下文
   	 * */
    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
    	tcpEvent.onInactive(context);
    }

    /**
   	 * 请求时回调7
   	 * @param context 通道操作上下文
   	 * */
    @Override
    public void handlerRemoved(ChannelHandlerContext context) throws Exception {
    	tcpEvent.onDisconnected(context);
    }
}
