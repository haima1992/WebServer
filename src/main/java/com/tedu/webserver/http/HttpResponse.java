package com.tedu.webserver.http;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpResponse {
	
	private int statusCode = 200;
	
	private Map<String,String> headers = new HashMap<String, String>();
	
	
	private File entity;
	
	private byte[] data;
	
	@SuppressWarnings("unused")
	private Socket socket; 
	private OutputStream out;
	
	public HttpResponse(Socket socket) {
		try {
			this.socket = socket;
			this.out = socket.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void flush() {
		sendStatusLine();
		sendHeaders();
		sendContent();
	}
	
	private void sendStatusLine() {
		try {
			String line = "HTTP/1.1"+" "+statusCode+" "+HttpContext.getStatusReason(statusCode);
			println(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendHeaders() {
		try {
			Set<Entry<String,String>> entries = headers.entrySet();
			for(Entry<String,String> header : entries) {
				String key = header.getKey();
				String value = header.getValue();
				String line = key+": "+value;
				println(line);
			}
			println("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendContent() {
		if(data != null) {
			try {
				out.write(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(entity!=null) {
			try (
			    FileInputStream fis = new FileInputStream(entity);
			){
				byte[] data = new byte[1024*10];
				int len = -1;
				while((len=fis.read(data))!=-1) {
					out.write(data, 0, len);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void println(String line) {
		try {
			out.write(line.getBytes("ISO8859-1"));
			out.write(13);
			out.write(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public File getEntity() {
		return entity;
	}
	public void setEntity(File entity) {
		this.entity = entity;
		String name = entity.getName();
		String ext = name.substring(name.lastIndexOf(".")+1);
		String contentType = HttpContext.getMimeType(ext);
		headers.put("Content-Type", contentType);
		
		headers.put("Content-Length", entity.length()+"");
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public void putHeader(String name,String value) {
		this.headers.put(name,value);
	}
	
	public void sendRedirect(String url) {
		this.setStatusCode(302);
		this.putHeader("Location",url);
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
}
