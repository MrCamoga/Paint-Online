package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet01Paint extends Packet {

	private int x, y, color, size, id;
	
	public Packet01Paint(byte[] data) {
		super(01);
		String[] dataArray = readData(data).split(",");
		x = Integer.parseInt(dataArray[0]);
		y = Integer.parseInt(dataArray[1]);
		size = Integer.parseInt(dataArray[2]);
		color = Integer.parseInt(dataArray[3]);
		id = Integer.parseInt(dataArray[4]);
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
		return ("01"+x+","+y+","+size+","+color+","+id).getBytes();
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
