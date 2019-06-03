package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet13Cursor extends Packet {
	private String username;
	private int x;
	private int y;
	private int tool;
	private int uuid;

	public Packet13Cursor(byte[] data) {
		super(13);
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		this.x = s.getShort();
		this.y = s.getShort();
		this.tool = s.get();
		this.uuid = s.getInt();
		this.username = s.getString(true);
	}

	public Packet13Cursor(String username, int x, int y, int tool, int uuid) {
		super(13);
		this.username = username;
		this.x = x;
		this.y = y;
		this.tool = tool;
		this.uuid = uuid;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}
	
	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return Serialize.allocate(1+2+2+1+4+1+username.length())
				.put(13)
				.putShort(x)
				.putShort(y)
				.put(tool)
				.putInt(uuid)
				.putString(username, true).array();
	}

	public int getTool() {
		return tool;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getUsername() {
		return username;
	}

	public int getUUID() {
		return uuid;
	}
}