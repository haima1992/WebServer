package com.tedu.webserver.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 服务端环境--相关配置信息
 * @author 杨帆
 *
 */
public class ServerContext {
	/*
	 * Servlet映射
	 * key:请求路径
	 * value:Servlet类名
	 */
	private static Map<String,String> SERVLET_MAPPING = new HashMap<String,String>();
	static {
		initServletMapping();
	}
	/** 初始化Servlet映射*/
	private static void initServletMapping() {
		/*
		 * 加载conf/servlets.xml
		 * 将每个<servlet>标签中的属性url的值作为key
		 * className属性的值作为value存入到SERVLET_MAPPING中
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("conf/servlets.xml"));
			Element root = doc.getRootElement();
			System.out.println("根元素:"+root);
			@SuppressWarnings("unchecked")
			List<Element> list = root.elements();//根标签只有一种子标签时可以不传参
			for(Element e : list) {
				String key = e.attributeValue("url");
				String value = e.attributeValue("className");
				SERVLET_MAPPING.put(key, value);
			}
			System.out.println("读取配置文件完毕！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** 根据请求获取对应的Servlet名字*/
	public static String getServletName(String url) {
		return SERVLET_MAPPING.get(url);
	}
}
