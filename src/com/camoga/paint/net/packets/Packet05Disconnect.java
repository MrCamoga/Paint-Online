package com.camoga.paint.net.packets;

import java.util.Arrays;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet05Disconnect extends Packet {

	private String username;
	
	public Packet05Disconnect(byte[] data) {
		super(05);
//		String[] dataArray = readData(data).split(",");
		username = new String(Arrays.copyOfRange(data, 1, data.length)).trim();
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
		byte[] user = username.getBytes();
		byte[] data = new byte[1+user.length];
		data[0] = 5;
		System.arraycopy(user, 0, data, 1, user.length);
		return data;
	}
	
	public String getUsername() {
		return username;
	}
}
