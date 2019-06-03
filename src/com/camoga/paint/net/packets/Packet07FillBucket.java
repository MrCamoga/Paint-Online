package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet07FillBucket extends Packet {

	//TODO fusionar con paint (tool)
	private int x, y, color, uuid;

	public Packet07FillBucket(byte[] data) {
		super(07);
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		x = s.get();
		y = s.get();
		color = s.getInt();
		uuid = s.getInt();
	}

	public Packet07FillBucket(int x, int y, int color, int uuid) {
		super(07);
		this.x = x;
		this.y = y;
		this.color = color;
		this.uuid = uuid;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return Serialize.allocate(11).put(7).put(x).put(y).putInt(color).putInt(uuid).array();
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

	public int getUUID() {
		return uuid;
	}
}