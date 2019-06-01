package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet11Password extends Packet {

	private String password;
	private boolean correct;
	
	public Packet11Password(byte[] data) {
		super(11);
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		correct = s.getBoolean();
		password = s.getString(true);
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
		return Serialize.allocate(3+password.length()).put(11).putBoolean(correct).putString(password, true).array();
	}
	
	public String toString() {
		return super.toString() + ": " + isCorrect();
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean isCorrect() {
		return correct;
	}
}