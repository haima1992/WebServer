package com.tedu.webserver.servlet;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.tedu.webserver.http.HttpRequest;
import com.tedu.webserver.http.HttpResponse;

public class UpdateUserServlet extends HttpServlet{
	@Override
	public void service(HttpRequest request, HttpResponse response) {
		String name = request.getparameter("username");
		String pw = request.getparameter("password");
		String nn = request.getparameter("nickname");
		int age = Integer.parseInt(request.getparameter("age"));
		try (
			RandomAccessFile raf = new RandomAccessFile("user.dat","rw");
		){
			for(int i=0;i<raf.length()/100;i++) {
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String username = new String(data,"utf-8").trim();
				if(username.equals(name)) {
					data = pw.getBytes("utf-8");
					data = Arrays.copyOf(data, 32);
					raf.write(data);
					data = nn.getBytes("utf-8");
					data = Arrays.copyOf(data, 32);
					raf.write(data);
					raf.writeInt(age);
					System.out.println("修改完毕！");
					break;
				}
			}
			System.out.println("开始拼接动态页面！！！");
			StringBuilder builder = new StringBuilder();
			builder.append("<html>");
			builder.append("<head><meta charset='utf-8'><title>修改成功</title></head>");
			builder.append("<body>");
			builder.append("<center>");
			builder.append("<h1>修改成功</h1>");
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
