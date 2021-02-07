package com.github.lixiang2114.netty;

import com.github.lixiang2114.netty.context.ServerConfig;
import com.github.lixiang2114.netty.context.SessionScheduler;
import com.github.lixiang2114.netty.handlers.GlobalChannelHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Lixiang
 * @description HTTP服务器
 */
public class HttpServer {
	/**
	 * Http服务器配置
	 */
	public ServerConfig config;
	
	/**
	 * Http服务器关闭操作句柄
	 */
	private ChannelFuture closeFuture;
	
	/**
	 * 客户端会话调度器
	 */
	private SessionScheduler sessionScheduler;
	
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
	 * 全局客户端通道操作器
	 */
	private GlobalChannelHandler globalChannelHandler;
	
	public HttpServer() throws Exception{
		this(null);
	}
	
	public HttpServer(ServerConfig config) throws Exception{
		this.workerThreadPool = new NioEventLoopGroup();
		this.socketThreadPool = new NioEventLoopGroup(1);
		this.config = null==config?new ServerConfig():config;
		this.sessionScheduler=new SessionScheduler(this.config);
		globalChannelHandler=new GlobalChannelHandler(this.config);
	}
	
	/**
	 * 关闭Http服务器
	 * @throws Exception
	 */
	public void shutdownServer() throws Exception{
		if(null!=closeFuture) closeFuture.cancel(true);
		globalChannelHandler.destoryContext();
		config.started=false;
	}
	
	/**
	 * 启动Http服务器
	 * @throws Exception
	 */
	public void startServer() throws Exception{
		try{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(socketThreadPool, workerThreadPool);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childHandler(globalChannelHandler);
			
			setServerChannelParams(config,bootstrap);
			setClientChannelParams(config,bootstrap);
			
			ChannelFuture future = bootstrap.bind(config.port).sync();
			serverChannel=(NioServerSocketChannel)future.channel();
			closeFuture=serverChannel.closeFuture();
			
			System.out.println("HttpServer Listening On Port: " + ((config.started=true)?config.port:"Unkown"));
			sessionScheduler.startupScheduler();
			closeFuture.sync();
		}finally{
			config.started=false;
			serverChannel.close();
			sessionScheduler.destoryScheduler();
			globalChannelHandler.destoryContext();
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
		if(null!=config.maxQueueSize) bootstrap.option(ChannelOption.SO_BACKLOG, config.maxQueueSize);
		if(null!=config.recvBufAllocator) bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, config.recvBufAllocator);
		if(null!=config.msgSizeEsimator) bootstrap.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, config.msgSizeEsimator);
		if(null!=config.maxWriteTimesPerLoop) bootstrap.option(ChannelOption.WRITE_SPIN_COUNT, config.maxWriteTimesPerLoop);
		if(null!=config.writeBufferHigMark) bootstrap.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, config.writeBufferHigMark);
		if(null!=config.writeBufferLowMark) bootstrap.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, config.writeBufferLowMark);
		if(null!=config.serverMaxMsgNumPerRead) bootstrap.option(ChannelOption.MAX_MESSAGES_PER_READ, config.serverMaxMsgNumPerRead);
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
		if(null!=config.clientMaxMsgNumPerRead) bootstrap.childOption(ChannelOption.MAX_MESSAGES_PER_READ, config.clientMaxMsgNumPerRead);
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
		new HttpServer(config).startServer();
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
