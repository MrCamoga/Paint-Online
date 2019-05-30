package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet01Paint extends Packet {

	private int x, y, color, size, id;
	
	public Packet01Paint(byte[] data) {
		super(01);
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		x = s.get();
		y = s.get();
		size = s.get();
		color = s.getInt();
		id = s.get();
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
		return Serialize.allocate(9).put(1).put(x).put(y).put(size).putInt(color).put(id).array();
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
