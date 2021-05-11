import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.lixiang2114.netty.Server;
import com.github.lixiang2114.netty.context.ServerConfig;
import com.github.lixiang2114.netty.server.HttpServer;
import com.github.lixiang2114.netty.server.TcpServer;

public class TestMain {
	
	private static Future<?> future;
	
	private static ExecutorService service;
	
	public static void main(String[] args) throws Exception {
//		startTcpServer();
		startHttpServer();
		
		Scanner scanner=new Scanner(System.in);
		while(!Thread.currentThread().isInterrupted()) {
			if("stop".equalsIgnoreCase(scanner.nextLine().trim())){
				System.out.println("shutdown server...");
				future.cancel(true);
				break;
			}
		}
		
		service.shutdownNow();
		System.out.println("server is stoped...");
	}
	
	private static void startTcpServer() throws Exception {
//		new TcpServer().startServer();
		future=(service=Executors.newFixedThreadPool(1)).submit(new Runnable(){
			@Override
			public void run() {
				ServerConfig serverConfig=new ServerConfig(8080);
				try {
					Server server=new TcpServer(serverConfig);
					server.startServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private static void startHttpServer() throws Exception {
//		new HttpServer().startServer();
		future=(service=Executors.newFixedThreadPool(1)).submit(new Runnable(){
			@Override
			public void run() {
				ServerConfig serverConfig=new ServerConfig(8080,UserAction.class);
				try {
					Server server=new HttpServer(serverConfig);
					server.startServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
