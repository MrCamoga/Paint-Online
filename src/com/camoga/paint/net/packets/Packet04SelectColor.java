package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet04SelectColor extends Packet {

	private int color;
	
	public Packet04SelectColor(byte[] data) {
		super(04);
		color = Serialize.wrap(data, 1, data.length-1).getInt();
	}
	
	public Packet04SelectColor(int color) {
		super(04);
		this.color = color;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return Serialize.allocate(5).put(4).putInt(color).array();
	}
	
	public int getColor() {
		return color;
	}
}
