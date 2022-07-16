package com.sk.server.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.sk.server.Request;

public class HttpRequest implements Request{
	
	private String method;
	
	private String requestPath;
	
	private String host;

	Map<String, String> headers;
	
	Map<String, String> parameters;
	
	private InputStream inputStream;
	
	public HttpRequest() {
	}
	
	public HttpRequest(InputStream inputStream) {
		this.inputStream = inputStream;
		parseInputStream();
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
	
	public String getParameter(String key) {
		return parameters==null ? null: parameters.get(key);
	}
	
	public String getHost() {
		return host;
	}
	
	public String getRequestPath() {
		return requestPath;
	}
	
	public String getMethod() {
		return method;
	}
	
	private void parseInputStream() {
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			
    		// readLine()은 line 을 \n 또는 \r 으로 끝나는 부분을 하나의 line 으로 본다.  client 에서 라인 단위로 보내야 한다. 
    		//1. 첫번째 라인은 Http요청메서드 / 요청url이 포함되어 있다. 
			String firstLine = reader.readLine();
			if(firstLine == null ) throw new IllegalStateException("유효하지 않은 HTTP request 입니다.");
			String[] tokens = firstLine.split(" ");
			
			this.method = tokens[0];
			
			setRequestPath(tokens[1]);
			//2. Http Header 파라미터 파싱
    		setHeader(reader);
    		
    		if(method.equals("POST")) {
    			int contentLength = Integer.parseInt(headers.get("Content-Length"));
    			char[] body = new char[contentLength];
    			reader.read(body, 0, contentLength);
    			parameters = parseQueryString(String.copyValueOf(body));
    		}
    		
		} catch (IOException e) {
			//오류메시지와 cause Exception 을 가지고, RuntimeException 을 생성한다. cause Exception 은 'Caused by:' 하위로 stack trace 가 append 된다.
			throw new IllegalStateException("HttpServerHandler request read IOException occured !", e);
		}
	}

	private void setHeader(BufferedReader reader) throws IOException {
		String readLine;
		while((readLine = reader.readLine()) != null) {
			if(readLine.equals("")) break;
			int idx =  readLine.indexOf(":");
			if(readLine.startsWith("Host:")) {
				setHost(readLine.substring(idx+2, readLine.length()));
			}else {
				addHeader(readLine.substring(0 , idx), readLine.substring(idx+2, readLine.length()));
			}
		}
	}

	private void setRequestPath(String requestPath) {
		
		int startIndexOf = requestPath.indexOf("?");
		if(startIndexOf==-1) {
			this.requestPath = requestPath;
			return;
		}
		
		this.requestPath = requestPath.substring(0, startIndexOf);
		String params = requestPath.substring(startIndexOf+1);
		
		parameters = parseQueryString(params);
	}

	private Map<String, String> parseQueryString(String params) {
		Map<String, String> results = new HashMap<>();
		String[] paramterArr = params.split("&");
		Arrays.stream(paramterArr)
			.map(parameter -> parameter.split("="))
			.forEach(p -> results.put(p[0], p[1]));
		return results;
	}
	
	private void setHost(String host) {
		this.host = host;
	}
	
	
}