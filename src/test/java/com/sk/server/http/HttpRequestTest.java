package com.sk.server.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;


public class HttpRequestTest {

	@Test
	public void getRequestTest() {
		try {
			InputStream in = new FileInputStream(new File("src/test/resources/http_get_request_sample.txt"));
			
			HttpRequest request = new HttpRequest(in);
			
			Assert.assertEquals(request.getRequestPath(), "/index.html");
			Assert.assertEquals(request.getMethod(), "GET");
			Assert.assertEquals(request.getParameter("userId"), "sixstar");
			Assert.assertEquals(request.getParameter("userName"), "HongKilDong");
			Assert.assertEquals(request.getHost(), "localhost:8080");
			Assert.assertEquals(request.getHeader("Connection"), "kepp-alive");
			Assert.assertEquals(request.getHeader("Accept"), "*/*");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getRequestTest2() {
		try {
			InputStream in = new FileInputStream(new File("src/test/resources/http_get_request_sample2.txt"));
			
			HttpRequest request = new HttpRequest(in);
			
			Assert.assertEquals(request.getRequestPath(), "/index.html");
			Assert.assertEquals(request.getMethod(), "GET");
			Assert.assertEquals(request.getParameter("userId"), null);
			Assert.assertEquals(request.getParameter("userName"), null);
			Assert.assertEquals(request.getHost(), "localhost:8080");
			Assert.assertEquals(request.getHeader("Connection"), "kepp-alive");
			Assert.assertEquals(request.getHeader("Accept"), "*/*");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void postRequestTest() {
		try {
			InputStream in = new FileInputStream(new File("src/test/resources/http_post_request_sample.txt"));
			
			HttpRequest request = new HttpRequest(in);
			
			Assert.assertEquals(request.getRequestPath(), "/user/create");
			Assert.assertEquals(request.getMethod(), "POST");
			Assert.assertEquals(request.getParameter("userId"), "sixstar");
			Assert.assertEquals(request.getParameter("userName"), "HongKilDong");
			Assert.assertEquals(request.getHost(), "localhost:8080");
			Assert.assertEquals(request.getHeader("Connection"), "keep-alive");
			Assert.assertEquals(request.getHeader("Accept"), "*/*");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}