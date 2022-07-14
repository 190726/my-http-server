package com.sk.server.http;

import java.util.HashMap;
import java.util.Map;

import com.sk.server.Request;

public class HttpRequest implements Request{
	
	String requestUrl;
	
	Map<String, String> headers;
	
	public HttpRequest() {
	}
	
	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
	public void addHeader(String key, String value) {
		if(headers==null) headers = new HashMap<>();
		headers.put(key, value);
	}
	
	public String getHeader(String key) {
		if(headers==null) return null;
		return headers.get(key);
	}
	
	public Map<String, String> getAllHeaders() {
		return headers;
	}
}