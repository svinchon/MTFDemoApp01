package com.diy.helpers.v1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JSONHelper {

	public static boolean isJSONValid_json_org(String test) {
		try { new JSONObject(test); } catch (JSONException ex) { try { new JSONArray(test); } catch (JSONException ex1) { return false; } }
		return true;
	}

	public static boolean isJSONValid_gson(String JSON_STRING) {
		try {
			new Gson().fromJson(JSON_STRING, Object.class); return true;
		} catch(com.google.gson.JsonSyntaxException ex) { 
			return false;
		}
	}

	public static String runJPath(String json, String xq) {
		String strResult = null;
		try {
			String[] xqa = xq.split("/");
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray;
			for(int i=1; i<xqa.length; i++) {
				int iIndex = 0;
				if (xqa[i].contains("[")) {
					String sIndex = xqa[i];
					sIndex = sIndex.replaceAll(".*\\[", "");
					sIndex = sIndex.replaceAll("]", "");
					iIndex = new Integer(sIndex).intValue();
					xqa[i] = xqa[i].substring(0, xqa[i].indexOf("["));
				}
				try {
					jsonArray = jsonObject.getJSONArray(xqa[i]);
					if (iIndex == 0) {
						strResult = jsonArray.toString();
					} else {
						strResult = jsonArray.get(iIndex-1).toString();
					}
				} catch (JSONException e) {
					try {
						strResult = jsonObject.getString(xqa[i]);
					} catch (Exception e3) {
						strResult = null;
					}
				}
				try {
					jsonObject = new JSONObject(strResult);
				} catch (Exception e2) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strResult;
	}
	
	// TODO code missing for updateNodeInJSON
	public static String updateNodeInJSON(String json, String xq, String newValue) {
		String strResult = null;
		return strResult;
	}
	
	public static String prettyPrint(String json) {
		String prettyJsonString = null;
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(json);
			prettyJsonString = gson.toJson(je);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prettyJsonString;
	}

	public static String convertToXML(String json) {
		String strResult = null;
		try {
			JSONObject obj = new JSONObject(json);
			strResult = XML.toString(obj);
		}catch (Exception e) {

		}
		return strResult;
	}

}
