package com.github.lixiang2114.netty.handlers;

import com.github.lixiang2114.netty.consts.ServerConst;
import com.github.lixiang2114.netty.context.HttpServletFactory;
import com.github.lixiang2114.netty.context.ServerConfig;
import com.github.lixiang2114.netty.scope.HttpServletRequest;
import com.github.lixiang2114.netty.scope.HttpServletResponse;
import com.github.lixiang2114.netty.scope.ServletContext;
import com.github.lixiang2114.netty.servlet.Servlet;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.ReferenceCountUtil;

/**
 * @author Lixiang
 * @description HTTP协议操作器
 * 本操作器是通道隔离的(即一个客户端通道每次请求持有本类的一个特定实例),
 * 但属于同一个IO线程的各个客户端通道可以通过ThreadLocal共享数据
 */
public class HttpChannelHandler extends ChannelHandlerAdapter{
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
	
	public HttpChannelHandler(ServerConfig serverConfig,ServletContext servletContext,Servlet dispatcherServlet) {
		this.serverConfig=serverConfig;
		this.servletContext=servletContext;
		this.dispatcherServlet=dispatcherServlet;
	}
    
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception{
		if(!(object instanceof HttpRequest)){
			ReferenceCountUtil.release(object);
			return;
		}
		
		request=new HttpServletRequest(ctx.channel(),(HttpRequest) object,serverConfig,servletContext);
		if(request.getRequestURI().equals(ServerConst.FAVICON_ICO)) return;
		
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
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if(serverConfig.servletSingleton) return;
		dispatcherServlet=HttpServletFactory.getServlet(serverConfig);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		if(null!=request){
			request.clearAllAttributes();
			request=null;
		}
		
		if(serverConfig.servletSingleton) return;
		dispatcherServlet.destory();
	}

	@Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) {
		channelHandlerContext.flush();
    }
	
	@Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        servletContext.removeHttpSession(request.getSessionId());
        channelHandlerContext.close();
    }
}
