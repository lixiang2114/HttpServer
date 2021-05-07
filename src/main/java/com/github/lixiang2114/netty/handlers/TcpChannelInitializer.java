package com.github.lixiang2114.netty.handlers;

import com.github.lixiang2114.netty.context.ServerConfig;
import com.github.lixiang2114.netty.context.TcpEventFactory;
import com.github.lixiang2114.netty.event.TcpEvent;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author Lixiang
 * @description TCP初始化器
 */
public class TcpChannelInitializer extends ChannelInitializer<SocketChannel> {
	/**
	 * 全局TCP事件
	 */
	private TcpEvent tcpEvent;
	
	/**
	 * 服务器配置
	 */
	private ServerConfig serverConfig;
	
	/**
	 * 通道编码操作器
	 */
	private ChannelHandler encoderChannelHandler;
	
	/**
	 * 通道解码操作器
	 */
	private ChannelHandler decoderChannelHandler;
	
	/**
	 * 通道分隔符操作器
	 */
	private ChannelHandler delimiterChannelHandler;
	
	/**
	 * 构造器(服务启动时回调)
	 * @param config TCP服务器配置
	 */
	public TcpChannelInitializer(ServerConfig serverConfig) throws Exception{
		this.serverConfig=serverConfig;
		this.encoderChannelHandler=new StringEncoder(serverConfig.charset);
		this.decoderChannelHandler=new StringDecoder(serverConfig.charset);
		if(serverConfig.eventSingleton) this.tcpEvent=TcpEventFactory.getEvent(serverConfig);
		this.delimiterChannelHandler=new DelimiterBasedFrameDecoder(serverConfig.maxFrameLength, serverConfig.lineDelimiter);
	}
	
	/**
	 * 初始化TCP协议通道
	 * */
	@Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("framer", delimiterChannelHandler);
        pipeline.addLast("decoder", decoderChannelHandler);
        pipeline.addLast("encoder", encoderChannelHandler);
        pipeline.addLast("handler", new TcpChannelHandler(serverConfig,tcpEvent));
    }
}
