package com.camoga.paint.net.packets;

import java.util.Arrays;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet10Version extends Packet {

	private String version;
	
	public Packet10Version(byte[] data) {
		super(10);
		version = new String(Arrays.copyOfRange(data, 1, data.length)).trim();
	}
	
	public Packet10Version(String version) {
		super(10);
		this.version = version;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		byte[] v = version.getBytes();
		byte[] data = new byte[1+v.length];
		data[0] = 10;
		System.arraycopy(v, 0, data, 1, v.length);
		
		return data;
	}
	
	public String getVersion() {
		return version;
	}
}