package com.github.lixiang2114.netty;

/**
 * @author Lixiang
 * @description 服务器
 */
public interface Server {
	/**
	 * 启动服务器
	 * @throws Exception
	 */
	public void startServer() throws Exception;

	/**
	 * 关闭服务器
	 * @throws Exception
	 */
	public void shutdownServer() throws Exception;
}
