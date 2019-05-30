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
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		this.x = s.getShort();
		this.y = s.getShort();
		this.tool = s.get();
		this.imageid = s.get();
		this.username = s.getString(true);
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
		return Serialize.allocate(1+2+2+1+1+1+username.length())
				.put((byte)13)
				.putShort((short)x)
				.putShort((short)y)
				.put((byte)tool)
				.put((byte)imageid)
				.putString(username, true).array();
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