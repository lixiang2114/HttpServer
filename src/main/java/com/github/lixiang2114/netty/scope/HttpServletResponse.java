package com.github.lixiang2114.netty.scope;

import com.github.lixiang2114.netty.handlers.PrintWriter;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @author Lixiang
 * @description HttpServlet响应对象
 */
public class HttpServletResponse {
	/**
	 * Servlet异常信息
	 */
	private Throwable cause;
	
	/**
	 * HTTP流输出器
	 */
	private PrintWriter printWriter;
	
	public HttpServletResponse(HttpServletRequest request){
		this.printWriter=new PrintWriter(request);
	}
	
	public HttpServletResponse(HttpServletRequest request,Throwable cause){
		this.cause=cause;
		this.printWriter=new PrintWriter(request);
	}
	
	public Throwable getCause() {
		return cause;
	}

	public PrintWriter getPrintWriter() {
		return printWriter;
	}
	
	public void sendRedirect(String url) {
		printWriter.setLocation(url);
	}

	public void setStatus(HttpResponseStatus status) {
		printWriter.setStatus(status);
	}
	
	public void setHttpVersion(HttpVersion httpVersion) {
		printWriter.setHttpVersion(httpVersion);
	}
	
	public void setCookie(String name ,String value) {
		printWriter.setCookie(name, value);
	}
	
	public void setHeader(String name ,String value) {
		printWriter.setHeader(name, value);
	}
	
	public void addCookie(String name ,String value) {
		printWriter.addCookie(name, value);
	}
	
	public void addHeader(String name ,String value) {
		printWriter.addHeader(name, value);
	}
}
