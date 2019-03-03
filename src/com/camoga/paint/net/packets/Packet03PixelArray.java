package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public class Packet03PixelArray extends Packet {

	private int[] pixels;
	private int num, id;
	
	
	public Packet03PixelArray(byte[] data) {
		super(03);
		//FIXME color = 0b00101100 corresponds to ","
		String[] dataArray = readData(data).split(",");
		num = Integer.parseInt(dataArray[0]);
		id = Integer.parseInt(dataArray[1]);
		byte[] pixelData = dataArray[2].getBytes();
		pixels = new int[pixelData.length/4];
		ByteBuffer.wrap(pixelData).asIntBuffer().get(pixels);
	}
	
	public Packet03PixelArray(int num, int id, int[] pixels) {
		super(00);
		this.num = num;
		this.id = id;
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
		for(int i:pixels) ib.put(i);
		
		String array = new String(bb.array());
		return ("03"+num+","+id+","+array+",").getBytes();
	}
	
	public int[] getPixels() {
		return pixels;
	}
	
	public int getNum() {
		return num;
	}
	
	public int getImageId() {
		return id;
	}
}
