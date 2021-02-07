package com.github.lixiang2114.netty.servlet;

import com.github.lixiang2114.netty.scope.HttpServletRequest;
import com.github.lixiang2114.netty.scope.HttpServletResponse;

/**
 * @author Lixiang
 * @description Servlet组件接口
 */
public interface Servlet {
	/**
	 * 组件初始化
	 */
	public void init() throws Exception;
	
	/**
	 * 组件服务
	 * @param request 请求对象
	 * @param response 响应对象
	 */
	public void service(HttpServletRequest request,HttpServletResponse response) throws Exception;
	
	/**
	 * 组件错误
	 * @param e
	 */
	public void exception(HttpServletRequest request,HttpServletResponse response) throws Exception;
	
	/**
	 * 组件销毁
	 */
	public void destory() throws Exception;
}
