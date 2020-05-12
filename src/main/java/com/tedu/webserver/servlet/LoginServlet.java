package com.tedu.webserver.servlet;

import java.io.RandomAccessFile;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;

public class LoginServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response) {
		//1:获取用户登录信息
		String name = request.getparameter("username");
		String pw = request.getparameter("password");
		//读取user.dat比对信息
		boolean check =false;
		try (
			RandomAccessFile raf = new RandomAccessFile("user.dat","r");			
		){
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String username = new String(data,"utf-8").trim();
				if(username.equals(name)) {
					raf.read(data);
					String password = new String(data,"utf-8").trim();
					if(password.equals(pw)) {
						check = true;
					}
					break;//?
				}
			}
			if(check) {
				/*
				 * 需要注意，内部跳转页面时使用的相对路径是服务端这边的相对路径而重定向是将路径发送给客户端，让其根据该地址发起请求，
				 * 所以指定的相对路径是相对浏览器上次请求的路径
				 * 这里要注意区分
				 */
//				forward("myweb/login_success.html",request,response);
				response.sendRedirect("login_success.html");
				System.out.println("登录成功！");
			}else {
//				forward("myweb/login_fail.html",request,response);
				response.sendRedirect("login_fail.html");
				System.out.println("您输入的用户名或密码不正确！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
