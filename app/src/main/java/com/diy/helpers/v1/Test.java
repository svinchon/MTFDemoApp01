package com.diy.helpers.v1;

public class Test {

	public static void main(String[] args) {
		// XMLHelper
		String strInputAsString = "<root><item index='1'>item A</item><item index='2'>item B</item></root>";
		String strXQUERYAsString = "declare variable $doc external;<xdata>{for $i in $doc/root/item return $i}</xdata>";
		String xml = XMLHelper.runXQueryAgainstXML_saxon9(strInputAsString, strXQUERYAsString);
		Utils.log(xml);
		//Utils.Log(XMLHelper.prettyPrintXML(xml));
		String str = XMLHelper.getValueFromXML(xml, "/xdata/item[@index='2']");
		Utils.log(str);
		// RESTHElper
		RESTHelper rh = new RESTHelper();
		RESTHelperCallRequest req;
		req = rh.getReq();
		String json;
		json = "{\"json\":\"true\", node: { key: value } }";
		json = "{\"json\":\"true\", node: { key: [ 'A', 'B'] } }";
		json = "{\"json\":\"true\", node: { key: [ 'A', { key2 : val2 } ] } }";
		req.setURL("http://localhost:18080/MTFServer01/TestRestService?return=json");
		req.setContent(json);
		rh.execute();
		RESTHelperCallResult res = rh.getRESTCallResult();
		Utils.log("status = " + res.getStatus(), "new test");
		Utils.log("content = " + res.getContent());
		Utils.log("content type = " + res.getContentType());
		rh = null;
		res = null;
		req =  null;
		// JSONHelper
		String jPath = "/node/key[2]/key2";
		String jPathResult = JSONHelper.runJPath(json, jPath);
		Utils.log("jpath: "+ jPath +" = " + jPathResult);
	}
	
	public Test() {
		
	}
}
