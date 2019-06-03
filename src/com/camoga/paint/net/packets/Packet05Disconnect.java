package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet05Disconnect extends Packet {

	private String username;
	
	public Packet05Disconnect(byte[] data) {
		super(05);
		username = Serialize.wrap(data, 1, data.length-1).getString(false);
	}
	
	public Packet05Disconnect(String username) {
		super(05);
		this.username = username;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return Serialize.allocate(1+username.length()).put(5).putString(username, false).array();
	}
	
	public String getUsername() {
		return username;
	}
}
