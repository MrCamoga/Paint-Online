package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet12DeleteImage extends Packet {
	
	private int uuid;

	public Packet12DeleteImage(byte[] data) {
		super(12);
		this.uuid = Serialize.wrap(data, 1, data.length-1).getInt();
	}

	public Packet12DeleteImage(int uuid) {
		super(12);
		this.uuid = uuid;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return Serialize.allocate(5).put(12).putInt(uuid).array();
	}

	public int getUUID() {
		return uuid;
	}
}
