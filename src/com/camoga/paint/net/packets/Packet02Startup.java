package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

@Deprecated
public class Packet02Startup extends Packet {

	private int width, height, id;

	public Packet02Startup(byte[] data) {
		super(02);
		Serialize s = Serialize.wrap(data,1,data.length-1);
		width = s.getInt();
		height = s.getInt();
		id = s.getInt();
	}

	public Packet02Startup(int width, int height, int id) {
		super(02);
		this.width = width;
		this.height = height;
		this.id = id;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return Serialize.allocate(12).putInt(width).putInt(height).putInt(id).array();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public int getImage() {
		return 0;
	}
}