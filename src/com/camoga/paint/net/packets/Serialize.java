package com.camoga.paint.net.packets;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Serialize {
	
	private ByteBuffer bb;
	
	public Serialize() {
	
	}
	
	public static Serialize allocate(int capacity) {
		Serialize s = new Serialize();
		s.bb = ByteBuffer.allocate(capacity);
		return s;
	}

//	public static Serialize allocate(Object ...objects ) {
//		Serialize s = new Serialize();
//		int size = 0;
//		for(Object o : objects) {
//			if(o instanceof Integer) size += 4;
//			else if(o instanceof Byte) size += 1;
//		}
//		return s;
//	}
	
	public static Serialize wrap(byte[] data) {
		return wrap(data, 0 , data.length);
	}
	
	public static Serialize wrap(byte[] data, int offset, int length) {
		Serialize s = new Serialize();
		s.bb = ByteBuffer.wrap(data, offset, length);
		return s;
	}
	
	public Serialize put(int data) {
		bb.put((byte)data);
		return this;
	}
	
	public Serialize put(byte[] data) {
		bb.put(data);
		return this;
	}
	
	/**
	 * Store boolean as 1 byte
	 * @param data
	 * @return
	 */
	public Serialize putBoolean(boolean data) {
		return put((byte)(data ? 1:0));
	}
	
	/**
	 * Store 8 boolean per byte little-endian
	 * @param data
	 * @return
	 */
	public Serialize putBoolean(boolean[] data) {
		byte[] b = new byte[(int)Math.ceil(data.length/8.0)];
		
		for(int i = 0; i < data.length; i++) {
			b[i/8] |= data[i] ? (1 << (i%8)):0;
		}
		return put(b);
	}
	
	public Serialize putShort(int data) {
		bb.putShort((short)data);
		return this;
	}
	
	public Serialize putInt(int data) {
		bb.putInt(data);
		return this;
	}
	
	public Serialize putInt(int[] data) {
		bb.asIntBuffer().put(data);
		return this;
	}
	
	public Serialize putString(String str, boolean size) {
		if(size) put((byte)str.length());
		put(str.getBytes());
		return this;
	}
	
	public int get() {
		return bb.get() & 0xff;
	}
	
	public byte[] get(int size) {
		byte[] dst = new byte[size];
		bb.get(dst);
		return dst;
	}
	
	public boolean getBoolean() {
		return bb.get() == 1;
	}
	
	public boolean[] getBoolean(int size) {
		boolean[] data = new boolean[size];
		byte[] b = get((int)Math.ceil(size/8.0));
		
		for(int i = 0; i < data.length; i++) {
			int e = 1<<(i%8);
			data[i] = (b[i/8] & e) == e;
		}
		
		return data;
	}
	
	public int getShort() {
		return bb.getShort() & 0xffff;
	}
	
	public int getInt() {
		return bb.getInt();
	}
	
	public int[] getInt(int size) {
		int[] data = new int[size];
		bb.asIntBuffer().get(data);
		return data;
	}
	
	public String getString(boolean size) {
		if(size) {
			int length  = bb.get() & 0xff;
			byte[] data = new byte[length];
			bb.get(data);
			return new String(data);
		} else {
			byte[] data = new byte[bb.remaining()];
			bb.get(data);
			return new String(data).trim();
		}
	}
	
	public byte[] array() {
		return bb.array();
	}
}
