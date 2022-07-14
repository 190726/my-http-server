package com.sk.server;

import java.net.Socket;

public interface ServerHandlerFactory {
	
	public ServerHandler newServerHandlerInstance(Socket socket);

}
