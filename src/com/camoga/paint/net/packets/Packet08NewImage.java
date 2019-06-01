package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet08NewImage extends Packet {

	private int width, height;
	
	public int imageid;
	
	public Packet08NewImage(byte[] data) {
		super(8);
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		width = s.getShort();
		height = s.getShort();
		imageid = s.get();
	}
	
	public Packet08NewImage(int width, int height, int id) {
		super(8);
		this.width = width;
		this.height = height;
		this.imageid = id;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return Serialize.allocate(6).put(8).putShort(width).putShort(height).put(imageid).array();
	}
	
	public String toString() {
		return super.toString() + ": " + getWidth() + "x" + getHeight();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getId() {
		return imageid;
	}
}