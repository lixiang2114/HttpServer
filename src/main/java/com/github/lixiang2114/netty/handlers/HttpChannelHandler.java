package com.github.lixiang2114.netty.handlers;

import com.github.lixiang2114.netty.consts.ServerConst;
import com.github.lixiang2114.netty.context.HttpServletFactory;
import com.github.lixiang2114.netty.context.ServerConfig;
import com.github.lixiang2114.netty.scope.HttpServletRequest;
import com.github.lixiang2114.netty.scope.HttpServletResponse;
import com.github.lixiang2114.netty.scope.ServletContext;
import com.github.lixiang2114.netty.servlet.Servlet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.ReferenceCountUtil;

/**
 * @author Lixiang
 * @description HTTP协议操作器
 * 本操作器是通道隔离的(即一个客户端通道每次请求持有本类的一个特定实例),
 * 但属于同一个IO线程的各个客户端通道可以通过ThreadLocal共享数据
 */
public class HttpChannelHandler extends ChannelInboundHandlerAdapter{
	/**
	 * 核心Servlet组件
	 */
	private Servlet dispatcherServlet;
	
	/**
	 * 服务器配置
	 */
	private ServerConfig serverConfig;
	
	/**
	 * HttpServlet请求对象
	 */
	private HttpServletRequest request;
	
	/**
	 * Servlet上下文对象
	 */
	private ServletContext servletContext;
	
	/**
	 * 请求时回调1
	 * @param serverConfig 服务器配置
	 * @param servletContext Servlet上下文
	 * @param dispatcherServlet 核心控制器
	 */
	public HttpChannelHandler(ServerConfig serverConfig,ServletContext servletContext,Servlet dispatcherServlet) {
		this.serverConfig=serverConfig;
		this.servletContext=servletContext;
		this.dispatcherServlet=dispatcherServlet;
	}
    
	/**
	 * 请求时回调3
	 * @param ctx 通道操作上下文
	 * @param object HTTP请求对象
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception{
		if(!(object instanceof HttpRequest)){
			ReferenceCountUtil.release(object);
			return;
		}
		
		HttpRequest httpRequest=(HttpRequest) object;
		if(ServerConst.FAVICON_ICO.equals(httpRequest.uri())) return;
		
		this.request=new HttpServletRequest(ctx.channel(),httpRequest,serverConfig,servletContext);
		try{
			dispatcherServlet.service(request, new HttpServletResponse(request));
		}catch(Throwable cause){
			HttpServletResponse response=new HttpServletResponse(request,cause);
			response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
			dispatcherServlet.exception(request, response);
		}finally{
			ReferenceCountUtil.release(object);
		}
	}
	
	/**
	 * 请求时回调2
	 * @param ctx 通道操作上下文
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if(serverConfig.servletSingleton) return;
		dispatcherServlet=HttpServletFactory.getServlet(serverConfig);
	}

	/**
	 * 请求时回调5
	 * @param ctx 通道操作上下文
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		if(null!=request){
			request.clearAllAttributes();
			request=null;
		}
		
		if(serverConfig.servletSingleton) return;
		dispatcherServlet.destory();
	}

	/**
	 * 请求时回调4
	 * @param ctx 通道操作上下文
	 */
	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
    }
	
	/**
	 * 请求中发生异常
	 * @param ctx 通道操作上下文
	 * @param cause 异常对象
	 */
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        servletContext.removeHttpSession(request.getSessionId());
        ctx.close();
    }
}
