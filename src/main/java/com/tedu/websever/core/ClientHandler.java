package com.tedu.websever.core;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.tedu.webserver.http.EmptyRequestException;
import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;
import com.tedu.webserver.servlet.HttpServlet;

public class ClientHandler implements Runnable{
	private Socket socket;
	public ClientHandler(Socket socket) {
		this.socket=socket;
	}
	public void run() {
		try {
			HttpRequest request = new HttpRequest(socket);
			HttpResponse response = new HttpResponse(socket);
			String url = request.getRequestURI();
			String servletName = ServerContext.getServletName(url);
			if (servletName!=null) {
				@SuppressWarnings("rawtypes")
				Class cls = Class.forName(servletName);
				HttpServlet servlet = (HttpServlet)cls.newInstance();
				servlet.service(request, response);
			}else {
				File  file = new File("webapps"+url);
				if(file.exists()) {
					response.setEntity(file);
				}else {
					response.setStatusCode(404);
					File notFoundPage = new File("webapps/root/404.html");
					response.setEntity(notFoundPage);
				}
			}
			response.flush();
		} catch (EmptyRequestException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
