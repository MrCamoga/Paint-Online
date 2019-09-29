package com.camoga.paint;

import com.camoga.paint.gui.Window;
import com.camoga.paint.gui.panels.LoginFrame;
import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.packets.Packet05Disconnect;
import com.camoga.paint.net.packets.Packet10Version;
import com.camoga.paint.net.packets.Packet11Password;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServerManager {
	public static ArrayList<ServerClient> clients = new ArrayList<ServerClient>();
	public static ServerClient currentsc;
	
	private static void addServer(ClientSocket socket) {
		socket.paint = new ServerClient(socket);
		clients.add(socket.paint);
		Tab servertab = new Tab(socket.getAddress().getHostAddress(), socket.paint);
		servertab.setOnCloseRequest(e -> {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to disconnect from the server?", ButtonType.YES, ButtonType.NO);
			alert.setTitle("Disconnect from server");
			if(alert.showAndWait().get() == ButtonType.YES) {
				disconnect(socket.paint);
				e.consume();
			}
		});
		Window.serverTabs.getTabs().add(servertab);
	}
	
	public static void disconnectAll() {
        clients.removeIf(ServerManager::disconnect);
	}

	public static void loginForm() {
		String[] data = LoginFrame.login();
		if(data == null) return;
		login(data[0], data[1], data[2]);
	}
	
	public static void login(String ip, String pass, String username) {
		try {
			System.out.println(ip + ", " + pass + ", " + username);
			ClientSocket socketClient = new ClientSocket(InetAddress.getByName(ip), new Client(username));
			socketClient.start();
			
			addServer(socketClient);
			Packet11Password passwordPacket = new Packet11Password(pass, false);
			passwordPacket.writeData(socketClient);
			
			Packet10Version versionPacket = new Packet10Version(Window.version);
			versionPacket.writeData(socketClient);			
		} catch(UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean disconnect(ServerClient p) {
		if(p.socketClient == null) return false;
		Packet05Disconnect disconnectPacket = new Packet05Disconnect(p.socketClient.client.getUsername());
		System.err.println("Disconnecting...");
		clients.remove(p);
		removeServer(p);
		disconnectPacket.writeData(p.socketClient);
		return true;
	}

	private static void removeServer(ServerClient paint) {
		paint.disconnect();
		Window.serverTabs.getTabs().remove(Window.serverTabs.getSelectionModel().getSelectedIndex());
	}

	public static void setCurrent(int index) {
		System.out.println(clients);
		if (index < 0) currentsc = null;
		else currentsc = clients.get(index);
	}
	
	private ServerManager() {}
}