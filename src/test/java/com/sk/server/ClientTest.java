package com.sk.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sk.server.http.HttpProtocolProcessor;
import com.sk.server.http.HttpServerHandler;

public class ClientTest {
	
	private static final Logger log = LoggerFactory.getLogger(ClientTest.class);
	
	@BeforeClass
	public static void init() {
		//Runnable 인터페이스를 구현하면, Thread 를 생성할 수 있다.메인쓰레드와 다른 쓰레드에서 실행된다.
		Thread t = new Thread(new Server(HttpServerHandler.newServerHandlerFactory()).withProtocolProcessor(new HttpProtocolProcessor()));
		t.start();
		log.info("server start!");
	}

	@Test
	public void test() {
		try {
			Socket socket = new Socket("localhost", 8080);
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			writer.print("GET /index.html HTTP/1.1\r\n");
			writer.print("Host: localhost:8080\r\n");
			writer.print("Connection: keep-alive\r\n");
			writer.print("\r\n");
			writer.flush();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			StringBuilder lines = new StringBuilder();
			
			while(true) {
				String line = reader.readLine();
				if(line == null)break;
				lines.append(line + "\r\n");
			}
			
			String content = lines.toString();
			log.info("\r\n" + content);
			
			writer.close();
			reader.close();
			socket.close();
			
			Assert.assertTrue(content.contains("테스트 페이지 입니다"));
			
			//Assertions.assertThat(lines).contains("HTTP/1.1 200 OK");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void end() {
		log.info("server end!");
	}

}
