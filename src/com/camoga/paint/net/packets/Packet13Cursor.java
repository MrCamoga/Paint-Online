package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;
import java.util.Arrays;

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
//		String[] dataArray = readData(data).split(",");
//		this.username = dataArray[0];
		this.x = ByteBuffer.wrap(data, 1, 2).getShort();
		this.y = ByteBuffer.wrap(data, 3, 2).getShort();
		this.tool = data[5] & 0xff;
		this.imageid = data[6] & 0xff;
		this.username = new String(Arrays.copyOfRange(data, 7, data.length)).trim();
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
		byte[] user = username.getBytes();
		byte[] xc = ByteBuffer.allocate(2).putShort((short)x).array();
		byte[] yc = ByteBuffer.allocate(2).putShort((short)y).array();
		byte[] data = new byte[1+2+2+1+1+user.length];
		data[0] = 13;
		System.arraycopy(xc, 0, data, 1, 2);
		System.arraycopy(yc, 0, data, 3, 2);
		data[5] = (byte) tool;
		data[6] = (byte) imageid;
		System.arraycopy(user, 0, data, 7, user.length);
		
		return data;
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