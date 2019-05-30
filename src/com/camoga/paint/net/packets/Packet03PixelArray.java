package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet03PixelArray extends Packet {

	private int[] pixels;
	private int num, imageId;
	
	//TODO change imageid from array index to unique id
	public Packet03PixelArray(byte[] data) {
		super(3);
		num = data[1] & 0xff;
		imageId = data[2] & 0xff;
		pixels = new int[64];
		ByteBuffer.wrap(data, 3, 4*64).asIntBuffer().get(pixels);
//		byte[] pixelData = Arrays.copyOfRange(data, 3, 64*4+3);
//		pixels = new int[pixelData.length/4];
//		ByteBuffer.wrap(pixelData).asIntBuffer().get(pixels);
	}
	
	public Packet03PixelArray(int num, int id, int[] pixels) {
		super(3);
		this.num = num;
		this.imageId = id;
		this.pixels = pixels;
	}

	public void writeData(ClientSocket client) {
		client.sendData(getData());
	}

	public void writeData(ServerSocket server) {
		server.sendDataToAllClients(getData());
	}
	
	public byte[] getData() {
		ByteBuffer bb = ByteBuffer.allocate(4*pixels.length);
		IntBuffer ib = bb.asIntBuffer();
		ib.put(pixels);
		
		byte[] m = new byte[] {3, (byte) num, (byte) imageId};
		byte[] img = bb.array();
		byte[] data = new byte[m.length + img.length];
		System.arraycopy(m, 0, data, 0, m.length);
		System.arraycopy(img, 0, data, m.length, img.length);
		
		return data;
	}
	
	public int[] getPixels() {
		return pixels;
	}
	
	public int getNum() {
		return num;
	}
	
	public int getImageId() {
		return imageId;
	}
}
