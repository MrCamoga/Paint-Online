package com.camoga.paint.net.packets;

import java.util.Arrays;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet06Chat extends Packet {

	private String message;
	private String username;
	
	public Packet06Chat(byte[] data) {
		super(06);
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		
		username = s.getString(true);
		message = s.getString(false);
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
		return Serialize.allocate(1+1+username.length()+message.length()).put(6).putString(username, true).putString(message, false).array();
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getUsername() {
		return username;
	}
}
