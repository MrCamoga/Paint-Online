package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet09Error extends Packet {

	private int type, a;
	
	public Packet09Error(byte[] data) {
		super(9);
		String[] dataArray = readData(data).split(",");
		type = Integer.parseInt(dataArray[0]);
		a = Integer.parseInt(dataArray[1]);
	}
	
	/**
	 * 
	 * @param type 0 = version discordance
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
		return null;
	}
	
	public int getType() {
		return type;
	}
	
	public int getA() {
		return a;
	}
}