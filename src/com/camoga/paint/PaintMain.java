package com.camoga.paint;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Random;

import com.camoga.paint.gui.Window;
import com.camoga.paint.gui.panels.LoginFrame;
import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.packets.Packet05Disconnect;
import com.camoga.paint.net.packets.Packet10Version;
import com.camoga.paint.net.packets.Packet11Password;

public class PaintMain extends Thread {
	
	public String version = "1.1.7";
	
	public Window window;
	public static PaintMain main;
	
	public static void main(String[] args) throws UnknownHostException {
//		new PaintMain("localhost", "null", "MrCamoga" + new Random().nextInt(1000));
		new PaintMain().start();
	}
	
	public PaintMain() {
		main = this;
		window = new Window(this);
	}
	
	public PaintMain(String server, String pass, String username) throws UnknownHostException {
		main = this;
		window = new Window(this);
		
		login(server, pass, username);		
	}
	
	public void loginPanel() throws UnknownHostException {
		LoginFrame panel = new LoginFrame();
		String[] data = panel.login();
		if(data == null) return;
		login(data[0], data[1], data[2]);
	}
	
	public void login(String ip, String pass, String username) throws UnknownHostException {
		ClientSocket socketClient = new ClientSocket(InetAddress.getByName(ip), new Client(username));
		socketClient.start();
		
		ServerManager.addServer(socketClient);
		Packet11Password passwordPacket = new Packet11Password(pass, false);
		passwordPacket.writeData(socketClient);
		
		Packet10Version versionPacket = new Packet10Version(version);
		versionPacket.writeData(socketClient);
	}
	
	public void disconnectAll() {
		for(Iterator<ServerClient> iterator = ServerManager.clients.iterator(); iterator.hasNext();) {
			ServerClient pc = iterator.next();
			if(disconnect(pc)) iterator.remove();
		}
	}
	
	public boolean disconnect(ServerClient p) {
		if(p.socketClient == null) return false;
		Packet05Disconnect disconnectPacket = new Packet05Disconnect(p.socketClient.client.getUsername());
		System.err.println("Disconnecting...");
		ServerManager.removeServer(p);
		disconnectPacket.writeData(p.socketClient);
		return true;
	}
}