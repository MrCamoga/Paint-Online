package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet04SelectColor extends Packet {

	private int color;
	
	public Packet04SelectColor(byte[] data) {
		super(04);
		color = ByteBuffer.wrap(data, 1, 4).getInt();
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
		byte[] data = new byte[5];
		data[0] = 4;
		System.arraycopy(ByteBuffer.allocate(4).putInt(color).array(), 0, data, 1, 4);
		return data;
	}
	
	public int getColor() {
		return color;
	}
}
