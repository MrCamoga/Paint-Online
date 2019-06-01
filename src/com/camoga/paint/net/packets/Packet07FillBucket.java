package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet07FillBucket extends Packet {

	//TODO fusionar con paint (tool)
	private int x, y, color, id;

	public Packet07FillBucket(byte[] data) {
		super(07);
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		x = s.get();
		y = s.get();
		color = s.getInt();
		id = s.get();
	}

	public Packet07FillBucket(int x, int y, int color, int id) {
		super(07);
		this.x = x;
		this.y = y;
		this.color = color;
		this.id = id;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return Serialize.allocate(8).put(7).put(x).put(y).putInt(color).put(id).array();
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

	public int getImage() {
		return id;
	}
}