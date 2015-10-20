package com.diy.helpers.v1;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class RESTHelperCallRequest {

	private String url;
	private String contentType;
	private String content;
	private String requestType;
    private String[] parameters;
    private List<NameValuePair> params;

    public RESTHelperCallRequest() {
        params = new ArrayList<NameValuePair>();
    }

    public String getURL() {
		return url;
	}
	
	public void setURL(String url) {
		this.url = url;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public boolean isValid() {
        if (requestType.equals("GET")) {
            if (url!=null) {
                return true;
            } else {
                return false;
            }
        } else if(requestType.equals("POST")) {
            if (url!=null && contentType!=null && content!=null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void addParameter(String key, String value) {
        params.add(new BasicNameValuePair(key, value));
    }

    public List<NameValuePair> getParameters() {
        return params;
    }
}
