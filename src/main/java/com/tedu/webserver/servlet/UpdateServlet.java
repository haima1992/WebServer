package com.tedu.webserver.servlet;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;

public class UpdateServlet extends HttpServlet{

	public void service(HttpRequest request, HttpResponse response) {
		String name = request.getparameter("username");
		String pw = request.getparameter("password");
		String newpassword = request.getparameter("newpassword1");
		try (
			RandomAccessFile raf = new RandomAccessFile("user.dat","rw");
		){
			boolean flag = false;
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String username = new String(data,"utf-8").trim();
				if(username.equals(name)) {
					flag = true;
					raf.read(data);
					String password = new String(data,"utf-8").trim();
					if(password.equals(pw)||pw==null&&"".equals(password)) {
						raf.seek(i*100+32);
						if(newpassword!=null) {
							data = newpassword.getBytes("utf-8");
							data = Arrays.copyOf(data, 32);
							raf.write(data);
						}else {
							data = new byte[32];
							raf.write(data);
						}
						forward("myweb/update_success.html",request,response);
					}else {
						forward("myweb/update_fail.html",request,response);
					}
					break;
				}
			}
			if(!flag) {
				forward("myweb/no_user.html",request,response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
