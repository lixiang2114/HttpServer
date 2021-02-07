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
