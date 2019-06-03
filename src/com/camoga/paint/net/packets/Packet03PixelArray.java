package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet03PixelArray extends Packet {

	public static final int packetsize = 254;
	
	private int[] pixels;
	private int num, uuid;
	
	public Packet03PixelArray(byte[] data) {
		super(3);
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		num = s.getShort();
		uuid = s.getInt();
		pixels = s.getInt(s.get());
	}
	
	public Packet03PixelArray(int num, int uuid, int[] pixels) {
		super(3);
		this.num = num;
		this.uuid = uuid;
		this.pixels = pixels;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}
	
	public byte[] getData() {
		return Serialize.allocate(8+pixels.length*4).put(3).putShort(num).putInt(uuid).put(pixels.length).putInt(pixels).array();
	}
	
	public int[] getPixels() {
		return pixels;
	}
	
	public int getNum() {
		return num;
	}
	
	public int getUUID() {
		return uuid;
	}
}
