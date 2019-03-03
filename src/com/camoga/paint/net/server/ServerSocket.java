package com.camoga.paint.net.server;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.camoga.paint.ClientMP;
import com.camoga.paint.Utils;
import com.camoga.paint.checkver.Check;
import com.camoga.paint.net.packets.Packet;
import com.camoga.paint.net.packets.Packet.PacketTypes;
import com.camoga.paint.net.packets.Packet00Login;
import com.camoga.paint.net.packets.Packet01Paint;
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

public class ServerSocket extends Thread {
	
	private String version = "1.1.5";
	
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
		
		load();
		
		paint = new PaintServer();
		
		try {
			socket = new DatagramSocket(PORT);		
		} catch(SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		
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
				while(true) {
					commands.exec(sc.nextLine().split(" "));
				}
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
			System.out.println(new String(packet.getData()).trim());
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
//			String message = new String(packet.getData()).trim();
//			System.out.println("[CLIENT " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + "] " + message);
//			if(message.equalsIgnoreCase("ping")) {
//				sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
//			}
		}
	}
	
	public void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.getPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			commands.print("[" + address.getHostAddress() + ":" + port + "] " + ((Packet00Login) packet).getUsername() + " has connected...\n");
			if(Utils.getClientMPIndex(((Packet00Login) packet).getUsername(), clients) == -1) {
				addConnection((Packet00Login)packet, address, port);

				for(int i = 0; i < paint.pixels.size(); i++) {
					sendInitializationData(address, port, i);
				}
				for(int i = 0; i < paint.pixels.size(); i++) {
					sendImage(address, port, i);									
				}
			} else {
				commands.print("User already connected to the server");
			}
			break;
		case PAINT:
			packet = new Packet01Paint(data);
			paint.pencil(((Packet01Paint) packet).getX(), 
					((Packet01Paint) packet).getY(),
					((Packet01Paint) packet).getSize(),
					((Packet01Paint) packet).getColor(),
					((Packet01Paint) packet).getImage());
			packet.writeData(this);
			break;
			//TODO upload image
		case PIXELARRAY:
			break;
		case STARTUP:
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
//			int index = Main.getClientMPIndex(((Packet06Chat) packet).getUsername(), clients);
//			sendData(packet.getData(), clients.get(index).address, clients.get(index).port);
			if(!handleChat((Packet06Chat) packet, address, port)) packet.writeData(this);
			break;
		case FILLBUCKET:
			packet = new Packet07FillBucket(data);
			paint.floodFill(((Packet07FillBucket) packet).getX(), 
					((Packet07FillBucket) packet).getY(), 
					((Packet07FillBucket) packet).getTarget(), 
					((Packet07FillBucket) packet).getColor(),
					((Packet07FillBucket) packet).getImage());
			packet.writeData(this);
			break;
			//DONE image width and height
		case NEWIMAGE:
			packet = new Packet08NewImage(data);
			paint.addImage(((Packet08NewImage) packet).getWidth(), ((Packet08NewImage) packet).getHeight());
			commands.print("new image was created\n");
			createImage((Packet08NewImage) packet, paint.pixels.size()-1);
			break;
		case VERSION:
			packet = new Packet10Version(data);
			Packet10Version version = new Packet10Version(this.version);
			sendData(version.getData(), address, port);
			break;
		case PASSWORD:
			packet = new Packet11Password(data);
			Packet11Password password = new Packet11Password("", ((Packet11Password) packet).getPassword().equals(getPassword()));
			System.out.println("Password " + (password.isCorrect() ? "correct":"incorrect"));
			sendData(password.getData(), address, port);
			break;
		case DELETEIMAGE:
			packet = new Packet12DeleteImage(data);
			String username = getClient(address, port).getUsername();
			for(ClientMP c : admins) {
				if(username.equals(c.getUsername())) {
					paint.removeImage(((Packet12DeleteImage) packet).getId());
					Packet12DeleteImage deleteImage = new Packet12DeleteImage(((Packet12DeleteImage) packet).getId());
					deleteImage.writeData(this);
					commands.print("image " + deleteImage.getId() + " was deleted by " + c.getUsername());
					break;
				}
			}
			break;
		case CURSOR:
			packet = new Packet13Cursor(data);
			packet.writeData(this);
		}
	}
	
	private boolean handleChat(Packet06Chat packet, InetAddress address, int port) {
		if(!packet.getMessage().startsWith("/")) return false;
		String[] msg = packet.getMessage().split(" ");
		switch(msg[0]) {
		case "/msg":
			//FIXME if user sends message to himself it will receive two messages
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
	
	//DONE send image correctly
	private void sendImage(InetAddress address, int port, int imageId) {
		int numPixels = paint.pixels.get(imageId).length;
		int l = 254;
		for(int num = 0; num <= numPixels/l; num++) {
			int[] pack = new int[l];
			if(numPixels - num*l <= l) {
				pack = null;
				pack = new int[numPixels - l*num];
			}
			for(int i = 0; i < pack.length; i++) {
				pack[i] = paint.pixels.get(imageId)[num*l + i];
			}
			Packet03PixelArray pixels = new Packet03PixelArray(num, imageId, pack);
			sendData(pixels.getData(), address, port);
//			for(int i = 0; i < pack.length; i++) {
//				System.out.print(pack[i]+" ");
//			}
//			System.out.println();
		}
		commands.print("Image sent successfully to " + clients.get(Utils.getClientMPIndex(address, port, clients)).getUsername()+ "\n");
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
		ClientMP client = (ClientMP)clients.remove(index);
		if(client != null) {
			commands.print("["+ address.getHostAddress() + ":" + port +"] " + ((Packet05Disconnect) packet).getUsername() + " has disconnected...\n");
		}
		packet.writeData(this);
	}
	
	public void createImage(Packet08NewImage packet, int imageid) {
		packet.imageid = imageid;
		packet.writeData(this);
	}
	
	private void sendInitializationData(InetAddress address, int port, int imageId) {
		Packet08NewImage startData = new Packet08NewImage(paint.size.get(imageId)[0], paint.size.get(imageId)[1], imageId);
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