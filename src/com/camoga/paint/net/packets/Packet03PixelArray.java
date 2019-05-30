package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet03PixelArray extends Packet {

	private int[] pixels;
	private int num, imageid;
	
	//TODO change imageid from array index to unique id
	public Packet03PixelArray(byte[] data) {
		super(3);
		Serialize s = Serialize.wrap(data, 1, data.length-1);
		num = s.get();
		imageid = s.get();
		pixels = s.getInt(64);
	}
	
	public Packet03PixelArray(int num, int id, int[] pixels) {
		super(3);
		this.num = num;
		this.imageid = id;
		this.pixels = pixels;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}
	
	public byte[] getData() {
		return Serialize.allocate(3+pixels.length*4).put(3).put(num).put(imageid).putInt(pixels).array();
	}
	
	public int[] getPixels() {
		return pixels;
	}
	
	public int getNum() {
		return num;
	}
	
	public int getImageId() {
		return imageid;
	}
}
