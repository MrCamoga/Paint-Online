package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet07FillBucket extends Packet {

	private int x, y, target, color, id;

	public Packet07FillBucket(byte[] data) {
		super(07);
		String[] dataArray = readData(data).split(",");
		x = Integer.parseInt(dataArray[0]);
		y = Integer.parseInt(dataArray[1]);
		target = Integer.parseInt(dataArray[2]);
		color = Integer.parseInt(dataArray[3]);
		id = Integer.parseInt(dataArray[4]);
	}

	public Packet07FillBucket(int x, int y, int target, int color, int id) {
		super(07);
		this.x = x;
		this.y = y;
		this.target = target;
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
		return ("07"+x+","+y+","+target+","+color+","+id).getBytes();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getTarget() {
		return target;
	}
	
	public int getColor() {
		return color;
	}

	public int getImage() {
		return id;
	}
}