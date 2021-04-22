package com.github.lixiang2114.netty.scope;

import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lixiang
 * @description Servlet上下文
 */
public class ServletContext {
	/**
	 * Servlet上下文
	 */
	private static ServletContext servletContext;
	
	/**
	 * 应用作用域字典
	 */
	private ConcurrentHashMap<String, Object> applicationScope=new ConcurrentHashMap<String, Object>();
	
	/**
	 * 客户端会话字典
	 */
	private ConcurrentHashMap<String, HttpSession> sessionMap=new ConcurrentHashMap<String, HttpSession>();
	
	private ServletContext(){}
	
	public void clearAllAttributes() {
		applicationScope.clear();
	}
	
	public Object getAttribute(String attributeName) {
		return applicationScope.get(attributeName);
	}
	
	public Object removeAttribute(String attributeName) {
		return applicationScope.remove(attributeName);
	}
	
	public void setAttribute(String attributeName,Object atributeValue) {
		applicationScope.put(attributeName,atributeValue);
	}
	
	public static synchronized ServletContext getInstance() {
		if(null!=servletContext) return servletContext;
		return servletContext=new ServletContext();
	}
	
	public int getSessionNumber(){
		return sessionMap.size();
	}
	
	public Enumeration<String> getAllSessionIds(){
		return sessionMap.keys();
	}
	
	public Collection<HttpSession> getAllSessions(){
		return sessionMap.values();
	}
	
	public HttpSession getHttpSession(String sessionId){
		return sessionMap.get(sessionId);
	}
	
	public HttpSession removeHttpSession(String sessionId){
		return sessionMap.remove(sessionId);
	}
	
	public ConcurrentHashMap<String, HttpSession> getSessionMap() {
		return sessionMap;
	}
	
	public void setHttpSession(String sessionId,HttpSession httpSession){
		sessionMap.put(sessionId, httpSession);
	}
}
