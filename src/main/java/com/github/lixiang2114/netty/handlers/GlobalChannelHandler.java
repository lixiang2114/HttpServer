package com.github.lixiang2114.netty.handlers;

import com.github.lixiang2114.netty.context.HttpServletFactory;
import com.github.lixiang2114.netty.context.ServerConfig;
import com.github.lixiang2114.netty.scope.ServletContext;
import com.github.lixiang2114.netty.servlet.Servlet;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author Lixiang
 * @description 全局通道操作器
 * 由于ChannelInitializer是被共享的,因此本操作器实例被多客户端通道所共享
 */
public class GlobalChannelHandler extends ChannelInitializer<SocketChannel>{
	/**
	 * 服务器配置
	 */
	private ServerConfig serverConfig;
	
	/**
	 * 全局Servlet组件
	 */
	private Servlet dispatcherServlet;
	
	/**
	 * 全局Servlet上下文
	 */
	private ServletContext servletContext;
	
	/**
	 * 构造器(服务启动时回调)
	 * @param config Http服务器配置
	 */
	public GlobalChannelHandler(ServerConfig serverConfig) throws Exception{
		this.serverConfig=serverConfig;
		this.servletContext=ServletContext.getInstance();
		if(serverConfig.servletSingleton) this.dispatcherServlet=HttpServletFactory.getServlet(serverConfig);
	}

	/**
	 * 请求时回调0
	 * 各客户端的每次请求都将调用此方法(此方法的调用粒度是请求级别)
	 * @param channel 客户端通道
	 */
	@Override
    public void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeLine=channel.pipeline();
		pipeLine.addLast("httpCodec",new HttpServerCodec());
		pipeLine.addLast("httpAggregator",new HttpObjectAggregator(serverConfig.maxContentLength));
		pipeLine.addLast("httpHandler",new HttpChannelHandler(serverConfig,servletContext,dispatcherServlet));
    }
	
	/**
	 * 销毁Servlet上下文
	 */
	public void destoryContext() {
		try {
			if(null!=this.dispatcherServlet) this.dispatcherServlet.destory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null!=this.servletContext) this.servletContext.clearAllAttributes();
		this.dispatcherServlet=null;
		this.servletContext=null;
	}
}
