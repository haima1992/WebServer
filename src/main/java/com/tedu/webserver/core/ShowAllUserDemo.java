package com.tedu.webserver.core;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 显示user.dat文件中的所有用户信息
 * @author 杨帆
 *
 */
public class ShowAllUserDemo {
	public static void main(String[] args) throws IOException {
		/*
		 * 将每条记录读取出来，并输出到控制台
		 * 格式:
		 * username,password,nickname,age
		 */
		RandomAccessFile raf
			= new RandomAccessFile("user.dat","r");
		for(int i=0;i<raf.length()/100;i++){
			//读取用户名
			byte[] data = new byte[32];
			raf.read(data);
			String username = new String(data,"UTF-8").trim();
			
			//读取密码
			raf.read(data);
			String password = new String(data,"UTF-8").trim();
			
			//读取昵称
			raf.read(data);
			String nickname = new String(data,"UTF-8").trim();
			
			//读取年龄
			int age = raf.readInt();
			System.out.println(username+","+password+","+nickname+","+age);
		}
		raf.close();
	}
}












