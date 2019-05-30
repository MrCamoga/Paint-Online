package com.camoga.paint.net.packets;

import java.util.Arrays;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet11Password extends Packet {

	private String password;
	private boolean correct;
	
	public Packet11Password(byte[] data) {
		super(11);
		correct = data[1] == 1;
		password = new String(Arrays.copyOfRange(data, 2, data.length)).trim();
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
		byte[] pass = password.getBytes();
		byte[] data = new byte[1+1+pass.length];
		data[0] = 11;
		data[1] = (byte) (correct ? 1:0);
		System.arraycopy(pass, 0, data, 2, pass.length);
		return data;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean isCorrect() {
		return correct;
	}
}