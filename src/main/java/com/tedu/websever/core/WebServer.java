package com.tedu.websever.core;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
	private ServerSocket server;
	
	private ExecutorService threadPool;
	
	public WebServer() {
		try {
			server = new ServerSocket(8088);
			threadPool = Executors.newFixedThreadPool(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void start() {
		try {
			while(true) {
				Socket socket = server.accept();
				ClientHandler handler = new ClientHandler(socket);
				threadPool.execute(handler);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
}
