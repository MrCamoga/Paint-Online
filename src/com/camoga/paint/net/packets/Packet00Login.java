package com.camoga.paint.net.packets;

import java.util.Arrays;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet00Login extends Packet {

	private String username;
	
	public Packet00Login(byte[] data) {
		super(0);
		username = Serialize.wrap(data, 1, data.length-1).getString(false);
	}
	
	public Packet00Login(String username) {
		super(00);
		this.username = username;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return Serialize.allocate(1+username.length()).put(0).putString(username, false).array();
	}
	
	public String toString() {
		return super.toString() + ": " + getUsername();
	}
	
	public String getUsername() {
		return username;
	}
}
