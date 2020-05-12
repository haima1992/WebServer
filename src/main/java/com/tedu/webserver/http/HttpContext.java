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

/**
 * HTTP协议中的相关定义内容
 * @author 杨帆
 *
 */
public class HttpContext {
	/*状态代码与描述的映射关系*/
	private static Map<Integer,String> STATUS_CODE_REASON_MAPPING = new HashMap<Integer, String>();
	
	/** 介质类型映射*/
	//key:实体资源后缀名
	//value:MIME对应值，格式如:text/html
	private static Map<String,String> MIME_TYPE_MAPPING = new HashMap<String, String>();
	
	static {
		//初始化状态代码与描述的映射
		initStatusCodeReasonMapping();
		//初始化介质类型映射
		initMimeTypeMapping();
	}
	
	/** 初始化介质类型映射*/
	private static void initMimeTypeMapping() {
		/*
		 * 1:读取conf/web.xml文件
		 * 2:获取根元素下所有名为<mime-mapping>的子元素
		 * 3:将每个<mime-mapping>元素下的子元素:
		 * 	 <extension>中间的文本作为key
		 * 	 <mime-type>中间的文本作为value
		 * 	  存入到MIME_TYPE_MAPPING,完成初始化操作
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("conf/web.xml"));
			Element root = doc.getRootElement();
			System.out.println("根元素:"+root.getName());
			@SuppressWarnings("unchecked")
			List<Element> list = root.elements("mime-mapping");
			for(Element mmEle : list) {
				String key = mmEle.element("extension").getTextTrim();
				String value = mmEle.element("mime-type").getTextTrim();
				MIME_TYPE_MAPPING.put(key,value);
			}
//			System.out.println("解析完毕！");
//			System.out.println(MIME_TYPE_MAPPING.size());
//			Set<Entry<String,String>> set = MIME_TYPE_MAPPING.entrySet();
//			for(Entry<String, String> e : set) {
//				System.out.println(e);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 初始化状态代码与描述的映射*/
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
	
	/** 根据给定的状态代码返回对应的状态描述*/
	public static String getStatusReason(int statusCode) {
		return STATUS_CODE_REASON_MAPPING.get(statusCode);
	}
	
	public static String getMimeType(String ext) {
		return MIME_TYPE_MAPPING.get(ext);
	}
	/**主函数 测试代码*/
	public static void main(String[] args) {
		initMimeTypeMapping();
	}
}
