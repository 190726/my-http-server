package com.sk.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sk.server.http.HttpProtocolProcessor;
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
		} catch (IOException | InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void handleStreamFromSocket(ServerSocket serverSocket) throws IOException, InterruptedException {
		
		ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		while(!Thread.currentThread().isInterrupted()) {
			threadPool.execute(new HttpWorker(serverHandlerFactory.newServerHandlerInstance(serverSocket.accept())));
//			Socket accept = serverSocket.accept();
//			accept.setSoTimeout(8000); //8000ms 동안 소켓에서 읽혀지는 패킷이 없으면 java.net.SocketTimeoutException: Read timed out 발생
		}
		
		threadPool.awaitTermination(5, TimeUnit.SECONDS);
		threadPool.shutdown();
		
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