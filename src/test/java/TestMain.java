import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.lixiang2114.netty.HttpServer;
import com.github.lixiang2114.netty.TcpServer;
import com.github.lixiang2114.netty.context.ServerConfig;

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
					new TcpServer(serverConfig).startServer();
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
					new HttpServer(serverConfig).startServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
