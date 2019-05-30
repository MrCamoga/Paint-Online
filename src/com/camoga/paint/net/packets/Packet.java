package com.camoga.paint.net.packets;

import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.server.ServerSocket;

public abstract class Packet {
	
	public static enum PacketTypes {
		INVALID(-1), LOGIN(00), PAINT(01),
		@Deprecated STARTUP(02), PIXELARRAY(03), SELECTCOLOR(04),
		DISCONNECT(05), CHAT(06), FILLBUCKET(07),
		NEWIMAGE(8), ERROR(9), VERSION(10),
		PASSWORD(11), DELETEIMAGE(12), CURSOR(13);
		
		private int packetId;
		
		PacketTypes(int id) {
			this.packetId = id;
		}
		
		public int getId() {
			return packetId;
		}
	}
	
	@SuppressWarnings("unused")
	private byte packetId;
	
	public Packet(int id) {
		this.packetId = (byte)id;
	}
	
	public abstract void writeData(ClientSocket client);
	public abstract void writeData(ServerSocket server);
	
	public abstract byte[] getData();
	
	public String reaData(byte[] data) {
		String message = new String(data).trim();
		return message.substring(2);
	}
	
	public static PacketTypes getPacket(String id) {
		try {
			return getPacket(Integer.parseInt(id));
		} catch(NumberFormatException e) {
			return PacketTypes.INVALID;
		}
	}
	
	public static PacketTypes getPacket(int id) {
		for(PacketTypes p: PacketTypes.values()) {
			if(p.getId() == id) return p;
		}
		return PacketTypes.INVALID;
	}
}