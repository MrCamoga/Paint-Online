package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet01Paint extends Packet {

	private int x, y, color, size, id;
	
	public Packet01Paint(byte[] data) {
		super(01);
//		String[] dataArray = readData(data).split(",");
		x = data[1] & 0xff;
		y = data[2] & 0xff;
		size = data[3] & 0xff;
		color = ByteBuffer.wrap(data, 4, 4).getInt();
		id = data[8] & 0xff;
	}
	
	public Packet01Paint(int x, int y, int size, int color, int id) {
		super(01);
		this.x = x;
		this.y = y;
		this.color = color;
		this.size = size;
		this.id = id;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		byte[] data = new byte[1+1+1+1+4+1];
		data[0] = 1;
		data[1] = (byte) x;
		data[2] = (byte) y;
		data[3] = (byte) size;
		System.arraycopy(ByteBuffer.allocate(4).putInt(color).array(), 0, data, 4, 4);
		data[8] = (byte) id;
		return data;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getColor() {
		return color;
	}

	public int getSize() {
		return size;
	}

	public int getImage() {
		return id;
	}
}
