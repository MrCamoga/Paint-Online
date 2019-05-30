package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet09Error extends Packet {

	private int type, a;
	
	public Packet09Error(byte[] data) {
		super(9);
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		type = s.getInt();
		a = s.getInt();
	}
	
	/**
	 * 
	 * @param type 0 = different version
	 */
	
	public Packet09Error(int type, int a) {
		super(9);
		this.type = type;
		this.a = a;
	}
	
	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return Serialize.allocate(9).put(9).putInt(type).putInt(a).array();
	}
	
	public int getType() {
		return type;
	}
	
	public int getA() {
		return a;
	}
}