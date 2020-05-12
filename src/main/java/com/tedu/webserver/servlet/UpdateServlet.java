package com.tedu.webserver.servlet;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;

public class UpdateServlet extends HttpServlet{

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		String name = request.getparameter("username");
		String pw = request.getparameter("password");
		String pw1 = request.getparameter("newpassword1");
		String pw2 = request.getparameter("newpassword2");
		if(pw1.equals(pw2)) {
			try (
				RandomAccessFile raf = new RandomAccessFile("user.dat","rw");
			){
				//
				boolean flag = false;
				for(int i=0;i<raf.length()/100;i++) {
					raf.seek(i*100);
					byte[] data = new byte[32];
					raf.read(data);
					String username = new String(data,"utf-8").trim();
					//查看是否
					if(username.equals(name)) {
						System.out.println("用户名存在！");
						flag = true;
						raf.read(data);
						String password = new String(data,"utf-8").trim();
						if(password.equals(pw)) {
							System.out.println("原密码正确！");
							//将指针移动到密码位置
							raf.seek(i*100+32);
							//修改密码
							data = pw1.getBytes("utf-8");
							data = Arrays.copyOf(data, 32);
							raf.write(data);
							forward("myweb/update_success.html",request,response);
						}else {
							forward("myweb/update_fail.html",request,response);
						}
						break;
					}
				}
				//查无此人
				if(!flag) {
					System.out.println("用户不存在！");
					forward("myweb/no_user.html",request,response);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("新密码输入不一致！！！");
			forward("myweb/update_newpassword_error.html", request, response);
		}
		
	}

}
