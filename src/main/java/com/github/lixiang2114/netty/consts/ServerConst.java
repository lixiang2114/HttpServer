package com.github.lixiang2114.netty.consts;

import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Lixiang
 * @description 服务器常量
 */
public interface ServerConst {
	/**
	 * 请求执行成功返回标识
	 */
	public static final String SUCCESS = "OK";
	
	/**
	 * 请求执行失败返回标识
	 */
	public static final String ERROR = "Error";
	
	/**
	 * 服务器响应头域Server字段值
	 */
	public static final String SERVER_ID = "HttpServer";
	
	/** 
	 * 网站图标
	 * */
    public static final String FAVICON_ICO = "/favicon.ico";
    
    /**
	 * MIME类型为application/json的参数键名
	 */
	public static final String JSON_BODY_KEY = "jsonBody";
    
    /**
     * 连接关闭头域值
     */
    public static final String CONNECTION_CLOSE = "close";
    
    /**
   	 * 按位与正则式
   	 */
    public static final Pattern ANDS_REGEX=Pattern.compile("&");
    
    /**
   	 * 分号正则式
   	 */
    public static final Pattern SEMIC_REGEX=Pattern.compile(";");
    
    /**
   	 * 逗号正则式
   	 */
    public static final Pattern COMMA_REGEX=Pattern.compile(",");
    
    /**
   	 * 等号正则式
   	 */
    public static final Pattern EQUAL_REGEX=Pattern.compile("=");
    
    /**
     * JSON映射器
     */
    public static final ObjectMapper MAPPER=new ObjectMapper();
    
    /**
     * 长连接头域值
     */
    public static final String CONNECTION_KEEP_ALIVE = "keep-alive";
    
    /**
	 * 服务器响应头域ContentType默认值
	 */
	public static final String CONTENT_TYPE = "text/html;charset=UTF-8";
    
    /**
     * MIME类型字段值
     */
    public static final String FORM_JSON="application/json";
    
    /**
     * MIME类型字段值
     */
    public static final String MULTIPART_FORMDATA="multipart/form-data";
    
    /**
     * MIME类型字段值
     */
    public static final String FORM_URLENCODED="application/x-www-form-urlencoded";
}
