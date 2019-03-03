package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet06Chat extends Packet {

	private String message;
	private String username;
	
	public Packet06Chat(byte[] data) {
		super(06);
		String dataString = readData(data);
		username = dataString.split(",")[0];
		message = dataString.substring(username.length()+1);
	}
	
	public Packet06Chat(String username, String message) {
		super(06);
		this.message = message;
		this.username = username;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("06"+username+","+message).getBytes();
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getUsername() {
		return username;
	}
}
