package com.sk.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sk.server.http.HttpProtocolProcessor;
import com.sk.server.http.HttpRequest;
import com.sk.server.http.HttpResponse;
import com.sk.server.http.HttpServerHandler;

public class Server implements Runnable{
	
	private static final Logger log = LoggerFactory.getLogger(Server.class);
	
	private static final int DEFAULT_PORT = 8080;
	
	private ServerHandlerFactory serverHandlerFactory;
	
	private ProtocolProcessor processor;
	
	public Server(ServerHandlerFactory serverHandlerFactory) {
		this.serverHandlerFactory = serverHandlerFactory;
	}
	
	public Server withProtocolProcessor(ProtocolProcessor processor) {
		this.processor = processor;
		return this;
	}
	
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);){
    		handleStreamFromSocket(serverSocket);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void handleStreamFromSocket(ServerSocket serverSocket) throws IOException {
		while(!Thread.currentThread().isInterrupted()) {
			log.info("request wating...");
			Socket socket = serverSocket.accept();
			Thread thread = new Thread(new HttpWorker(serverHandlerFactory.newServerHandlerInstance(socket)));
			thread.start();
		}
	}
	
	private class HttpWorker implements Runnable{
		
		private ServerHandler serverHandler;
		
		public HttpWorker(ServerHandler serverHandler) {
			this.serverHandler = serverHandler;
		}

		@Override
		public void run() {
			try {
				Request request = serverHandler.readRequst();
				Response response = processor.service(request);
				serverHandler.writeResponse(response);
			} catch(Exception e) {
				log.error(e.getMessage(), e);
			}finally {
				try {
					serverHandler.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new Server(HttpServerHandler.newServerHandlerFactory())
				.withProtocolProcessor(new HttpProtocolProcessor()).run();
	}
}