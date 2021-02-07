package com.github.lixiang2114.netty.context;

import java.lang.reflect.Field;

import com.github.lixiang2114.netty.servlet.HttpServlet;
import com.github.lixiang2114.netty.servlet.Servlet;

/**
 * @author Lixiang
 * @descrption HttpServlet组件工厂
 */
public class HttpServletFactory {
	/**
	 * 获取Servlet组件
	 * @param serverConfig 服务器配置
	 * @return Servlet组件
	 */
	public static final Servlet getServlet(ServerConfig serverConfig) throws Exception {
		if(null==serverConfig) return null;
		if(null==serverConfig.servletClass) return null;
		
		Servlet servlet=serverConfig.servletClass.newInstance();
		if(servlet instanceof HttpServlet){
			Field field=HttpServlet.class.getDeclaredField("serverConfig");
			field.setAccessible(true);
			field.set(servlet, serverConfig);
		}
		
		servlet.init();
		
		return servlet;
	}
}
