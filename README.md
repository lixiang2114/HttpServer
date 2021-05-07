### 开发背景  
HttpServer是一款基于JAVA的NIO框架Netty4.1设计的嵌入式服务器，可以无缝嵌入JAVA应用程序中，常见的嵌入式服务器还有Jetty，但可支持独立运行的Jetty被设计的太过于重量化，且在JAVA程序中调用起来也相对较为复杂，而HttpServer则专注于嵌入式设计，将连接器封装于框架内部，仅暴露仅有的端口设置和Servlet接口给应用层，使用起来简单、轻量化。  
​      
      

### 功能特性  
支持ISO四层TCP协议和七层HTTP协议，其中，七层应用协议支持Servlet容器、常用的Servlet-API组件，支持会话跟踪和发送重定向；使用异步事件驱动和IO多路复用机制完成客户端连接和请求响应处理过程。  
        
      
### 安装部署  
mkdir -p $MVN_HOME/repository/com/github/lixiang2114/netty/HttpServer/2.0/
wget https://github.com/lixiang2114/HttpServer/blob/main/target/HttpServer-2.0.jar -P $MVN_HOME/repository/com/github/lixiang2114/netty/HttpServer/2.0/   
wget https://github.com/lixiang2114/HttpServer/blob/main/target/HttpServer-2.0-sources.jar -P $MVN_HOME/repository/com/github/lixiang2114/netty/HttpServer/2.0/   
​      
### 工程应用  
1. 引用依赖  
```Xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.63.Final</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>com.github.lixiang2114.netty</groupId>
    <artifactId>HttpServer</artifactId>
    <version>2.0</version>
</dependency>
```
2. 应用范例  
```JAVA
package com.wa.bfw.server.test;

import com.github.lixiang2114.netty.HttpServer;
import com.github.lixiang2114.netty.context.ServerConfig;
import com.wa.bfw.server.test.servlet.UserServlet;

public class TestMain {

	public static void main(String[] args) throws Exception {
		ServerConfig serverConfig=new ServerConfig(8080,UserServlet.class);
		new HttpServer(serverConfig).startServer();
	}
}
```
其中，UserServlet是应用自定义的请求响应处理类，类的设计范例如下：  
```JAVA
package com.wa.bfw.server.test.servlet;

import java.util.Collections;
import com.github.lixiang2114.netty.handlers.PrintWriter;
import com.github.lixiang2114.netty.scope.HttpServletRequest;
import com.github.lixiang2114.netty.scope.HttpServletResponse;
import com.github.lixiang2114.netty.scope.HttpSession;
import com.github.lixiang2114.netty.servlet.HttpServlet;

/**
 * @author Lixiang
 * @description 用户模块控制器
 */
public class UserServlet extends HttpServlet{
	
	@Override
	public void init() {}

	@Override
	public void destory() {}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		this.doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String validateResult="Error";
		HttpSession session=request.getSession();
		PrintWriter writer=response.getPrintWriter();
		
		if(null!=session.getAttribute("loginUser")) {
			validateResult="OK";
			System.out.println(request.getJsonBody());
		}else{
			String userName=request.getParameter("userName");
			String passWord=request.getParameter("passWord");
			if("ligang".equals(userName) && "123456".equals(passWord)) {
				validateResult="OK";
				System.out.println("Login Success...");
				request.getSession().setAttribute("loginUser", Collections.singletonMap("ligang", "123456"));
			}
		}
		
		try{
			writer.write(validateResult);
		}finally{
			writer.close();
		}
	}
}

```
#### 特别说明：  
ServerConfig类封装了所有的服务器配置和Servlet应用配置，应用层可根据业务需求酌情修改以适应实际应用场景；HttpServer的使用与Tomcat中常规Servlet的使用相同（若基于TCP协议创建服务器请将HttpServer替换成TcpServer），上述startServer方法被调用之后，服务器将被挂起并等待客户端连接，一旦客户端连接请求到来便委托给绑定的Servlet处理（即：由UserServlet处理）