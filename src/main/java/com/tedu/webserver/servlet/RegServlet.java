package com.tedu.webserver.servlet;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;

public class RegServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response) {
		String username = request.getparameter("username");
		String password = request.getparameter("password");
		String nickname = request.getparameter("nickname");
		String ageStr=request.getparameter("age");
		try (
			RandomAccessFile raf = new RandomAccessFile("user.dat","rw");
		){
			raf.seek(raf.length());
			if(username!=null) {
				byte[] name = username.getBytes("utf-8");
				name  = Arrays.copyOf(name, 32);
				raf.write(name);
				if(password!=null) {
					byte[] data = password.getBytes("utf-8");
					data = Arrays.copyOf(data, 32);
					raf.write(data);
				}else {
					byte[] data = new byte[32];
					raf.write(data);
				}
				if(nickname!=null) {
					byte[] data = nickname.getBytes("utf-8");
					data = Arrays.copyOf(data, 32);
					raf.write(data);
				}else {
					byte[] data = new byte[32];
					raf.write(data);
				}
				if(ageStr!=null) {
					int age = Integer.parseInt(request.getparameter("age"));
					raf.writeInt(age);
				}else {	
					byte[] data = new byte[4];
					raf.write(data);
				}
				forward("myweb/reg_success.html",request,response);
			}else {
				forward("myweb/reg_fail.html",request,response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
