package com.sk.server.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
		
		HttpRequest result = null;
		try {
			result = new HttpRequest(socket.getInputStream());
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