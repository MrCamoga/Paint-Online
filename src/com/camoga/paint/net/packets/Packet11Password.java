package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet11Password extends Packet {

	private String password;
	private boolean correct;
	
	public Packet11Password(byte[] data) {
		super(11);
		String[] dataArray = readData(data).split(",");
		password = dataArray[0];
		correct = Boolean.parseBoolean(dataArray[1]);
	}
	
	public Packet11Password(String password, boolean correct) {
		super(11);
		this.password = password;
		this.correct = correct;
	}
	
	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("11" + password+","+correct).getBytes();
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean isCorrect() {
		return correct;
	}
}