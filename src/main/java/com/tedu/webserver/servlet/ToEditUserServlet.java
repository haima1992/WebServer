package com.tedu.webserver.servlet;

import java.io.RandomAccessFile;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;
public class ToEditUserServlet extends HttpServlet{
	public void service(HttpRequest request, HttpResponse response) {
		String name = request.getparameter("username");
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
					raf.read(data);
					String nickname = new String(data,"utf-8").trim();
					int age = raf.readInt();
					StringBuilder builder = new StringBuilder();
					builder.append("<html>");
					builder.append("<head><meta charset='utf-8'><title>用户列表</title></head>");
					builder.append("<body>");
					builder.append("<center>");
					builder.append("<h1>用户列表</h1>");
					builder.append("<form action='updateUser' method='get'>");
					builder.append("<table border='2'>");
					builder.append("<tr>");
					builder.append("<td align='center'>用户名</td>");
					builder.append("<td>"+username+"<input type='hidden' name='username'"+"value='"+username+"'></td>");
					builder.append("</tr>");
					builder.append("<tr>");
					builder.append("<td align='center'>密码</td>");
					builder.append("<td><input type='password' name='password' "+"value='"+password+"'></td>");
					builder.append("</tr>");
					builder.append("<tr>");
					builder.append("<td align='center'>昵称</td>");
					builder.append("<td><input type='text' name='nickname' "+"value='"+nickname+"'></td>");
					builder.append("</tr>");
					builder.append("<tr>");
					builder.append("<td align='center'>年龄</td>");
					builder.append("<td><input type='text' name='age' "+"value='"+age+"'></td>");
					builder.append("</tr>");
					builder.append("<tr>");
					builder.append("<td align='center' colspan='2'><input type='submit' value='保存' ></td>");
					builder.append("</tr>");
					builder.append("</table>");
					builder.append("</form>");
					builder.append("</center>");
					builder.append("</body>");
					builder.append("</html>");
					
					byte[] arr = builder.toString().getBytes("utf-8");
					response.putHeader("Content-Type", "text/html");
					response.putHeader("Content-Length", arr.length+"");
					response.setData(arr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
