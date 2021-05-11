package com.github.lixiang2114.netty.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Lixiang
 * @description TCP事件接口
 */
public interface TcpEvent extends Event{
	/**
	 * 组件初始化
	 */
	public void init() throws Exception;
	
    /**
   	 * 回调2:连接被激活后触发
   	 * */
    public void onActived(ChannelHandlerContext context) throws Exception;
       
    /**
   	 * 回调3:连接发生异常后触发
   	 * */
    public void onExceptioned(ChannelHandlerContext context, Throwable cause);
    
    /**
	 * 回调3:有客户端消息到来时触发
	 * */
    public void onMessaged(ChannelHandlerContext context, String message) throws Exception;
    
    /**
   	 * 回调4:连接被钝化后触发
   	 * */
    public void onInactive(ChannelHandlerContext context) throws Exception;

    /**
	 * 回调5:连接断开之后触发
	 * */
    public void onDisconnected(ChannelHandlerContext context) throws Exception;
    
    /**
	 * 组件销毁
	 */
	public void destory() throws Exception;
}
