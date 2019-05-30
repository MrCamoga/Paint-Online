package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet12DeleteImage extends Packet {
	
	private int imageid;

	public Packet12DeleteImage(byte[] data) {
		super(12);
		this.imageid = data[1] & 0xff;
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
		byte[] data = {12, (byte)imageid};
		return data;
	}

	public int getId() {
		return this.imageid;
	}
}
