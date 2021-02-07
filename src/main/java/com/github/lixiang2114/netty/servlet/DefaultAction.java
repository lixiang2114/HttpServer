package com.github.lixiang2114.netty.servlet;

import com.github.lixiang2114.netty.handlers.PrintWriter;
import com.github.lixiang2114.netty.scope.HttpServletRequest;
import com.github.lixiang2114.netty.scope.HttpServletResponse;

/**
 * @author Lixiang
 * @description 默认Action实现
 */
public class DefaultAction extends HttpAction{
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println(request.getQueryString());
		PrintWriter writer=response.getPrintWriter();
		try{
			writer.write("OK");
		}finally{
			writer.close();
		}
	}
}
