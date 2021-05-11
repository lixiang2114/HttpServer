package com.github.lixiang2114.netty.context;

import java.lang.reflect.Field;

import com.github.lixiang2114.netty.event.AbstractEvent;
import com.github.lixiang2114.netty.event.TcpEvent;

/**
 * @author Lixiang
 * @description TCP事件工厂
 */
public class TcpEventFactory {
	/**
	 * 获取TCP事件工厂实例
	 * @param serverConfig 服务器配置
	 * @return TcpEvent组件
	 */
	public static final TcpEvent getEvent(ServerConfig serverConfig) throws Exception {
		if(null==serverConfig) return null;
		if(null==serverConfig.eventClass) return null;
		
		TcpEvent tcpEvent=serverConfig.eventClass.newInstance();
		if(tcpEvent instanceof AbstractEvent){
			Field field=AbstractEvent.class.getDeclaredField("serverConfig");
			field.setAccessible(true);
			field.set(tcpEvent, serverConfig);
		}
		
		tcpEvent.init();
		
		return tcpEvent;
	}
}
