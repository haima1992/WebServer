package com.tedu.webserver.servlet;
import java.io.File;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;
public abstract class HttpServlet {
	public abstract void service(HttpRequest request,HttpResponse response);
	public void forward(String path,HttpRequest request,HttpResponse response) {
		File file = new File("webapps/"+path);
		response.setEntity(file);
	}
}
