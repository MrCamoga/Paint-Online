package com.camoga.paint;

import java.net.InetAddress;

public class ClientMP extends Client {
	
	public InetAddress address;
	public int port;
	
	public ClientMP(String username, InetAddress address, int port) {
		super(username);
		this.address = address;
		this.port = port;
	}
}
