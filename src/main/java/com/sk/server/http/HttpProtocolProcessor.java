package com.sk.server.http;

import static java.nio.file.Files.readAllBytes;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sk.server.ProtocolProcessor;
import com.sk.server.Request;

public class HttpProtocolProcessor implements ProtocolProcessor{
	
	private static final Logger log = LoggerFactory.getLogger(HttpProtocolProcessor.class); 

	@Override
	public HttpResponse service(Request request) {
		
		HttpRequest httpRequest = (HttpRequest)request;
		HttpResponse httpResponse = new HttpResponse();
		
		String requestPath = httpRequest.getRequestPath();
		
		//requestPath 를 가지고 처리할 Controller 와 매핑 시킨다.
		
		// classPath 상에 있는 자원 접근 방법1.
		//InputStream inputStream = getClass().getResourceAsStream("/static/index.html");
		try {
			
			URL url = getClass().getResource("/static" + requestPath);
			if(url==null) requestPath = "/404.html";
			url = getClass().getResource("/static" + requestPath);
			
			Path path = Paths.get(url.toURI());
			
			//if(isDirectory(path)) path = path404;
			
			byte[] resource = readAllBytes(path);
			
			httpResponse.setBody(resource);
		} catch (IOException | URISyntaxException e) {
			log.error("클리이언트 요청 파일 읽기중 오류 발생!");
			throw new IllegalStateException(e.getMessage(), e);
		}
		
		return httpResponse;
	}
}