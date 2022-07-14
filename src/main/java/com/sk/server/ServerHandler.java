package com.sk.server;

import java.io.IOException;

public interface ServerHandler {
	
	public Request readRequst();
	
	public void writeResponse(Response response);
	
	public void close() throws IOException;
	
}
