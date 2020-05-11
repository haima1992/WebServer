package com.tedu.webserver.http;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	
	private String method;
	private String url;
	private String protocol;
	
	private String requestURI;
	private String queryString;
	private Map<String,String> parameters = new HashMap<String,String>();
	
	private Map<String,String> headers = new HashMap<String,String>();
	
	@SuppressWarnings("unused")
	private Socket socket;
	private InputStream in;
	public HttpRequest(Socket socket) throws EmptyRequestException{
		try {
			this.socket = socket;	
			this.in= socket.getInputStream();
			parseRequestLine();
			parseHeaders();
			parseContent();
		} catch (EmptyRequestException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private void parseRequestLine() throws EmptyRequestException{
		String line = readLine();
		String regex = "\\s";
		String[] strs = line.split(regex);
		if(strs.length<3) {
			throw new EmptyRequestException();
		}
		this.method = strs[0];
		this.url = strs[1];
		parseURL();
		this.protocol = strs[2];
	}	
	
	private void parseURL() {
		if(url.contains("?")) {
			String[] strs1 = url.split("\\?");
			this.requestURI = strs1[0];
			if(strs1.length>1) {
				this.queryString = strs1[1];
				parseParameters(this.queryString);
			}
		}else {
			this.requestURI = this.url;
		}
	}
	
	public void parseParameters(String line) {
		try {
			line = URLDecoder.decode(line, "utf-8");
			String[] strs2 = line.split("&");
			for(int i=0;i<strs2.length;i++) {
				String[] strs3 = strs2[i].split("=");
				if(strs3.length>1) {
					parameters.put(strs3[0], strs3[1]);
				}else {
					parameters.put(strs3[0], null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseHeaders() {
		String line = null;
		while(!"".equals((line=readLine()))) {
			String[] strs = line.split(":\\s");
			headers.put(strs[0], strs[1]);
		}
	}
	private void parseContent() {
		if(headers.containsKey("Content-Length")) {
			int contentLength = Integer.parseInt(headers.get("Content-Length"));
			try {
				byte[] data = new byte[contentLength];
				in.read(data);
				String contentType = headers.get("Content-Type");
				if("application/x-www-form-urlencoded".equals(contentType)) {
					String form = new String(data,"ISO8859-1");
					parseParameters(form);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
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
	
	public String getHeader(String name) {
		return headers.get(name);
	}
	
	public String getparameter(String name) {
		return this.parameters.get(name);
	}
}
