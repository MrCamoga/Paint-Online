package com.camoga.paint.net.packets;

import java.util.Arrays;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet00Login extends Packet {

	private String username;
	
	public Packet00Login(byte[] data) {
		super(0);
//		String[] dataArray = readData(data).split(",");
		username = new String(Arrays.copyOfRange(data, 1, data.length)).trim();
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
		byte[] user = username.getBytes();
		byte[] data = new byte[1+user.length];
		data[0] = 0;
		System.arraycopy(user, 0, data, 1, user.length);
		return data;
	}
	
	public String getUsername() {
		return username;
	}
}
