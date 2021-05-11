package com.github.lixiang2114.netty.context;

import java.io.File;
import java.nio.charset.Charset;

import com.github.lixiang2114.netty.event.DefaultDriveAction;
import com.github.lixiang2114.netty.event.Event;
import com.github.lixiang2114.netty.event.TcpEvent;
import com.github.lixiang2114.netty.servlet.DefaultAction;
import com.github.lixiang2114.netty.servlet.Servlet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.util.NettyRuntime;

/**
 * @author Lixiang
 * @description Http服务器配置
 */
@SuppressWarnings("unchecked")
public class ServerConfig {
	/**
	 * 发送数据包的Qos选项
	 */
	public Integer qos;
	
	/**
	 * 应用层配置
	 */
	public Object appConfig;
	
	/**
	 * 服务端是否需要在读取完数据之后自动进行下次读取
	 * 默认值为true
	 */
	public Boolean autoRead;
	
	/**
	 * 服务端口
	 */
	public Integer port=8080;
	
	/**
	 * 服务端通道接收缓冲区大小
	 */
	public Integer serverRcvBuf;
	
	/**
	 * 客户端通道接收缓冲区大小
	 */
	public Integer clientRcvBuf;
	
	/**
	 * 客户端通道发送缓冲区大小
	 */
	public Integer clientSndBuf;
	
	/**
	 * 服务是否启动
	 */
	public Boolean started=false;
	
	/**
	 * 客户端通道关闭模式
	 * -1:close方法无阻塞,立即返回,同时操作系统将数据发送到对端
	 *  0:close方法无阻塞,立即返回,操作系统放弃通道缓冲区中的数据,直接向对端发送RST包,对端收到复位错误
	 *  正整数:阻塞电泳close方法的线程,直到延迟时间到或发送缓冲区中的数据发送完毕,若超时,则对端会收到复位错误
	 */
	public Integer closeMode=-1;
	
	/**
	 * 是否支持半关闭
	 * 设置为false表示在对端关闭通道后,本地端也同步将连接关闭,设置为true表示不关闭本地端连接,
	 * 而是触发ChannelInboundHandler的userEventTriggered()方法,事件为ChannelInputShutdownEvent
	 */
	public Boolean halfClose=false;
	
	/**
	 * 是否允许地址复用
	 * 设置为true表示不同的进程可以不同的IP地址绑定相同的端口
	 */
	public Boolean reuseAddr=true;
	
	/**
	 * 是否与客户端保持长连接
	 * 若设置为false则表示在每次接收完请求后便断开连接通道
	 */
	public Boolean isKeepLive=true;
	
	/**
	 * 是否启用TCP缓冲
	 * 缓冲是以延时为代价换取更少的数据包IO频次以优化网络效率,从而提升吞吐量
	 */
	public Boolean tcpNoDelay=true;
	
	/**
	 * 服务端上报超时时间(单位:毫秒)
	 * 某些场景下也表示接收数据超时时间,默认值为0表示无限期等待,
	 * 若设置一个非负整数值,则在等待时间超过该值时抛出SocketTimeoutException,但输入流并未关闭,可以继续读取数据
	 */
	public Integer readTimeoutMills;
	
	/**
	 * ByteBuf缓冲区分配器
	 * 默认实现为PooledByteBufAllocator
	 */
	public ByteBufAllocator allocator;
	
	/**
	 * Socket线程池尺寸
	 */
	public int socketThreadNums = 1;
	
	/**
	 * 传递数据到应用层前是否去掉分隔符
	 */
	public boolean stripDelimiter=true;
	
	/**
	 * IO连接池最大队列尺寸
	 * 超过此尺寸后,新进入的客户端连接将被丢弃
	 */
	public Integer maxQueueSize=1024;
	
	/**
	 * TCP解析帧最大长度(单位:字节)
	 * 默认32KB
	 */
	public int maxFrameLength=32*1024;
	
	/**
	 * Web应用服务器会话跟踪标识
	 */
	public String sessionId="JSESSIONID";
	
	/**
	 * TCP事件是否为单例全局共享
	 * 单例:全局共享同一个实例
	 * 多例:实例粒度到请求级别
	 */
	public boolean eventSingleton=true;
	
	/**
	 * Servlet是否为单例全局共享
	 * 单例:全局共享同一个实例
	 * 多例:实例粒度到请求级别
	 */
	public boolean servletSingleton=true;
	
	/**
	 * 会话过期时间(单位:毫秒)
	 * 默认30分钟(半小时)
	 */
	public long sessionExpire=30*60*1000L;
	
	/**
	 * 客户端下发超时时间(单位:毫秒)
	 * 某些场景下也表示发送数据超时时间
	 */
	public Integer connTimeoutMills=30000;
	
	/**
	 * 是否保持HTTP会话
	 */
	public boolean enableHttpSession=true;
	
	/**
	 * 服务端Worker线程每次循环执行的最大写次数
	 * 默认值为16次,超过该次数则放入下次循环中去写,以留给其它客户端通道执行写操作的机会,
	 * 该值设置得越大,则客户端通道以更大的延时为代价换取更大的吞吐量;反之,以更小的吞吐量换取更快的实时效率
	 */
	public Integer maxWriteTimesPerLoop=16;
	
	/**
	 * Http协议数据工厂
	 */
	private DefaultHttpDataFactory dataFactory;
	
	/**
	 * 客户端每次读取消息的最大数量
	 */
	public Integer clientMaxMsgNumPerRead=1;
	
	/**
	 * 服务端每次读取消息的最大数量
	 */
	public Integer serverMaxMsgNumPerRead=16;
	
	/**
	 * 每次请求的最大消息体尺寸(单位:字节),默认1MB
	 */
	public Integer maxContentLength=1024*1024;
	
	/**
	 * 消息体存在于内存中的最大尺寸(默认12MB)
	 * 超过该尺寸将通过磁盘缓冲
	 */
	public long maxDataSizeInMemory=0XC00000;
	
	/**
	 * 会话调度初始延迟时间(单位:毫秒)
	 */
	public long sessionSchedulerInitDelay=15000L;
	
	/**
	 * 接收缓冲区分配器
	 * 默认值为AdaptiveRecvByteBufAllocator.DEFAULT
	 */
	public RecvByteBufAllocator recvBufAllocator;
	
	/**
	 * 消息尺寸大小估算器
	 * 默认值为DefaultMessageSizeEstimator.DEFAULT,用于估算ByteBuf、ByteBufHolder和FileRegion的大小
	 * 其中ByteBuf和ByteBufHolder为实际大小,FileRegion估算值为0,该值估算的字节数在计算水位时使用,
	 * FileRegion为0可知FileRegion不影响高低水位
	 */
	public MessageSizeEstimator msgSizeEsimator;
	
	/**
	 * 服务器消息编码类型
	 */
	public Charset charset=Charset.defaultCharset();
	
	/**
	 * 当接收数据长度超出最大帧长度是否快速失败
	 */
	public boolean fastFailOutofMaxFrameLengh=true;
	
	/**
	 * 会话调度间隔(上一次结束到下一次开始)时间(单位:毫秒)
	 */
	public long sessionSchedulerIntervalMillis=120000L;
	
	/**
	 * TCP字节流解析分隔符
	 */
	public ByteBuf[] lineDelimiter=Delimiters.lineDelimiter();
	
	/**
	 * 核心Servlet组件(全局共享)
	 */
	public Class<? extends Servlet> servletClass=DefaultAction.class;
	
	/**
	 * Worker线程池尺寸
	 */
	public int workerThreadNums=2*NettyRuntime.availableProcessors();
	
	/**
	 * TCP事件回调接口类
	 */
	public Class<? extends TcpEvent> eventClass=DefaultDriveAction.class;
	
	/**
	 * Multipart中上传文件存储目录
	 */
	public File uploadDirectory=new File(System.getProperty("user.dir"));
	
	public synchronized HttpDataFactory dataFactory() {
		if(null!=dataFactory) return dataFactory;
		return dataFactory=new DefaultHttpDataFactory(maxDataSizeInMemory);
	}
	
	public ServerConfig(){}
	
	public ServerConfig(int port){
		this.port=port;
	}
	
	public ServerConfig(Class<? extends Event> eventClass){
		if(Servlet.class.isAssignableFrom(eventClass)) {
			this.servletClass=(Class<? extends Servlet>)eventClass;
		}else if(TcpEvent.class.isAssignableFrom(eventClass)) {
			this.eventClass=(Class<? extends TcpEvent>)eventClass;
		}
	}
	
	public ServerConfig(int port,Class<? extends Event> eventClass){
		this.port=port;
		if(Servlet.class.isAssignableFrom(eventClass)) {
			this.servletClass=(Class<? extends Servlet>)eventClass;
		}else if(TcpEvent.class.isAssignableFrom(eventClass)) {
			this.eventClass=(Class<? extends TcpEvent>)eventClass;
		}
	}
	
	public ServerConfig(Object appConfig,Class<? extends Event> eventClass){
		this.appConfig=appConfig;
		if(Servlet.class.isAssignableFrom(eventClass)) {
			this.servletClass=(Class<? extends Servlet>)eventClass;
		}else if(TcpEvent.class.isAssignableFrom(eventClass)) {
			this.eventClass=(Class<? extends TcpEvent>)eventClass;
		}
	}
	
	public ServerConfig(int port,Object appConfig,Class<? extends Event> eventClass){
		this.port=port;
		this.appConfig=appConfig;
		if(Servlet.class.isAssignableFrom(eventClass)) {
			this.servletClass=(Class<? extends Servlet>)eventClass;
		}else if(TcpEvent.class.isAssignableFrom(eventClass)) {
			this.eventClass=(Class<? extends TcpEvent>)eventClass;
		}
	}
}
