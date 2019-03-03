package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet08NewImage extends Packet {

	private int width, height;
	
	public int imageid;
	
	public Packet08NewImage(byte[] data) {
		super(8);
		String[] dataArray = readData(data).split(",");
		width = Integer.parseInt(dataArray[0]);
		height = Integer.parseInt(dataArray[1]);
		imageid = Integer.parseInt(dataArray[2]);
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
		return ("08"+width+","+height+","+imageid).getBytes();
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