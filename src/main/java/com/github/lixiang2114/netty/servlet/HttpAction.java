package com.github.lixiang2114.netty.servlet;

import java.lang.reflect.Method;

import com.github.lixiang2114.netty.scope.HttpServletRequest;
import com.github.lixiang2114.netty.scope.HttpServletResponse;

/**
 * @author Lixiang
 * @description HttpServlet适配器
 */
public class HttpAction extends HttpServlet {
	
	@Override
	public void init() throws Exception{}
	
	@Override
	public void destory() throws Exception{}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception{
		this.doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String methodName=request.getHeader("method");
		if(null==methodName) methodName=request.getHeader("Method");
		if(null==methodName) methodName=request.getParameter("method");
		if(null==methodName) methodName="execute";
		Method method=this.getClass().getMethod(methodName,HttpServletRequest.class, HttpServletResponse.class);
		method.invoke(this, request,response);
	}
	
	/**
	 * 默认的业务逻辑控制器方法
	 * @param request 请求对象
	 * @param response 响应对象
	 */
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception{}
}
