package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet10Version extends Packet {

	private String version;
	
	public Packet10Version(byte[] data) {
		super(10);
		version = readData(data);
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
		return ("10"+version).getBytes();
	}
	
	public String getVersion() {
		return version;
	}
}