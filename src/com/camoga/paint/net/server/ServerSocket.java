package com.camoga.paint.net.server;

import com.camoga.paint.ClientMP;
import com.camoga.paint.Image;
import com.camoga.paint.Utils;
import com.camoga.paint.checkver.Check;
import com.camoga.paint.net.packets.*;
import com.camoga.paint.net.packets.Packet.PacketTypes;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerSocket extends Thread {
	
	private String version = "1.1.7";
	
	private static final int PORT = 7357;
	
	private DatagramSocket socket;
	private String password = "null";

	public PaintServer paint;
	private ArrayList<ClientMP> clients = new ArrayList<>();
	private ArrayList<ClientMP> admins = new ArrayList<>();
	public JTextArea console;
	private Console commands;
	
	public static void main(String[] args) {
		new ServerSocket().start();
	}
	
	public ServerSocket() {
		commands = new Console(this);
		if(!GraphicsEnvironment.isHeadless()) {
			JFrame frame = new JFrame();
			frame.setSize(600, 700);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			console = new JTextArea();
			console.setEditable(false);
			
			final JTextField intro = new JTextField();
			
			frame.add(console);
			frame.add(intro, BorderLayout.SOUTH);
			
			intro.addActionListener(commands);
			frame.setVisible(true);
		}
		
//		load();
		
		paint = new PaintServer();
		
		try {
			socket = new DatagramSocket(PORT);		
		} catch(SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					check();
					Thread.sleep(43200000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "Check for updates").start();
		
		new Thread(new Runnable() {
			public void run() {
				Scanner sc = new Scanner(System.in);
				while(!Thread.interrupted()) {
					commands.exec(sc.nextLine().split(" "));
				}
				sc.close();
			}
		}, "Console input").start();
		
		while(true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);				
			} catch(IOException e) {
				e.printStackTrace();
			}
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}
	
	public void parsePacket(byte[] data, InetAddress address, int port) {
		PacketTypes type = Packet.getPacket(data[0]);
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			System.out.println(data[0]);
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			commands.print("[" + address.getHostAddress() + ":" + port + "] " + ((Packet00Login) packet).getUsername() + " has connected...\n");
			if(Utils.getClientMPIndex(((Packet00Login) packet).getUsername(), clients) == -1) {
				addConnection((Packet00Login)packet, address, port);

				for(Image img : paint.images) {
					sendInitializationData(address, port, img);
				}
				for(Image img : paint.images) {
					sendImage(address, port, img);									
				}
			} else {
				commands.print("User already connected to the server");
			}
			break;
		case PAINT:
			packet = new Packet01Paint(data);
			if(paint.getImage(((Packet01Paint) packet).getUUID()) == null) break;
			paint.pencil(((Packet01Paint) packet).getX(), 
					((Packet01Paint) packet).getY(),
					((Packet01Paint) packet).getSize(),
					((Packet01Paint) packet).getColor(),
					((Packet01Paint) packet).getUUID());
			packet.writeData(this);
			break;
			//DONE upload image
		case PIXELARRAY:
			packet = new Packet03PixelArray(data);
			packet.writeData(this);
			int num = ((Packet03PixelArray) packet).getNum();
			int uuid = ((Packet03PixelArray) packet).getUUID();
			int[] pixels = ((Packet03PixelArray) packet).getPixels();
			paint.handlePixelArray(pixels, num, uuid);
			break;
		case SELECTCOLOR:
			packet = new Packet04SelectColor(data);
			packet.writeData(this);
			break;
		case DISCONNECT:
			packet = new Packet05Disconnect(data);
			//TODO remove/add connection by ipaddress and port
			removeConnection((Packet05Disconnect) packet, address, port);
			break;
		case CHAT:
			packet = new Packet06Chat(data);
			commands.print("[" + ((Packet06Chat) packet).getUsername() +"]: " + ((Packet06Chat) packet).getMessage()+"\n");
			if(!handleChat((Packet06Chat) packet, address, port)) packet.writeData(this);
			break;
		case FILLBUCKET:
			packet = new Packet07FillBucket(data);
			int x = ((Packet07FillBucket) packet).getX();
			int y = ((Packet07FillBucket) packet).getY();
			uuid = ((Packet07FillBucket) packet).getUUID();
			paint.floodFill(x, y, paint.getImage(uuid).getPixel(x, y), ((Packet07FillBucket) packet).getColor(), uuid);
			packet.writeData(this);
			break;
		case NEWIMAGE:
			packet = new Packet08NewImage(data);
			paint.addImage(((Packet08NewImage) packet).getWidth(), ((Packet08NewImage) packet).getHeight(), ((Packet08NewImage) packet).getUUID());
			commands.print("new image was created\n");
			packet.writeData(this);
			break;
		case VERSION:
			packet = new Packet10Version(data);
			Packet10Version version = new Packet10Version(this.version);
			sendData(version.getData(), address, port);
			break;
		case PASSWORD:
			packet = new Packet11Password(data);
			boolean correct = ((Packet11Password) packet).getPassword().equals(getPassword());
			System.out.println("Password " + (correct ? "correct":"incorrect"));
			packet = new Packet11Password("", correct);
			sendData(packet.getData(), address, port);
			break;
		case DELETEIMAGE:
			packet = new Packet12DeleteImage(data);
			String username = getClient(address, port).getUsername();
			//DONE isAdmin
			if(isAdmin(username)) {
				paint.removeImage(((Packet12DeleteImage) packet).getUUID());
				Packet12DeleteImage deleteImage = new Packet12DeleteImage(((Packet12DeleteImage) packet).getUUID());
				deleteImage.writeData(this);
				commands.print("image " + deleteImage.getUUID() + " was deleted by " + username);
			}
			break;
		case CURSOR:
			packet = new Packet13Cursor(data);
			packet.writeData(this);
		}
	}
	
	public boolean isAdmin(String username) {
		for(ClientMP c : admins) {
			if(username.equals(c.getUsername())) return true;
		}
		return false;
	}
	
	private boolean handleChat(Packet06Chat packet, InetAddress address, int port) {
		if(!packet.getMessage().startsWith("/")) return false;
		String[] msg = packet.getMessage().split(" ");
		switch(msg[0]) {
		case "/msg":
			int clientIndex = Utils.getClientMPIndex(msg[1],clients);
			if(clientIndex != -1) {
				Packet06Chat sendMessage = new Packet06Chat(packet.getUsername(), "PM: " + packet.getMessage().substring(6+msg[1].length()));
				sendData(sendMessage.getData(), clients.get(clientIndex).address, clients.get(clientIndex).port);
				sendMessage = new Packet06Chat(packet.getUsername(), "to " + msg[1] + ": " + packet.getMessage().substring(6+msg[1].length()));
				sendData(sendMessage.getData(), address, port);
			} else {
				Packet06Chat sendMessage = new Packet06Chat(packet.getUsername(), "user " + msg[1] + " was not found");
				sendData(sendMessage.getData(), address, port);
			}
			return true;
		}
		return false;
	}

	private ClientMP getClient(InetAddress address, int port) {
		for(ClientMP c : clients) {
			if(c.address.getHostAddress().equals(address.getHostAddress())&&c.port == port) return c;
		}
		return null;
	}
	
	private void sendImage(InetAddress address, int port, Image image) {
		for(Packet03PixelArray packet : Utils.sendImage(image)) {
			sendData(packet.getData(), address, port);
		}
	}
//

	private void addConnection(Packet00Login packet, InetAddress address, int port) {
		ClientMP client = new ClientMP(packet.getUsername(), address, port);
		packet.writeData(this);
		clients.add(client);
		for(ClientMP c:clients) {
			Packet00Login loginPacket = new Packet00Login(c.getUsername());
			sendData(loginPacket.getData(), address, port);
		}
	}
	
	private void removeConnection(Packet05Disconnect packet, InetAddress address, int port) {
		int index = Utils.getClientMPIndex(packet.getUsername(), clients);
		if(index == -1) return;
		ClientMP client = clients.remove(index);
		if(client != null) {
			commands.print("["+ address.getHostAddress() + ":" + port +"] " + ((Packet05Disconnect) packet).getUsername() + " has disconnected...\n");
		}
		packet.writeData(this);
	}
	
	private void sendInitializationData(InetAddress address, int port, Image image) {
		Packet08NewImage startData = new Packet08NewImage(image.width, image.height, image.UUID);
		sendData(startData.getData(), address, port);
	}
	
	public String getPassword() {
		return password;
	}
	
	public ArrayList<ClientMP> getClients() {
		return clients;
	}
	
	public ArrayList<ClientMP> getAdmins() {
		return admins;
	}
	
	public void sendData(byte[] data, InetAddress address, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendDataToAllClients(byte[] data) {
		for(ClientMP c : clients) {
			sendData(data, c.address, c.port);
		}
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean check() {
		try {
			if(!Check.version("paintserver").equals(version)) {
				commands.print("There is a new version, type \"/update help\" to see how to update the program");
				return true;
			}
		} catch(Exception e) {
			System.out.println("Server couldn't be found");
		}
		return false;
	}
}