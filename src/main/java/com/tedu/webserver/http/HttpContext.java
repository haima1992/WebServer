package com.tedu.webserver.http;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class HttpContext {
	private static Map<Integer,String> STATUS_CODE_REASON_MAPPING = new HashMap<Integer, String>();
	
	private static Map<String,String> MIME_TYPE_MAPPING = new HashMap<String, String>();
	
	static {
		initStatusCodeReasonMapping();
		initMimeTypeMapping();
	}
	
	private static void initMimeTypeMapping() {
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("conf/web.xml"));
			Element root = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> list = root.elements("mime-mapping");
			for(Element mmEle : list) {
				String key = mmEle.element("extension").getTextTrim();
				String value = mmEle.element("mime-type").getTextTrim();
				MIME_TYPE_MAPPING.put(key,value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void initStatusCodeReasonMapping() {
		STATUS_CODE_REASON_MAPPING.put(200, "OK");
		STATUS_CODE_REASON_MAPPING.put(201, "Created");
		STATUS_CODE_REASON_MAPPING.put(202, "Accepted");
		STATUS_CODE_REASON_MAPPING.put(204, "No Content");
		STATUS_CODE_REASON_MAPPING.put(301, "Moved Permanently");
		STATUS_CODE_REASON_MAPPING.put(302, "Moved Temporarily");
		STATUS_CODE_REASON_MAPPING.put(304, "Not Modified");
		STATUS_CODE_REASON_MAPPING.put(400, "Bad Request");
		STATUS_CODE_REASON_MAPPING.put(401, "Unauthorized");
		STATUS_CODE_REASON_MAPPING.put(403, "Forbidden");
		STATUS_CODE_REASON_MAPPING.put(404, "Not Found");
		STATUS_CODE_REASON_MAPPING.put(500, "Internal Server Error");
		STATUS_CODE_REASON_MAPPING.put(501, "Not Implemented");
		STATUS_CODE_REASON_MAPPING.put(502, "Bad Gateway");
		STATUS_CODE_REASON_MAPPING.put(503, "Service Unavailable");
	}
	
	public static String getStatusReason(int statusCode) {
		return STATUS_CODE_REASON_MAPPING.get(statusCode);
	}
	
	public static String getMimeType(String ext) {
		return MIME_TYPE_MAPPING.get(ext);
	}
}
