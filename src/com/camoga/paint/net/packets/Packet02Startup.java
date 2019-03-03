package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

@Deprecated
public class Packet02Startup extends Packet {

	private int width, height, scale, id;
	
	public Packet02Startup(byte[] data) {
		super(02);
		String[] dataArray = readData(data).split(",");
		System.out.println(readData(data));
		width = Integer.parseInt(dataArray[0]);
		height = Integer.parseInt(dataArray[1]);
		scale = Integer.parseInt(dataArray[2]);
		id = Integer.parseInt(dataArray[3]);
	}
	
	public Packet02Startup(int width, int height, int scale, int id) {
		super(02);
		this.width = width;
		this.height = height;
		this.scale = scale;
		this.id = id;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("02" + width+","+height+","+scale+","+id).getBytes();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getScale() {
		return scale;
	}

	public int getImage() {
		return 0;
	}
}