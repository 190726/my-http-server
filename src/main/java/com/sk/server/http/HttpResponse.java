package com.sk.server.http;

import com.sk.server.Response;

public class HttpResponse implements Response{
	
	private byte[] body;

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

}
