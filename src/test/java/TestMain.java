import com.github.lixiang2114.netty.HttpServer;
import com.github.lixiang2114.netty.context.ServerConfig;

public class TestMain {
	
	public static void main(String[] args) throws Exception {
//		new HttpServer().startServer();
		ServerConfig serverConfig=new ServerConfig(8080,UserAction.class);
		new HttpServer(serverConfig).startServer();
	}
}
