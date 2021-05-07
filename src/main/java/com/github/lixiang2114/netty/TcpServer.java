package com.github.lixiang2114.netty;

import com.github.lixiang2114.netty.context.ServerConfig;
import com.github.lixiang2114.netty.handlers.TcpChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Lixiang
 * @description TCP服务端
 */
public class TcpServer {
	/**
	 * TCP服务器配置
	 */
	public ServerConfig config;
	
	/**
	 * TCP服务器关闭操作句柄
	 */
	private ChannelFuture closeFuture;
	
	/**
	 * Socket线程池
	 */
	private EventLoopGroup socketThreadPool;
	
	/**
	 * Worker线程池
	 */
	private EventLoopGroup workerThreadPool;
	
	/**
	 * 服务端通道对象
	 */
	private  NioServerSocketChannel serverChannel;
	
	/**
	 * TCP客户端通道初始化器
	 */
	private TcpChannelInitializer tcpChannelInitializer;
	
	public TcpServer() throws Exception{
		this(null);
	}
	
	public TcpServer(ServerConfig config) throws Exception{
		this.config = null==config?new ServerConfig():config;
		tcpChannelInitializer=new TcpChannelInitializer(this.config);
		this.socketThreadPool = new NioEventLoopGroup(this.config.socketThreadNums);
		this.workerThreadPool = new NioEventLoopGroup(this.config.workerThreadNums);
	}
	
	/**
	 * 关闭TCP服务器
	 * @throws Exception
	 */
	public void shutdownServer() throws Exception{
		if(null!=closeFuture) closeFuture.cancel(true);
		config.started=false;
	}
	
	/**
	 * 启动TCP服务器
	 * @throws Exception
	 */
	public void startServer() throws Exception{
		try{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(socketThreadPool, workerThreadPool);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childHandler(tcpChannelInitializer);
			
			setServerChannelParams(config,bootstrap);
			setClientChannelParams(config,bootstrap);
			
			ChannelFuture future = bootstrap.bind(config.port).sync();
			serverChannel=(NioServerSocketChannel)future.channel();
			closeFuture=serverChannel.closeFuture();
			
			System.out.println("TcpServer Listening On Port: " + ((config.started=true)?config.port:"Unkown"));
			closeFuture.sync();
		}finally{
			config.started=false;
			serverChannel.close();
			socketThreadPool.shutdownGracefully();
			workerThreadPool.shutdownGracefully();
			if(null!=closeFuture) closeFuture.cancel(true);
		}
	}
	
	/**
	 * 设置服务端通道参数
	 */
	private static void setServerChannelParams(ServerConfig config,ServerBootstrap bootstrap){
		if(null!=config.allocator) bootstrap.option(ChannelOption.ALLOCATOR, config.allocator);
		if(null!=config.autoRead) bootstrap.option(ChannelOption.AUTO_READ, config.autoRead);
		if(null!=config.reuseAddr) bootstrap.option(ChannelOption.SO_REUSEADDR, config.reuseAddr);
		if(null!=config.serverRcvBuf) bootstrap.option(ChannelOption.SO_RCVBUF, config.serverRcvBuf);
		bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);
		if(null!=config.maxQueueSize) bootstrap.option(ChannelOption.SO_BACKLOG, config.maxQueueSize);
		if(null!=config.recvBufAllocator) bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, config.recvBufAllocator);
		if(null!=config.msgSizeEsimator) bootstrap.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, config.msgSizeEsimator);
		if(null!=config.maxWriteTimesPerLoop) bootstrap.option(ChannelOption.WRITE_SPIN_COUNT, config.maxWriteTimesPerLoop);
		if(null!=config.serverMaxMsgNumPerRead) {
			bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator().maxMessagesPerRead(config.serverMaxMsgNumPerRead));
		}
	}
	
	/**
	 * 设置客户端通道参数
	 */
	private static void setClientChannelParams(ServerConfig config,ServerBootstrap bootstrap){
		if(null!=config.qos) bootstrap.childOption(ChannelOption.IP_TOS, config.qos);
		if(null!=config.allocator) bootstrap.childOption(ChannelOption.ALLOCATOR, config.allocator);
		if(null!=config.closeMode) bootstrap.childOption(ChannelOption.SO_LINGER, config.closeMode);
		if(null!=config.isKeepLive) bootstrap.childOption(ChannelOption.SO_KEEPALIVE, config.isKeepLive);
		if(null!=config.clientRcvBuf) bootstrap.childOption(ChannelOption.SO_RCVBUF, config.clientRcvBuf);
		if(null!=config.clientSndBuf) bootstrap.childOption(ChannelOption.SO_SNDBUF, config.clientSndBuf);
		if(null!=config.tcpNoDelay) bootstrap.childOption(ChannelOption.TCP_NODELAY, config.tcpNoDelay);
		if(null!=config.halfClose) bootstrap.childOption(ChannelOption.ALLOW_HALF_CLOSURE, config.halfClose);
		if(null!=config.readTimeoutMills) bootstrap.childOption(ChannelOption.SO_TIMEOUT, config.readTimeoutMills);
		if(null!=config.recvBufAllocator) bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, config.recvBufAllocator);
		if(null!=config.connTimeoutMills) bootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.connTimeoutMills);
		if(null!=config.clientMaxMsgNumPerRead) {
			bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator().maxMessagesPerRead(config.clientMaxMsgNumPerRead));
		}
	}
	
	/**
	 * 启动服务器
	 * @throws Exception 
	 */
	public static void start() throws Exception{
		start(null);
	}
	
	/**
	 * 启动服务器
	 * @param config 服务器配置
	 * @throws Exception 
	 */
	public static void start(ServerConfig config) throws Exception{
		new TcpServer(config).startServer();
	}
	
	public ChannelFuture getCloseFuture() {
		return closeFuture;
	}

	public EventLoopGroup getSocketThreadPool() {
		return socketThreadPool;
	}

	public EventLoopGroup getWorkerThreadPool() {
		return workerThreadPool;
	}

	public NioServerSocketChannel getServerChannel() {
		return serverChannel;
	}
}
