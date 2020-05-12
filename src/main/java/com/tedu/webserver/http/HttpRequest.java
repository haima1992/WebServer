package com.tedu.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * 请求类
 * 该类的每一个实例用于表示客户端发送过来的一个具体请求内容。
 * @author 杨帆
 *
 */
public class HttpRequest {
	
	/** 请求行相关信息*/
	//请求方式
	private String method;
	//请求资源路径
	private String url;
	//请求的协议版本
	private String protocol;
	
	/*
	 * 由于url可能会出现两种情况，是否带参数
	 * 例如:
	 * /myweb/index.html 不带参数
	 * /myweb/index.html?name=xxx&password=xxx... 带参数
	 * 所以对于url内容而言，我们再定义两个属性，分别保存url中的请求部分与参数部分。 
	 */
	//请求部分,url中"?"左侧的内容，若没有"?"则与url内容一致
	private String requestURI;
	//参数部分
	private String queryString;
	//用于保存每个参数的Map
	private Map<String,String> parameters = new HashMap<String,String>();
	
	/** 消息头相关信息*/
	//key:消息头名字      value:消息头对应的值
	private Map<String,String> headers = new HashMap<String,String>();
	
	//对应客户端的Socket
	@SuppressWarnings("unused")
	private Socket socket;
	//通过Socket获取的输入流，用于读取客户端发送的请求内容
	private InputStream in;
	/*
	 * 创建HttpRequest的同时传入对应客户端的Socket.
	 * 要根据该Socket获取输入流读取客户端发送的请求
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException{
		try {
			this.socket = socket;
			this.in= socket.getInputStream();
			//1解析请求行
			parseRequestLine();
			//2解析消息头
			parseHeaders();
			//3解析消息正文
			parseContent();
		} catch (EmptyRequestException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 解析请求行*/
	private void parseRequestLine() throws EmptyRequestException{
		System.out.println("开始解析请求行...");
		/*
		 * 1:先通过输入流读取第一行字符串(请求行内容)
		 * 2:按照空格拆分字符串
		 * 3:将拆分的三项内容设置到请求行对应的属性上
		 */
		String line = readLine();
		System.out.println("请求行内容:"+line);
		/*
		 * 解析过程中可能会出现数组下标越界，这是由于HTTP协议允许客户端发送一个空请求,
		 * 即:客户端连接后没有按照HTTP协议的request格式发送内容。
		 * 后期解决！
		 */
		String regex = "\\s";//"\\s"表示空格
		String[] strs = line.split(regex);
		if(strs.length<3) {
			//空请求
			throw new EmptyRequestException();
		}
		this.method = strs[0];
		this.url = strs[1];
		//进一步解析url
		parseURL();
		this.protocol = strs[2];
		System.out.println("method="+method);
		System.out.println("url="+url);
		System.out.println("protocol="+protocol);
		System.out.println("解析请求行完毕!");
	}	
	
	/*
	 * 解析url
	 * 由于url中可能含有参数，所以要对url进行进一步解析
	 */
	private void parseURL() {
		/*
		 * url会存在两种情况，是否带有参数
		 * 1:判断当前url是否含有"?",若有则表示该url是含有参数部分的。
		 * 2:若含有"?",则首先按照"?"将url拆分为两部分
		 *   将"?"左侧内容设置到requestURI属性上，将"?"右侧内容设置到queryString属性上。
		 * 3:进一步解析参数部分，首先按照"&"拆分出每个参数，再将每个参数按照"="拆分为参数名与参数值，
		 *   并将参数名作为key,参数的值作为value存入到parameters这个map中。
		 *   
		 * 4:若url不含有"?",则直接将url的值设置到属性requestURI即可。
		 * 
		 * /myweb/reg?username=123&password=123&nickname=123456&age=123
		 */
		//1判断是否包含?
		if(url.contains("?")) {
			System.out.println("含有参数！");
			//2根据?拆分请求
			String[] strs1 = url.split("\\?");
			this.requestURI = strs1[0];
			if(strs1.length>1) {
				this.queryString = strs1[1];
				parseParameters(this.queryString);
			}
			Set<Entry<String,String>> set = parameters.entrySet();
			for(Entry<String,String> entry : set) {
				System.out.println(entry);
			}
		}else {
			this.requestURI = this.url;
		}
		System.out.println("requestURI:"+requestURI);
		System.out.println("queryString:"+queryString);
	}
	
	/**
	 * 解析参数
	 * 该格式应当为:
	 * name1=value1&name2=value2&...
	 */
	public void parseParameters(String line) {
		try {
			/*
			 * 将line进行转码，将所有%XX内容转换为对应字符
			 * 
			 * URLDecoder用来对URL中%xx内容进行解码
			 * 其提供了静态方法:static String decode(String str,String csn)对给定的字符串str解码，
			 * 将其中所有%XX内容按照给定的字符集转换为对应字符并替换%XX,将替换好的字符串返回
			 */
			line = URLDecoder.decode(line, "utf-8");
			String[] strs2 = line.split("&");
			for(int i=0;i<strs2.length;i++) {
				//根据=拆分参数名和参数值
				String[] strs3 = strs2[i].split("=");
				if(strs3.length>1) {
					parameters.put(strs3[0], strs3[1]);
				}else {
					parameters.put(strs3[0], null);
				}
			}
			System.out.println("parameters:"+parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 解析消息头*/
	private void parseHeaders() {
		System.out.println("开始解析消息头...");
		/*
		 * 1:循环读取一行字符串,若读取到的是空字符串则表示单独读取到了CRLF，
		 * 那么就停止循环，因为所有的消息头均读取完毕。
		 * 2:每当读取一行字符串后，要按照": "拆分为两项，第一项为消息头名字，
		 * 第二项为消息头的值。
		 * 3:将该消息头信息存入到属性headers这个map中完成消息头解析工作。
		 */
		String line = null;
		while(!"".equals((line=readLine()))) {
			String[] strs = line.split(":\\s");
			headers.put(strs[0], strs[1]);
		}
		System.out.println("解析消息头完毕!");
		System.out.println("headers:"+headers);
		Set<Entry<String, String>> entries = headers.entrySet();
		for(Entry<String, String> e:entries) {
			//e代表map集合中的每个key-value对
			//e.getKey()方法可以获取key的值
			//e.getValue()方法可以获取key对应的value
			System.out.print(e.getKey());
			System.out.print(": ");
			System.out.println(e.getValue());
		}
	}
	/** 解析消息正文*/
	private void parseContent() {
		System.out.println("开始解析消息正文...");
		/*
		 * 首先判断当前消息头中是否含有:Content-Length
		 */
		if(headers.containsKey("Content-Length")) {
			//取出消息正文长度
			int contentLength = Integer.parseInt(headers.get("Content-Length"));
			try {
				byte[] data = new byte[contentLength];
				//读取消息正文内容
				in.read(data);
				/*
				 * 再跟进消息头中:Content-Type来判断消息正文内容是什么
				 */
				String contentType = headers.get("Content-Type");
				//判断是否为form表单
				if("application/x-www-form-urlencoded".equals(contentType)) {
					System.out.println("解析form表单数据！！！！！");
					//将读取到的字节还原为字符串
					String form = new String(data,"ISO8859-1");
					System.out.println("form表单内容:"+form);
					//解析表单内容
					parseParameters(form);
				}
				System.out.println("解析消息正文完毕!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 读取一行字符串，以CRLF结尾则认为一行字符串结束，
	 * 并将之前内容以一个字符串形式返回。返回的字符串中不包含最后的CRLF符
	 * @param in
	 * @return
	 */
	private String readLine() {
		StringBuilder builder = new StringBuilder();
		try {
			int d = -1;
			char c1 = 'a';
			char c2 = 'a';
			while((d = in.read())!=-1) {
				c2 = (char)d;
				if(c1==13&&c2==10) {
					break;
				}
				builder.append(c2);
				c1 = c2;
			}
			return builder.toString().trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	public String getMethod() {
		return method;
	}
	public String getUrl() {
		return url;
	}
	public String getProtocol() {
		return protocol;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public String getQueryString() {
		return queryString;
	}
	
	/**
	 * 根据给定的消息头名字获取对应的值
	 */
	public String getHeader(String name) {
		return headers.get(name);
	}
	
	/**
	 *获取给定参数
	 */
	public String getparameter(String name) {
		return this.parameters.get(name);
	}
}
