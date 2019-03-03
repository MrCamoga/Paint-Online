package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet12DeleteImage extends Packet {
	private int imageid;

	public Packet12DeleteImage(byte[] data) {
		super(12);
		this.imageid = Integer.parseInt(readData(data));
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
		return ("12" + this.imageid).getBytes();
	}

	public int getId() {
		return this.imageid;
	}
}
