package com.camoga.paint.net.client;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.camoga.paint.Client;
import com.camoga.paint.ClientMP;
import com.camoga.paint.PaintMain;
import com.camoga.paint.ServerClient;
import com.camoga.paint.Utils;
import com.camoga.paint.gui.Window;
import com.camoga.paint.net.packets.Packet;
import com.camoga.paint.net.packets.Packet.PacketTypes;
import com.camoga.paint.net.packets.Packet00Login;
import com.camoga.paint.net.packets.Packet01Paint;
import com.camoga.paint.net.packets.Packet02Startup;
import com.camoga.paint.net.packets.Packet03PixelArray;
import com.camoga.paint.net.packets.Packet04SelectColor;
import com.camoga.paint.net.packets.Packet05Disconnect;
import com.camoga.paint.net.packets.Packet06Chat;
import com.camoga.paint.net.packets.Packet07FillBucket;
import com.camoga.paint.net.packets.Packet08NewImage;
import com.camoga.paint.net.packets.Packet10Version;
import com.camoga.paint.net.packets.Packet11Password;
import com.camoga.paint.net.packets.Packet12DeleteImage;
import com.camoga.paint.net.packets.Packet13Cursor;

public class ClientSocket extends Thread {
	
	private DatagramSocket socket;
	private InetAddress address;
	public ServerClient paint;
	public Client client;
	public static final int PORT = 7357;
	
	public ClientSocket(InetAddress address, Client client) {
		super("ClientSocket");
		this.client = client;
		try {
			socket = new DatagramSocket();
			this.address = address;
			
			//sendData("ping".getBytes());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			byte[] data = new byte[1024];
			
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (SocketTimeoutException e) {
				JOptionPane.showMessageDialog(this.paint, "Connection refused", "A connection error has ocurred", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			parsePacket(packet.getData(), address, PORT);	
		}
	}
	
	public void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.getPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			System.out.println(new String(data).trim());
			//throw new RuntimeException("You are not allowed to enter the server or an error has ocurred, if you think that this is an error please contact with mrcamoga@gmail.com");
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet00Login) packet).getUsername() + " has joined...");
			handleLogin((Packet00Login) packet, address, port);
			break;
		case PAINT:
			packet = new Packet01Paint(data);
			handlePaint((Packet01Paint) packet);
			break;
		case STARTUP: //DELETE?
//			packet = new Packet02Startup(data);
//			main.paint.init(((Packet02Startup) packet).getWidth(), ((Packet02Startup) packet).getHeight(), ((Packet02Startup) packet).getScale(), ((Packet02Startup) packet).getImage());
//			main.paint.start();
			break;
		case PIXELARRAY:
			packet = new Packet03PixelArray(data);
			handlePixelArray((Packet03PixelArray) packet);
			break;
		case SELECTCOLOR:
			packet = new Packet04SelectColor(data);
			paint.getCurrentPP().addRecentColor(((Packet04SelectColor) packet).getColor());
			break;
		case DISCONNECT:
			packet = new Packet05Disconnect(data);
			handleDisconnect((Packet05Disconnect) packet);
			System.out.println("["+ address.getHostAddress() + ":" + port +"] " + ((Packet05Disconnect) packet).getUsername() + " has left...");
			break;
		case CHAT:
			packet = new Packet06Chat(data);
			handleChat((Packet06Chat) packet);
			break;
		case FILLBUCKET:
			packet = new Packet07FillBucket(data);
			handleFillBucket(((Packet07FillBucket) packet));
			break;
		case NEWIMAGE:
			packet = new Packet08NewImage(data);
			int width = ((Packet08NewImage) packet).getWidth();
			int height = ((Packet08NewImage) packet).getHeight();
			paint.init(width, height, ((Packet08NewImage) packet).getId());
			paint.start();
			break;
		case ERROR:
			//TODO ?
			
			break;
		case VERSION:
			packet = new Packet10Version(data);
			if(!PaintMain.main.version.equals(((Packet10Version) packet).getVersion())) {
				int i = JOptionPane.showConfirmDialog(paint, "You have a version discordance with the server \n Your version: " + PaintMain.main.version + "\n Server version" + ((Packet10Version) packet).getVersion() + ".\n Do you want to continue running the client? This may cause serious issues!", "ERROR! Version discordance", JOptionPane.YES_NO_CANCEL_OPTION);
				if(i == JOptionPane.CANCEL_OPTION || i == JOptionPane.NO_OPTION) PaintMain.main.disconnect(paint);
			}
			break;
		case PASSWORD:
			packet = new Packet11Password(data);
			if(((Packet11Password) packet).isCorrect()) {
				Packet00Login loginPacket = new Packet00Login(client.getUsername());;
				loginPacket.writeData(this);
				return;
			} else {
				//DONE add warning: wrong password
				JOptionPane.showMessageDialog(Window.window, new JLabel("The password is wrong"), "Wrong password!", JOptionPane.ERROR_MESSAGE);
				PaintMain.main.disconnect(paint);
			}
			break;
		case DELETEIMAGE:
			packet = new Packet12DeleteImage(data);
			int i = JOptionPane.showConfirmDialog(paint, "An user has deleted an image, Would you like to save it? If you don't, you won't be able to recover it", "Image deleted", JOptionPane.ERROR_MESSAGE);
			if (i == 0) {
				Utils.saveImage(((Packet12DeleteImage)packet).getId());
			}
			paint.removeImage(((Packet12DeleteImage)packet).getId());
			
			break;
		case CURSOR:
			//DONE add circle of drawing area
			packet = new Packet13Cursor(data);
			String username = ((Packet13Cursor) packet).getUsername();
			int x = ((Packet13Cursor) packet).getX();
			int y = ((Packet13Cursor) packet).getY();
			int tool = ((Packet13Cursor) packet).getTool();
			int imageid = ((Packet13Cursor) packet).getImageId();
			paint.updateCursor(username, x, y, tool, imageid);
			break;
		}
	}
	
	public void handleLogin(Packet00Login packet, InetAddress address, int port) {
		ClientMP client = new ClientMP(packet.getUsername(), address, port);
		paint.addClient(client);
	}
	
	public void handleDisconnect(Packet05Disconnect packet) {
		paint.removeClient(packet.getUsername());
	}
	
	//TODO change to int serialization
	public void handlePixelArray(Packet03PixelArray packet) {
		paint.imagepacket(packet.getPixels(), packet.getNum(), packet.getImageId());
	}
	
	public void handlePaint(Packet01Paint packet) {
		paint.pencil(packet.getX(), packet.getY(), packet.getSize(), packet.getColor(), packet.getImage());
	}
	
	public void handleFillBucket(Packet07FillBucket packet) {
		paint.floodFill(packet.getX(), packet.getY(), packet.getTarget(), packet.getColor(), packet.getImage());
	}
	
	public void handleChat(Packet06Chat packet) {
		paint.chat.addText("[" + packet.getUsername() +"]: " + packet.getMessage());
		if(!packet.getUsername().equals(client.getUsername())) Toolkit.getDefaultToolkit().beep();
	}
	
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, address, PORT);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InetAddress getAddress() {
		return address;
	}
}