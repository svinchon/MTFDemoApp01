package com.diy.helpers.v1;

import org.apache.commons.lang3.StringUtils;

public class XPathUtils {

	private static final String SLASH = "/";
	private static final String R_BRACKET = "]";
	private static final String L_BRACKET = "[";
	
	public static final String getChildElementName(String xpath) {
		if(StringUtils.isEmpty(xpath)) { return null; }
		String childName = xpath.substring(xpath.lastIndexOf(SLASH) + 1);
		return stripIndex(childName);
	}
	
	public static final String getParentXPath(String xpath) {
		if(StringUtils.isEmpty(xpath) || xpath.lastIndexOf(SLASH) <= 0) { return null; }
		return xpath.substring(0, xpath.lastIndexOf(SLASH));
	}
	
	public static Integer getChildElementIndex(String xpath) {
		if(StringUtils.isEmpty(xpath)) { return null; }
		if(xpath.endsWith(R_BRACKET)) {
			String value = xpath.substring(xpath.lastIndexOf(L_BRACKET) + 1, xpath.lastIndexOf(R_BRACKET));
			if(StringUtils.isNumeric(value)) { return Integer.valueOf(value); }
		} 
		return 1;
	}
	
	public static String createPositionXpath(String xpath, Integer childIndex) {
		if(StringUtils.isEmpty(xpath)) { return null; }
		return stripIndex(xpath) + "[position()<" + childIndex + "]";
	}
	
	private static String stripIndex(String childName) {
		if(childName.endsWith(R_BRACKET)) { return childName.substring(0, childName.lastIndexOf(L_BRACKET));
		} else {
			return childName;
		}
	}

	

	
}
