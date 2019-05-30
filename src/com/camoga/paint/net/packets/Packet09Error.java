package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet09Error extends Packet {

	private int type, a;
	
	public Packet09Error(byte[] data) {
		super(9);
//		String[] dataArray = readData(data).split(",");
		int[] i = new int[2];
		ByteBuffer.wrap(data, 1, 8).asIntBuffer().get(i);
		type = i[0];
		a = i[1];
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

	//TODO getData
	public byte[] getData() {
		byte[] data = new byte[9];
		data[0] = 9;
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putInt(type);
		bb.putInt(a);
		System.arraycopy(bb.array(), 0, data, 1, 8);
		return data;
	}
	
	public int getType() {
		return type;
	}
	
	public int getA() {
		return a;
	}
}