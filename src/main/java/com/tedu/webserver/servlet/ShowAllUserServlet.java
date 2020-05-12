package com.tedu.webserver.servlet;

import java.io.RandomAccessFile;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;
/**
 * 显示用户列表
 * @author 杨帆
 */
public class ShowAllUserServlet extends HttpServlet{

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		try (
			RandomAccessFile raf = new RandomAccessFile("user.dat","r");
		){
			/*
			 * 读取user.dat文件，将数据拼接到html中
			 */
			StringBuilder builder = new StringBuilder();
			builder.append("<html>");
			builder.append("<head><meta charset='utf-8'><title>用户列表</title></head>");
			builder.append("<body>");
			builder.append("<center>");
			builder.append("<h1>用户列表</h1>");
			builder.append("<table border='2'>");
			builder.append("<tr><td align='center'>用户名</td><td align='center'>密码</td><td align='center'>昵称</td><td align='center'>年龄</td><td align='center'>修改</td></tr>");
			for(int i=0;i<raf.length()/100;i++) {
				byte[] data = new byte[32];
				//读用户名
				raf.read(data);
				String username = new String(data,"utf-8").trim();
				//读密码
				raf.read(data);
				String password = new String(data,"utf-8").trim();
				//读昵称
				raf.read(data);
				String nickname = new String(data,"utf-8").trim();
				//读年龄
				int age = raf.readInt();
				
				builder.append("<tr>");
				builder.append("<td align='center'>"+username+"</td>");
				builder.append("<td align='center'>"+password +"</td>");
				builder.append("<td align='center'>"+nickname+"</td>");
				builder.append("<td align='center'>"+age+"</td>");
				//
				builder.append("<td ><a href='toEditUser?username="+username+"'>修改</a></td>");
				builder.append("</tr>");
			}
			builder.append("</table><br>");
			builder.append("<a href='index.html'>返回主页</a>");
			builder.append("</center>");
			builder.append("</body>");
			builder.append("</html>");
			String table = builder.toString();
			byte[] data = table.getBytes("utf-8");
			
			response.putHeader("Content-Type", "text/html");
			response.putHeader("Content-Length", data.length+"");
			response.setData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
