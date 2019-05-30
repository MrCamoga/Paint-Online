package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet12DeleteImage extends Packet {
	
	private int imageid;

	public Packet12DeleteImage(byte[] data) {
		super(12);
		this.imageid = Serialize.wrap(data, 1, data.length-1).get();
	}

	public Packet12DeleteImage(int imageid) {
		super(12);
		this.imageid = imageid;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return new byte[]{12, (byte)imageid};
	}

	public int getId() {
		return this.imageid;
	}
}
