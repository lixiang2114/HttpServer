package com.github.lixiang2114.netty.servlet;

import com.github.lixiang2114.netty.context.ServerConfig;
import com.github.lixiang2114.netty.handlers.PrintWriter;
import com.github.lixiang2114.netty.scope.HttpServletRequest;
import com.github.lixiang2114.netty.scope.HttpServletResponse;

import io.netty.handler.codec.http.HttpMethod;

/**
 * @author Lixiang
 * @description 抽象Servlet组件
 */
public abstract class HttpServlet implements Servlet {
	/**
	 * 服务器配置
	 */
	protected ServerConfig serverConfig;
	
	@Override
	public void service(HttpServletRequest request,HttpServletResponse response) throws Exception{
		HttpMethod httpMethod=request.getOriginalMethod();
		if(httpMethod.equals(HttpMethod.GET)) {
			doGet(request,response);
		}else if(httpMethod.equals(HttpMethod.POST)) {
			doPost(request,response);
		}else{
			throw new RuntimeException("Not Support Http Method: "+httpMethod);
		}
	}
	
	@Override
	public void exception(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Throwable cause=response.getCause();
		cause.printStackTrace();
		PrintWriter writer=response.getPrintWriter();
		try{
			writer.write("Error: "+cause.getMessage());
		}finally{
			writer.close();
		}
	}

	/**
	 * 客户端Get请求
	 * @param request
	 * @param response
	 */
	public abstract void doGet(HttpServletRequest request,HttpServletResponse response) throws Exception;
	
	/**
	 * 客户端POST请求
	 * @param request
	 * @param response
	 */
	public abstract void doPost(HttpServletRequest request,HttpServletResponse response) throws Exception;
}
