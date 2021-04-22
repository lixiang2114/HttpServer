package com.github.lixiang2114.netty.handlers;

import java.util.ArrayList;

import com.github.lixiang2114.netty.consts.HttpHeader;
import com.github.lixiang2114.netty.consts.ServerConst;
import com.github.lixiang2114.netty.scope.HttpServletRequest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @author Lixiang
 * @description HTTP流输出器
 */
public class PrintWriter {
	/**
	 * 客户端连接通道
	 */
	private Channel channel;
	
	/**
	 * 客户端请求对象
	 */
	private HttpServletRequest request;
	
	/**
	 * NIO原生Http响应对象接口
	 */
	private FullHttpResponse httpResponse;
	
	/**
	 * 响应消息操作句柄
	 */
	private ArrayList<ChannelFuture> futureList;
	
	public PrintWriter(HttpServletRequest request){
		this.request=request;
		this.channel=request.getChannel();
		this.futureList=new ArrayList<ChannelFuture>();
		this.httpResponse=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		String cookie=new StringBuilder(request.getServerConfig().sessionId).append("=").append(request.getSessionId()).toString();
		this.httpResponse.headers().set(HttpHeader.CONTENT_TYPE, ServerConst.CONTENT_TYPE);
		this.httpResponse.headers().set(HttpHeader.SERVER,ServerConst.SERVER_ID);
		this.httpResponse.headers().set(HttpHeader.SET_COOKIE, cookie);
	}
	
	/**
	 * 响应消息回客户端
	 * @param message 消息内容
	 */
	public void write(String message) {
		ByteBuf byteBuf = Unpooled.wrappedBuffer(message.getBytes(request.getCharset()));
		String length=String.valueOf(byteBuf.readableBytes());
		FullHttpResponse response = httpResponse.replace(byteBuf);
		response.headers().set(HttpHeader.CONTENT_LENGTH, length);
		futureList.add(channel.write(response));
		channel.flush();
	}
	
	/**
	 * 关闭客户端连接
	 */
	public void close() {
		if(request.keepConnection()) return;
		forceClose();
	}
	
	/**
	 * 强制关闭客户端连接
	 */
	public void forceClose() {
		futureList.forEach(future->future.addListener(ChannelFutureListener.CLOSE));
	}
	
	public void addCookie(String name ,String value) {
		httpResponse.headers().add(HttpHeader.SET_COOKIE, new StringBuilder(name).append("=").append(value).toString());
	}
	
	public void setCookie(String name ,String value) {
		httpResponse.headers().set(HttpHeader.SET_COOKIE, new StringBuilder(name).append("=").append(value).toString());
	}
	
	public void setHeader(String name ,String value) {
		httpResponse.headers().set(name, value);
	}
	
	public void addHeader(String name ,String value) {
		httpResponse.headers().add(name, value);
	}
	
	public void setHttpVersion(HttpVersion httpVersion) {
		httpResponse.setProtocolVersion(httpVersion);
	}

	public void setStatus(HttpResponseStatus status) {
		httpResponse.setStatus(status);
	}
	
	public void setLocation(String url) {
		httpResponse.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
		httpResponse.headers().set(HttpHeader.LOCATION, url);
	}
}
