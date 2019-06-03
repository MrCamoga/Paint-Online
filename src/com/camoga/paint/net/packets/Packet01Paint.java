package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet01Paint extends Packet {

	private int x, y, color, size, uuid;
	
	public Packet01Paint(byte[] data) {
		super(01);
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		x = s.get();
		y = s.get();
		size = s.get();
		color = s.getInt();
		uuid = s.getInt();
	}
	
	public Packet01Paint(int x, int y, int size, int color, int uuid) {
		super(01);
		this.x = x;
		this.y = y;
		this.color = color;
		this.size = size;
		this.uuid = uuid;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return Serialize.allocate(12).put(1).put(x).put(y).put(size).putInt(color).putInt(uuid).array();
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

	public int getUUID() {
		return uuid;
	}
}
