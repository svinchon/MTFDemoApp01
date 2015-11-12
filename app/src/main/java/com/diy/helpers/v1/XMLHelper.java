package com.diy.helpers.v1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

//saxon 8
//import java.util.Properties;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.sax.SAXSource;
//import javax.xml.transform.stream.StreamResult;
//import org.xml.sax.InputSource;
//import net.sf.saxon.Configuration;
//import net.sf.saxon.om.DocumentInfo;
//import net.sf.saxon.query.DynamicQueryContext;
//import net.sf.saxon.query.QueryModule;
//import net.sf.saxon.query.StaticQueryContext;
//import net.sf.saxon.query.XQueryExpression;
//import net.sf.saxon.xqj.SaxonXQDataSource;
//import javax.security.auth.login.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.io.StringWriter;

//saxon 9
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQSequence;
import javax.xml.xquery.XQStaticContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.sf.saxon.xqj.SaxonXQDataSource;

public class XMLHelper {

	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	
	public static String runXQueryAgainstXML_saxon9(String xml, String xq) throws HelperException {
		String strResult = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
			SaxonXQDataSource ds = new SaxonXQDataSource();
			XQConnection con = ds.getConnection();
			XQStaticContext ctx = con.getStaticContext();
			ctx.setBindingMode(XQConstants.BINDING_MODE_DEFERRED);
			con.setStaticContext(ctx);
			XQPreparedExpression expr = con.prepareExpression(xq);
			expr.bindDocument(new QName("doc"), bais, null, null);
			XQSequence result = expr.executeQuery();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			result.writeSequence(baos, null);
			strResult = baos.toString("UTF-8");
			result.close();
			expr.close();
			con.close();
		} catch (Exception e) {
			//e.printStackTrace();
			String message = "";
			//message += "\n"+"Input data: "+xml.replaceAll("[\n\t]","");
			message += ""+"Error message : "+e.getMessage();
			message += "\n"+"Input xquery: "+xq.replaceAll("[\n\t]", "");
			HelperException he = new HelperException("XMLHelper", "runXQueryAgainstXML_saxon9", message);
			throw he;
			//strResult = "ERROR: "+e.getMessage();
		}
		return strResult;
	}
	
	public static String getValueFromXML(String xml, String xp) {
		String strResult = null;
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				new InputSource(new StringReader(xml))
			);
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList) xpath.evaluate(xp, doc, XPathConstants.NODESET);
			if (nodes!=null && nodes.getLength()>0) {
				String strValue=nodes.item(0).getTextContent();
				//System.out.println("XML Helper => Extracted "+strXPath+" value: "+strValue);
				return strValue;
			} else {
				//strResult = "ERROR strGetValueFromXML (xpath returned null or empty)";
				System.out.println("Input data: "+xml.replaceAll("[\n\t]",""));
				System.out.println("Input xpath: "+xp.replaceAll("[\n\t]",""));
				System.out.println("Error message : "+"xpath returned null or empty");
			}
			nodes = null;
			xpath = null;
			doc = null;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Input data: "+xml.replaceAll("[\n\t]",""));
			System.out.println("Input xpath: "+xp.replaceAll("[\n\t]",""));
			System.out.println("Error message : "+e.getMessage());
			//strResult = "ERROR: "+e.getMessage();
		}
		return strResult;
	}

	public static String[] getValuesFromXML(String xml, String xp) {
		String[] strResult= null;
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				new InputSource(new StringReader(xml))
			);
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList) xpath.evaluate(xp, doc, XPathConstants.NODESET);
			if (nodes!=null && nodes.getLength()>0) {
				int iArraySize = nodes.getLength();
				strResult = new String[iArraySize];
				for (int iNode=0;iNode<iArraySize;iNode++) {
					strResult[iNode] = nodes.item(iNode).getTextContent();					
				}
			} else {
				//strResult = new String[1];
				//strResult[0] = "ERROR strGetValueFromXML (xpath returned null or empty)";
				System.out.println("Input data: "+xml.replaceAll("[\n\t]",""));
				System.out.println("Input xpath: "+xp.replaceAll("[\n\t]",""));
				System.out.println("Error message : "+"xpath returned null or empty");
			}
			nodes = null;
			xpath = null;
			doc = null;
		} catch (Exception e) {
			System.out.println("Input data: "+xml.replaceAll("[\n\t]",""));
			System.out.println("Input xpath: "+xp.replaceAll("[\n\t]",""));
			System.out.println("Error message : "+e.getMessage());
			//strResult = new String[1];
			//strResult[0] = "ERROR: "+e.getMessage();
			e.printStackTrace();
		}
		return strResult;
	}

	public static String prettyPrintXML(String xml) {
		String strReturn = null;
		try {
			Source sourceContent = new StreamSource(new StringReader(xml));
			StringWriter sw = new StringWriter();
			StreamResult xmlOutput = new StreamResult(sw);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
			transformer.transform(sourceContent, xmlOutput);						
			strReturn = xmlOutput.getWriter().toString();
			transformer = null;
			xmlOutput = null;
			sw.close();
			sourceContent = null;			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return strReturn;
	}
	
//	public static String updateNodeInXMLFile(String strFileName, String strXPath, String strValue) {
//		String strReturn = "nothing done";
//		try {
//			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(strFileName));
//			XPath xpath = XPathFactory.newInstance().newXPath();
//			NodeList nodes = (NodeList) xpath.evaluate(strXPath, doc, XPathConstants.NODESET);
//			for (int idx = 0; idx < nodes.getLength(); idx++) {
//			    nodes.item(idx).setTextContent(strValue);
//			}
//			Transformer xformer = TransformerFactory.newInstance().newTransformer();
//			xformer.transform(new DOMSource(doc), new StreamResult(new File(strFileName)));
//			xformer = null;
//			doc = null;
//			strReturn = "strUpdateNodeInXMLFile sucessfull";
//		} catch (Exception e) {
//			e.printStackTrace();
//			strReturn = "Error during strUpdateNodeInXMLFile";
//		}
//		return strReturn;
//	}

	public static String updateNodeInXMLString(String strXML, String strXPath, String strValue) {
		String strReturn = "nothing done";
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(strXML)));
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList) xpath.evaluate(strXPath, doc, XPathConstants.NODESET);
			for (int idx = 0; idx < nodes.getLength(); idx++) {
				nodes.item(idx).setTextContent(strValue);
			}
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			StringWriter sw = new StringWriter();
			xformer.transform(new DOMSource(doc), new StreamResult(sw));
			sw.flush();
			strXML = sw.toString();
			xformer = null;
			doc = null;
//			another option to write xml to string in more elegant way
//			DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
//		    LSSerializer lsSerializer = domImplementation.createLSSerializer();
//		    return lsSerializer.writeToString(doc);
			strReturn = strXML;
		} catch (Exception e) {
			e.printStackTrace();
			strReturn = "Error during strUpdateNodeInXMLFile";
		}
		return strReturn;
	}

	public static String convertToJSON(String xml) {
		String strReturn = null;
		try {
            JSONObject xmlJSONObj = XML.toJSONObject(xml);
            strReturn = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
		return strReturn;
    }
	
}


