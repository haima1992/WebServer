package com.tedu.websever.core;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ServerContext {
	private static Map<String,String> SERVLET_MAPPING = new HashMap<String,String>();
	static {
		initServletMapping();
	}
	private static void initServletMapping() {
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("conf/servlets.xml"));
			Element root = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> list = root.elements();
			for(Element e : list) {
				String key = e.attributeValue("url");
				String value = e.attributeValue("className");
				SERVLET_MAPPING.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getServletName(String url) {
		return SERVLET_MAPPING.get(url);
	}
}
