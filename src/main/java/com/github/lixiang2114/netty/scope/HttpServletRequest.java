package com.github.lixiang2114.netty.scope;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.github.lixiang2114.netty.consts.HttpHeader;
import com.github.lixiang2114.netty.consts.ServerConst;
import com.github.lixiang2114.netty.context.ServerConfig;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

/**
 * @author Lixiang
 * @description HttpServlet请求对象
 */
@SuppressWarnings("unchecked")
public class HttpServletRequest {
	/**
	 * 请求内容字符编码集
	 */
	private Charset charset;
	
	/**
	 * 请求内容MIME类型
	 */
	private String mimeType;
	
	/**
	 * 会话跟踪ID
	 */
	private String sessionId;
	
	/**
	 * 客户端通道
	 */
	private Channel channel;
	
	/**
	 * 查询字符串
	 */
	private String queryString;
	
	/**
	 * 请求内容类型
	 */
	private String contentType;
	
	/**
	 * 请求内容字符编码集
	 */
	private String characterEncoding;
	
	/**
	 * 上传文件列表
	 */
	private List<File> uploadFileList;
	
	/**
	 * Http会话对象
	 */
	private HttpSession httpSession;
	
	/**
	 * Http请求方式
	 */
	private HttpMethod httpMethod;
	
	/**
	 * Http协议版本
	 */
	private HttpVersion httpVersion;
	
	/**
	 * Http协议消息头
	 */
	private HttpHeaders httpHeader;
	
	/**
	 * NIO产生的原生HttpRequest对象
	 * 实际上是一个HttpObjectAggregator.AggregatedFullHttpRequest对象
	 */
	private HttpRequest httpRequest;
	
	/**
	 * HTTP服务器配置
	 */
	private ServerConfig serverConfig;
	
	/**
	 * Servlet上下文
	 */
	private ServletContext servletContext;
	
	/**
	 * Post请求解码器
	 */
	private HttpPostRequestDecoder postDecoder;
	
	/**
	 * 请求参数字典
	 */
	private Map<String,List<Object>> parametersMap;
	
	/**
	 * 会话作用域字典
	 */
	private ConcurrentHashMap<String, Object> requestScope=new ConcurrentHashMap<String, Object>();
	
	public HttpServletRequest(Channel channel,HttpRequest httpRequest,ServerConfig serverConfig,ServletContext servletContext) {
		this.channel=channel;
		this.httpRequest=httpRequest;
		this.serverConfig=serverConfig;
		this.servletContext=servletContext;
		this.httpHeader = httpRequest.headers();
		this.httpMethod = httpRequest.method();
		this.httpVersion=httpRequest.protocolVersion();
		this.cleanAndInitPostDecoder();
		this.mayRequireInitHttpSession();
	}
	
	/**
	 * 清除磁盘上的数据并获取Post解码器
	 */
	private void cleanAndInitPostDecoder(){
		if (postDecoder != null) postDecoder.cleanFiles();
		postDecoder = new HttpPostRequestDecoder(serverConfig.dataFactory(), httpRequest, serverConfig.charset);
	}
	
	/**
	 * 初始化会话跟踪对象
	 */
	private void mayRequireInitHttpSession() {
		String sessionId=getSessionId();
		if(null!=sessionId) this.httpSession=servletContext.getHttpSession(sessionId);
		if(null==this.httpSession) this.httpSession=new HttpSession(servletContext);
		this.sessionId=this.httpSession.getSessionId();
		this.httpSession.refleshSessionStatus();
	}
	
	/**
	 * 清除请求作用域中所有数据
	 */
	public void clearAllAttributes() {
		requestScope.clear();
	}
	
	/**
	 * 获取请求作用域中指定键的值
	 * @param attributeName 键名
	 * @return 键值
	 */
	public Object getAttribute(String attributeName) {
		return requestScope.get(attributeName);
	}
	
	/**
	 * 移除请求作用域中指定键的值
	 * @param attributeName 键名
	 * @return 键值
	 */
	public Object removeAttribute(String attributeName) {
		return requestScope.remove(attributeName);
	}
	
	/**
	 * 设置请求作用域中指定键的值
	 * @param attributeName 键名
	 * @param atributeValue 键值
	 */
	public void setAttribute(String attributeName,Object atributeValue) {
		requestScope.put(attributeName,atributeValue);
	}
	
	/**
	 * 获取请求作用域字典
	 * @return 请求作用域字典
	 */
	public ConcurrentHashMap<String, Object> getRequestScope() {
		return requestScope;
	}

	/**
	 * 获取客户端通道
	 * @return 客户端通道
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * 获取请求URI
	 * @return 请求URI
	 */
	public String getRequestURI() {
		return httpRequest.uri();
	}
	
	/**
	 * 获取会话对象
	 * @return 会话对象
	 */
	public HttpSession getSession() {
		return httpSession;
	}
	
	/**
	 * 获取Http方法
	 * @return Http方法
	 */
	public String getMethod() {
		return httpMethod.toString();
	}
	
	/**
	 * 获取服务器配置信息
	 * @return 服务器配置
	 */
	public ServerConfig getServerConfig(){
		return serverConfig;
	}
	
	/**
	 * 获取原生Http请求方法
	 * @return 原生Http请求方法
	 */
	public HttpMethod getOriginalMethod() {
		return httpMethod;
	}
	
	/**
	 * 获取NIO原生请求对象
	 * @return 请求对象
	 */
	public HttpRequest getOriginalHttpRequest() {
		return httpRequest;
	}
	
	/**
	 * 获取HTTP请求协议
	 * @return 协议名称
	 */
	public String getHttpProtocol() {
		return httpVersion.protocolName().toString();
	}
	
	/**
	 * 获取HTTP协议版本
	 * @return 协议版本
	 */
	public String getHttpVersion() {
		return httpVersion.majorVersion()+"."+httpVersion.minorVersion();
	}
	
	/**
	 * 获取HTTP协议和版本
	 * @return 协议和版本信息
	 */
	public String getProtocolVersion() {
		return httpVersion.toString();
	}
	
	/**
	 * 获取HTTP原生版本对象
	 * @return HTTP原生版本对象
	 */
	public HttpVersion getOriginalVersion() {
		return httpVersion;
	}
	
	/**
	 * 获取Servlet上下文对象
	 * @return Servlet上下文对象
	 */
	public ServletContext getServletContext() {
		return servletContext;
	}
	
	/**
	 * 返回远端主机名/域名
	 * 若基于IP地址构建主机名则返回IP地址
	 * @return 主机名/域名/IP地址
	 */
	public String getRemoteHost() {
		return getRemoteHost(false);
	}
	
	/**
	 * 返回远端主机名/域名
	 * 若基于IP地址构建主机名则通过IP地址反向解析出域名,若不能反向解析出域名则返回IP地址
	 * @return 主机名/域名/IP地址
	 */
	public String getRemoteDomain() {
		return getRemoteDomain(false);
	}
	
	/**
	 * 获取远端原生Socket地址
	 * @return 原生Socket地址
	 */
	public SocketAddress getOriginalRemoteAddress() {
		return channel.remoteAddress();
	}
	
	/**
	 * 获取会话跟踪ID
	 * @return 会话跟踪ID
	 */
	public String getSessionId() {
		if(null!=sessionId) return sessionId;
		return getCookie(serverConfig.sessionId);
	}
	
	/**
	 * 返回远端主机名/域名
	 * 若基于IP地址构建主机名则返回IP地址
	 * @return 主机名/域名/IP地址
	 */
	public String getRemoteHost(boolean force) {
		SocketAddress address=channel.remoteAddress();
		if(!(address instanceof InetSocketAddress))return force?address.toString():null;
		return ((InetSocketAddress)address).getHostString();
	}
	
	/**
	 * 返回远端主机名/域名
	 * 若基于IP地址构建主机名则通过IP地址反向解析出域名,若不能反向解析出域名则返回IP地址
	 * @return 主机名/域名/IP地址
	 */
	public String getRemoteDomain(boolean force) {
		SocketAddress address=channel.remoteAddress();
		if(!(address instanceof InetSocketAddress)) return force?address.toString():null;
		return ((InetSocketAddress)address).getHostName();
	}
	
	/**
	 * 返回远端主机端口
	 * @return 主机端口
	 */
	public Integer getRemotePort() {
		SocketAddress address=channel.remoteAddress();
		if(!(address instanceof InetSocketAddress)) return null;
		return ((InetSocketAddress)address).getPort();
	}
	
	/**
	 * 获取请求消息的MIME类型
	 * @return MIME类型(NULL:无消息体,非NULL:有消息体)
	 */
	public String getMimeType(){
		if(null!=this.mimeType) return this.mimeType;
		String contentType=getContentType();
		if(null==contentType) return null;
		return this.mimeType=ServerConst.SEMIC_REGEX.split(contentType)[0].trim();
	}
	
	/**
	 * 获取请求消息的Charset编码类型
	 * @return Charset编码类型
	 */
	public Charset getCharset() {
		if(null!=charset) return charset;
		String charsetName=getCharacterEncoding();
		if(null==charsetName) return this.charset=serverConfig.charset;
		return this.charset=Charset.forName(charsetName);
	}
	
	/**
	 * 获取请求消息的字符集编码类型
	 * @return 字符集编码类型
	 */
	public String getCharacterEncoding() {
		if(null!=this.characterEncoding) return this.characterEncoding;
		String contentType=getContentType();
		if(null==contentType) return null;
		String[] array=ServerConst.SEMIC_REGEX.split(contentType);
		if(2>array.length) return null;
		String charsetPart=array[1].trim();
		if(0==charsetPart.length()) return null;
		String[] charsetArray=ServerConst.EQUAL_REGEX.split(charsetPart);
		if(2>charsetArray.length) return null;
		String charsetName=charsetArray[1].trim();
		return 0==charsetName.length()?null:(this.characterEncoding=charsetName);
	}
	
	/**
	 * 获取请求头域中的ContentType字段值
	 * @return ContentType字段值
	 */
	public String getContentType(){
		if(null!=this.contentType) return this.contentType;
		String contentType=httpHeader.getAndConvert(HttpHeader.CONTENT_TYPE);
		if(null==contentType) return null;
		contentType=contentType.trim();
		return 0==contentType.length()?null:(this.contentType=contentType);
	}
	
	/**
	 * 请求头域中是否包含指定的头域字段名
	 * @param name 头域字段名
	 * @return 是否包含
	 */
	public boolean containsHeader(String name) {
		if(null==name) return false;
		name=name.trim();
		return  0==name.length()?false:httpHeader.contains(name);
	}
	
	/**
	 * 获取指定的头域字段的第一个值
	 * @param name 头域字段名
	 * @return 第一个值字段值
	 */
	public String getHeader(String name) {
		if(null==name) return null;
		name=name.trim();
		return 0==name.length()?null:httpHeader.getAndConvert(name);
	}
	
	/**
	 * 获取指定的头域字段值列表
	 * @param name 头域字段名
	 * @return 字段值列表
	 */
	public Set<String> getHeaders(String name) {
		if(null==name) return null;
		name=name.trim();
		if(0==name.length()) return null;
		return Collections.unmodifiableSet(new HashSet<String>(httpHeader.getAllAndConvert(name)));
	}
	
	/**
	 * 获取Http协议头域字典
	 * 由于Http请求消息头是始终存在的,所有本方法返回非NULL非空值()
	 * @return
	 */
	public Map<String, String> getHeaderMap() {
		HashMap<String,String> tmpMap=new HashMap<String,String>();
		for(Entry<String, String> entry:httpHeader.entriesConverted()) {
			if(null==entry) continue;
			String key=entry.getKey();
			if(null==key) continue;
			key=key.trim();
			if(0==key.length()) continue;
			String value=entry.getValue();
			if(null==value) continue;
			tmpMap.put(key, value);
		}
		return Collections.unmodifiableMap(tmpMap);
	}
	
	/**
	 * 获取客户端提交的指定Cookie值
	 * @param name Cookie字段名
	 * @return Cookie值
	 */
	public String getCookie(String name) {
		if(null==name) return null;
		name=name.trim();
		if(0==name.length()) return null;
		
		Set<String> tmpSet=getCookies();
		if(null==tmpSet) return null;
		
		for(String entry:tmpSet){
			String[] keyVals=ServerConst.EQUAL_REGEX.split(entry);
			if(2>keyVals.length) continue;
			String key=keyVals[0].trim();
			if(name.equalsIgnoreCase(key)) return keyVals[1];
		}
		
		return null;
	}
	
	/**
	 * 获取客户端提交的所有Cookies
	 * @return Cookie集合
	 */
	public Set<String> getCookies() {
		Set<String> tmpSet=getHeaders(HttpHeader.COOKIE).stream()
		.filter(e->{return null!=e;})
		.map(e->e.trim())
		.filter(e->{return 0!=e.length();})
		.collect(Collectors.toSet());
		return 0==tmpSet.size()?null:Collections.unmodifiableSet(tmpSet);
	}
	
	/**
	 * 获取标准化Cookie字典
	 * 本方法会丢弃那些不符合键值对添加规则的Cookie值
	 * @return Cookie字典
	 */
	public Map<String,String> getCookieMap() {
		Set<String> tmpSet=getCookies();
		if(null==tmpSet) return null;
		
		HashMap<String,String> tmpMap=new HashMap<String,String>();
		for(String entry:tmpSet){
			String[] keyVals=ServerConst.EQUAL_REGEX.split(entry);
			if(2>keyVals.length) continue;
			tmpMap.put(keyVals[0].trim(), keyVals[1].trim());
		}
		
		return Collections.unmodifiableMap(tmpMap);
	}
	
	/**
	 * 获取MIME=application/json的HttpBody值
	 * @return 参数值
	 */
	public String getJsonBody() {
		Map<String, List<Object>> paramMap=getParametersMap();
		if(null==paramMap) return null;
		List<Object> valueList=paramMap.get(ServerConst.JSON_BODY_KEY);
		if(null==valueList || 0==valueList.size()) return null;
		return (String)valueList.get(0);
	}
	
	/**
	 * 获取MIME=application/octet-stream的HttpBody值
	 * @return 参数值
	 */
	public String getStreamBody() {
		Map<String, List<Object>> paramMap=getParametersMap();
		if(null==paramMap) return null;
		List<Object> valueList=paramMap.get(ServerConst.STREAM_BODY_KEY);
		if(null==valueList || 0==valueList.size()) return null;
		return (String)valueList.get(0);
	}
	
	/**
	 * 获取MIME=application/octet-stream的HttpBody值
	 * @return 参数值
	 */
	public ByteBuf getBodyByteBuf() {
		Map<String, List<Object>> paramMap=getParametersMap();
		if(null==paramMap) return null;
		List<Object> valueList=paramMap.get(ServerConst.STREAM_BODY_KEY);
		if(null==valueList || 0==valueList.size()) return null;
		return (ByteBuf)valueList.get(1);
	}
	
	/**
	 * 获取指定的参数值
	 * @return 参数值
	 */
	public String getParameter(String paramName) {
		Map<String, List<Object>> paramMap=getParametersMap();
		if(null==paramMap) return null;
		List<Object> valueList=paramMap.get(paramName);
		if(null==valueList || 0==valueList.size()) return null;
		return (String)valueList.get(0);
	}
	
	/**
	 * 获取指定的参数值列表
	 * @return 参数值列表
	 */
	public String[] getParameterValues(String paramName) {
		Map<String, List<Object>> paramMap=getParametersMap();
		if(null==paramMap) return null;
		List<Object> valueList=paramMap.get(paramName);
		if(null==valueList || 0==valueList.size()) return null;
		return valueList.toArray(new String[valueList.size()]);
	}
	
	/**
	 * 获取查询字符串
	 * @return 查询字符串
	 */
	public String getQueryString() {
		if(null!=queryString) return queryString;
		Map<String, List<Object>> tmpMap=getParametersMap();
		
		if(null==tmpMap) return null;
		StringBuilder builder=new StringBuilder("");
		
		for(Map.Entry<String, List<Object>> entry:tmpMap.entrySet()) {
			String key=entry.getKey();
			List<Object> valueList=entry.getValue();
			for(Object value:valueList) builder.append(key).append("=").append(value).append("&");
		}
		
		return queryString=0==builder.length()?"":builder.deleteCharAt(builder.length()-1).toString().trim();
	}
	
	/**
	 * 获取参数字典
	 * @return 请求参数字典
	 */
	public Map<String,List<Object>> getParametersMap() {
		if(null==parametersMap) parseHttpProtocol();
		return parametersMap;
	}
	
	/**
	 * 获取文件列表
	 * @return 文件列表
	 */
	public List<File> getUploadFileList() {
		if(null==uploadFileList) parseHttpProtocol();
		return uploadFileList;
	}
	
	/**
	 * 客户端是否需要服务端保持长连接
	 * @return 是否需要服务端保持长连接
	 */
	public boolean keepConnection(){
		if(httpHeader.contains(HttpHeader.CONNECTION, ServerConst.CONNECTION_CLOSE, true)) return false;
		if(httpHeader.contains(HttpHeader.CONNECTION, ServerConst.CONNECTION_KEEP_ALIVE, true)) return true;
		return false;
	}
	
	/**
	 * 解析Http协议流
	 */
	private final void parseHttpProtocol() {
		QueryStringDecoder queryDecoder = new QueryStringDecoder(getRequestURI(), getCharset());
		HashMap<String, List<Object>> accumulateMap=new HashMap<String, List<Object>>();
		Map<String, List<String>> uriParamMap=queryDecoder.parameters();
		if(null!=uriParamMap) {
			for(Map.Entry<String, List<String>> entry:uriParamMap.entrySet()){
				accumulateMap.put(entry.getKey(),entry.getValue().stream().map(x->(Object)x).collect(Collectors.toList()));
			}
		}
		
		String mimeType=getMimeType();
		if(null==mimeType) {
			parametersMap=Collections.unmodifiableMap(accumulateMap);
			return;
		}
		
		if(ServerConst.FORM_URLENCODED.equalsIgnoreCase(mimeType) || ServerConst.MULTIPART_FORMDATA.equalsIgnoreCase(mimeType)){
			List<InterfaceHttpData> httpBodys=postDecoder.getBodyHttpDatas();
			if(null==httpBodys || 0==httpBodys.size()) {
				parametersMap=Collections.unmodifiableMap(accumulateMap);
				return;
			}
			
			try{
				File uploadFile=null;
				ArrayList<File> fileList=new ArrayList<File>();
				for (InterfaceHttpData bodyPart : httpBodys) {
	            	if(HttpDataType.Attribute == bodyPart.getHttpDataType()) {
	            		Attribute attribute = (Attribute) bodyPart;
	            		String key=attribute.getName().trim();
	            		List<Object> valueList=accumulateMap.get(key);
	            		if(null==valueList) accumulateMap.put(key, valueList=new ArrayList<Object>());
	            		valueList.add(attribute.getValue());
	            	}else if(bodyPart.getHttpDataType() != HttpDataType.FileUpload){
	            		FileUpload fileUpload = (FileUpload) bodyPart;
	            		if(!fileUpload.isCompleted()) continue;
	            		fileList.add(uploadFile=new File(serverConfig.uploadDirectory,fileUpload.getFilename()));
	            		fileUpload.renameTo(uploadFile);
	            	}
	            }
				uploadFileList=Collections.unmodifiableList(fileList);
				parametersMap=Collections.unmodifiableMap(accumulateMap);
			}catch(IOException e) {
				e.printStackTrace();
			}
			return;
		}
		
		if(ServerConst.FORM_JSON.equalsIgnoreCase(mimeType)){
			ByteBuf byteBuf=((FullHttpRequest)httpRequest).content();
			if(null==byteBuf) {
				parametersMap=Collections.unmodifiableMap(accumulateMap);
				return;
			}
			
			String jsonMsgBody = byteBuf.toString(getCharset());
			if(null==jsonMsgBody || 0==(jsonMsgBody=jsonMsgBody.trim()).length()) {
				parametersMap=Collections.unmodifiableMap(accumulateMap);
				return;
			}
			
			HashMap<String, Object> tmpMap=new HashMap<String,Object>();
			tmpMap.put(ServerConst.JSON_BODY_KEY, jsonMsgBody);
			
			try {
				tmpMap.putAll(ServerConst.MAPPER.readValue(jsonMsgBody, HashMap.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for(Entry<String, Object> entry:tmpMap.entrySet()) {
				String key=entry.getKey().trim();
        		List<Object> valueList=accumulateMap.get(key);
        		if(null==valueList) accumulateMap.put(key, valueList=new ArrayList<Object>());
        		valueList.add(entry.getValue().toString());
			}
			parametersMap=Collections.unmodifiableMap(accumulateMap);
			return;
		}
		
		if(ServerConst.BODY_STREAM.equalsIgnoreCase(mimeType)){
			ByteBuf byteBuf=((FullHttpRequest)httpRequest).content();
			if(null==byteBuf) {
				parametersMap=Collections.unmodifiableMap(accumulateMap);
				return;
			}
			
			String jsonMsgBody = byteBuf.toString(getCharset());
			if(null==jsonMsgBody || 0==(jsonMsgBody=jsonMsgBody.trim()).length()) {
				parametersMap=Collections.unmodifiableMap(accumulateMap);
				return;
			}
			
			ArrayList<Object> valueList=new ArrayList<Object>();
			valueList.add(jsonMsgBody);
			valueList.add(byteBuf);
			accumulateMap.put(ServerConst.STREAM_BODY_KEY, valueList);
			parametersMap=Collections.unmodifiableMap(accumulateMap);
			return;
		}
		
		System.out.println("ERROR: QueryString Not Support MIME Type: "+mimeType);
	}
}
