package com.tedu.webserver.core;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.tedu.webserver.http.EmptyRequestException;
import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;
import com.tedu.webserver.servlet.HttpServlet;

/**
 * 用来处理客户端请求
 * @author 杨帆
 *
 */
public class ClientHandler implements Runnable{
	private Socket socket;
	public ClientHandler(Socket socket) {
		this.socket=socket;
	}
	public void run() {
		try {
			//1解析请求
			HttpRequest request = new HttpRequest(socket);
			//2创建响应对象
			HttpResponse response = new HttpResponse(socket);
			//2.1获取请求的资源路径
			String url = request.getRequestURI();
			//2.2判断是否为请求业务
			//先根据请求获取对应的Servlet名字
			String servletName = ServerContext.getServletName(url);
			//若Servlet名字不为null,说明该请求对应的是业务
			if (servletName!=null) {
				System.out.println("加载:"+servletName);
				//加载该Servlet
				@SuppressWarnings("rawtypes")
				Class cls = Class.forName(servletName);
				//实例化该Servlet
				HttpServlet servlet = (HttpServlet)cls.newInstance();
				System.out.println("调用"+servletName+"的Service开始");
				servlet.service(request, response);
			}else {
				//2.3对应的从webapps目录中找到该资源
				File  file = new File("webapps"+url);
				//2.4判断用户请求的资源是否真实存在
				if(file.exists()) {
					System.out.println("资源已找到！");
					//将该资源设置到响应对象的属性上
					response.setEntity(file);
				}else {
					System.out.println("根据相关法律法规和政策，搜索结果未予显示。");
					//响应404页面
					//1设置状态代码为404
					response.setStatusCode(404);
					//2设置错误页面
					File notFoundPage = new File("webapps/root/404.html");
					response.setEntity(notFoundPage);
				}
			}
			//3响应客户端
			response.flush();
		} catch (EmptyRequestException e) {
			System.out.println("空请求！");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//处理断开连接后的操作
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
