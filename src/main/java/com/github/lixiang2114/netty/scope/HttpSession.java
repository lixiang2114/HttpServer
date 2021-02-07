package com.github.lixiang2114.netty.scope;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lixiang
 * @description Http会话
 */
public class HttpSession {
	/**
	 * 会话跟踪ID
	 */
	private String sessionId;
	
	/**
	 * 最后访问会话时间(单位:毫秒)
	 */
	private Long lastAcessTime;
	
	/**
	 * 请求次数计数器
	 */
	private long requestCounter=0L;
	
	/**
	 * Servlet上下文
	 */
	private ServletContext servletContext;
	
	/**
	 * 随机数工具
	 */
	private static final Random RANDOM=new java.util.Random();
	
	/**
	 * 会话作用域字典
	 */
	private ConcurrentHashMap<String, Object> sessionScope=new ConcurrentHashMap<String, Object>();
	
	public HttpSession(ServletContext servletContext){
		this.servletContext=servletContext;
		this.servletContext.setHttpSession(sessionId=generateSessionID(), refleshSessionStatus());
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}
	
	/**
	 * 刷新Session会话状态
	 */
	public HttpSession refleshSessionStatus() {
		requestCounter++;
		lastAcessTime=System.currentTimeMillis();
		return this;
	}
	
	/**
	 * 销毁Session会话
	 */
	public void invalidate() {
		sessionScope.clear();
		servletContext.removeHttpSession(sessionId);
	}
	
	/**
	 * 如果会话过期则自毁
	 * @param expireTime 会话过期时长
	 */
	public void maySelfDestruction(long expireTime) {
		if(isExpire(expireTime)) invalidate();
	}
	
	/**
	 * 会话是否过期
	 * @param expireTime 会话过期时长
	 */
	public boolean isExpire(long expireTime) {
		return System.currentTimeMillis()-lastAcessTime>=expireTime;
	}
	
	public Long getLastAcessTime() {
		return lastAcessTime;
	}
	
	public long getRequestCounter() {
		return requestCounter;
	}
	
	public void clearAllAttributes() {
		sessionScope.clear();
	}
	
	public Object getAttribute(String attributeName) {
		return sessionScope.get(attributeName);
	}
	
	public Object removeAttribute(String attributeName) {
		return sessionScope.remove(attributeName);
	}
	
	public void setAttribute(String attributeName,Object atributeValue) {
		sessionScope.put(attributeName,atributeValue);
	}

	/**
	 * 生成会话跟踪ID
	 * @return 会话跟踪ID
	 */
	private static final String generateSessionID(){
		String zeroNum=null;
		int num=RANDOM.nextInt(1000);
		if(num<10) {
			zeroNum="00";
		}else if(num<100) {
			zeroNum="0";
		}else{
			zeroNum="";
		}
		return new StringBuilder("").append(num).append(zeroNum).append(System.currentTimeMillis()).toString();
	}
}
