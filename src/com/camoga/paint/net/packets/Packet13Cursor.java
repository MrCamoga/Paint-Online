package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet13Cursor extends Packet {
	private String username;
	private int x;
	private int y;
	private int tool;
	private int imageid;

	public Packet13Cursor(byte[] data) {
		super(13);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x = Integer.parseInt(dataArray[1]);
		this.y = Integer.parseInt(dataArray[2]);
		this.tool = Integer.parseInt(dataArray[3]);
		this.imageid = Integer.parseInt(dataArray[4]);
	}

	public Packet13Cursor(String username, int x, int y, int tool, int imageid) {
		super(13);
		this.username = username;
		this.x = x;
		this.y = y;
		this.tool = tool;
		this.imageid = imageid;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}
	
	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("13" + this.username + "," + this.x + "," + this.y + "," + this.tool + "," + this.imageid).getBytes();
	}

	public int getTool() {
		return this.tool;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public String getUsername() {
		return this.username;
	}

	public int getImageId() {
		return this.imageid;
	}
}