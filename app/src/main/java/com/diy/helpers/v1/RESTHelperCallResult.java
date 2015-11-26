package com.diy.helpers.v1;

public class RESTHelperCallResult {

	private String status;
	private String contentType;
	private String content;
	private byte[] contentBytes;
	private String errorMessage;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getContent() { return content; }
	public void setContentBytes(byte[] contentBytes) { this.contentBytes = contentBytes; }
	public byte[] getContentBytes() { return contentBytes;}
	public void setContent(String content) {
		this.content = content;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
