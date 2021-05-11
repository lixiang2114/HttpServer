

import java.io.IOException;
import java.util.Collections;

import com.github.lixiang2114.netty.handlers.PrintWriter;
import com.github.lixiang2114.netty.scope.HttpServletRequest;
import com.github.lixiang2114.netty.scope.HttpServletResponse;
import com.github.lixiang2114.netty.scope.HttpSession;
import com.github.lixiang2114.netty.servlet.HttpAction;

/**
 * @author Lixiang
 * @description 转存服务模块
 */
public class UserAction extends HttpAction{
	/**
	 * 用户应用层Servlet配置
	 */
	private Object httpConfig;
	
	@Override
	public void init() throws IOException {
		this.httpConfig=serverConfig.appConfig;
	}

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
