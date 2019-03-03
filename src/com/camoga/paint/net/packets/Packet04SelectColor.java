package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet04SelectColor extends Packet {

	private int color;
	
	public Packet04SelectColor(byte[] data) {
		super(04);
		color = Integer.parseInt(readData(data));
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
		return ("04"+color).getBytes();
	}
	
	public int getColor() {
		return color;
	}
}
