package com.tedu.webserver.servlet;

import java.io.File;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;
/**
 * 所有Servlet的超类
 * @author 杨帆
 *
 */
public abstract class HttpServlet {
	public abstract void service(HttpRequest request,HttpResponse response);
	//跳转指定路径对应的资源
	public void forward(String path,HttpRequest request,HttpResponse response) {
		File file = new File("webapps/"+path);
		response.setEntity(file);
	}
}
