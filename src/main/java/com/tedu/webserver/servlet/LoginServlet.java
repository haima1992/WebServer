package com.tedu.webserver.servlet;
import java.io.RandomAccessFile;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;

public class LoginServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response) {
		String name = request.getparameter("username");
		String pw = request.getparameter("password");
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
					if(password.equals(pw)||"".equals(password)&&pw==null) {
						check = true;
					}
					break;
				}
			}
			if(check) {
				response.sendRedirect("login_success.html");
			}else {
				response.sendRedirect("login_fail.html");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
