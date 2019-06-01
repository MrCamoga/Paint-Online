package com.camoga.paint.net.packets;

import java.util.Arrays;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet10Version extends Packet {

	private String version;
	
	public Packet10Version(byte[] data) {
		super(10);
		version = Serialize.wrap(data, 1, data.length-1).getString(true);
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
		return Serialize.allocate(1+1+version.length()).put(10).putString(version, true).array();
	}
	
	public String toString() {
		return super.toString() + ": " + getVersion();
	}
	
	public String getVersion() {
		return version;
	}
}