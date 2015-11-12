
package com.diy.helpers.v1;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

//import javax.xml.ws.soap.AddressingFeature;




import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XPath2XmlCreator  {
	
	Document document = null;

	public static void main(String[] args) {
		XPath2XmlCreator x2x = new XPath2XmlCreator();
		//x2x.initDocument("Documents");
		x2x.initDocumentFromString("<Documents><test>value</test></Documents>");
		x2x.addElement("/Documents/Document/FirstName", "Bob");
		x2x.addElement("/Documents/Document[2]/FirstName", "Robert");
		System.out.println(x2x.getResultingDocument());	
	}
  
	public void initDocument(String rootNode) {
		document = DocumentHelper.createDocument(DocumentHelper.createElement(rootNode));
	}
	
	public void initDocumentFromString(String xml) {
		try {
			document = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			e.printStackTrace();
			Error ne = new Error("invalid input string to create xml");
			throw ne;
		}
	}
	
	public void addElement(String xpath, String value) {
		if (document != null) {
			addElementToParent(document, xpath, value);
		} else {
			Error e = new Error("missing initDocument");
			throw e;
		}
	}
	
	public String getResultingDocument() {
		if (document != null) {
			return printDoc(document);
		} else {
			Error e = new Error("missing initDocument");
			throw e;
		}
		
	}
	
	private String printDoc(Document document) {
		String ret = null;
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("ISO-8859-1");
		StringWriter writer = new StringWriter();
		XMLWriter xmlwriter = new XMLWriter(writer, format);
		try {
			xmlwriter.write( document );
			ret = writer.getBuffer().toString();
			//System.out.println(ret);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return ret;
	}
	
	public Node addElementToParent(Document document, String xpath, String value) {
		String elementName = XPathUtils.getChildElementName(xpath);
		String parentXPath = XPathUtils.getParentXPath(xpath);
		Node parentNode = document.selectSingleNode(parentXPath);
		if(parentNode == null) { parentNode = addElementToParent(document, parentXPath, null); }
		Integer childIndex = XPathUtils.getChildElementIndex(xpath);
		if(childIndex > 1) {
			List<?> nodelist = document.selectNodes(XPathUtils.createPositionXpath(xpath, childIndex));
			int nodesToCreate = childIndex - nodelist.size() - 1;
			for(int i = 0; i < nodesToCreate; i++) { ((Element)parentNode).addElement(elementName); }
		}
		Element created = ((Element)parentNode).addElement(elementName);
		if(null != value) {
			created.addText(value);
		}
		return created;
	}

}
