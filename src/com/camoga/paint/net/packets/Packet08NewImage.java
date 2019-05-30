package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet08NewImage extends Packet {

	private int width, height;
	
	public int imageid;
	
	public Packet08NewImage(byte[] data) {
		super(8);
		width = data[1] & 0xff;
		height = data[2] & 0xff;
		imageid = data[3] & 0xff;
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
		byte[] data = new byte[1+1+1+1];
		data[0] = 8;
		data[1] = (byte) width;
		data[2] = (byte) height;
		data[3] = (byte) imageid;
		
		return data;
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