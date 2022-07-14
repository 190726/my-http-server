package com.sk.server.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sk.server.Response;
import com.sk.server.ServerHandler;
import com.sk.server.ServerHandlerFactory;

public class HttpServerHandler implements ServerHandler{
	
	private static Logger log = LoggerFactory.getLogger(HttpServerHandler.class);
	
	private Socket socket;
	
	private BufferedReader reader;
	
	private DataOutputStream outStream;
	
	public HttpServerHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void close() throws IOException {
		
		if(reader!=null) reader.close();
		if(outStream!=null) outStream.close();
		if(socket!=null) socket.close();
		
	}

	@Override
	public HttpRequest readRequst() {
		
		HttpRequest result = new HttpRequest();
			
		try {
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			
			String readLine = "";
			
    		// readLine()은 line 을 \n 또는 \r 으로 끝나는 부분을 하나의 line 으로 본다.  client 에서 라인 단위로 보내야 한다. 
    		//1. 첫번째 라인은 Http요청메서드 / 요청url이 포함되어 있다. 
			String firstLine = reader.readLine();
			if(firstLine == null ) throw new IllegalStateException("유효하지 않은 HTTP request 입니다.");
			String[] tokens = firstLine.split(" ");
			
			result.setRequestUrl(tokens[1]);
			
			//2. Http Header 파라미터 파싱
    		while((readLine = reader.readLine()) != null) {
    			if(readLine.equals("")) break;
    			int idx =  readLine.indexOf(":");
    			//header를 key, value 로 파싱하여 저장, ex)"Host: localhost:8080"
    			result.addHeader(readLine.substring(0 , idx), readLine.substring(idx+2, readLine.length()));
    		}
    		
    		result.getAllHeaders().forEach((k,v) -> System.out.println(k +": " + v));
    		
		} catch (IOException e) {
			//오류메시지와 cause Exception 을 가지고, RuntimeException 을 생성한다. cause Exception 은 'Caused by:' 하위로 stack trace 가 append 된다.
			throw new IllegalStateException("HttpServerHandler request read IOException occured !", e);
		}
		return result;
	}

	@Override
	public void writeResponse(Response response) {
		
		try{
			this.outStream = new DataOutputStream(socket.getOutputStream());
			
			byte[] body = response.getBody();
			int lengthContent = body.length;
			
			outStream.writeBytes("HTTP/1.1 200 OK\r\n");
			outStream.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			outStream.writeBytes("Content-Length: "+lengthContent + "\r\n");
			outStream.writeBytes("\r\n");
			
			outStream.write(body, 0, lengthContent);
			
			outStream.writeBytes("\r\n");
			
			outStream.flush();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new IllegalStateException("HttpServerHandler response write IOException occured !");
		}
	}
	
	public static ServerHandlerFactory newServerHandlerFactory() {
		
		return new ServerHandlerFactory() {
			
			@Override
			public ServerHandler newServerHandlerInstance(Socket socket) {
				return new HttpServerHandler(socket);
			}
		};
		
	}
	
}