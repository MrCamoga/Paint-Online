package com.camoga.paint;

import java.util.ArrayList;

import com.camoga.paint.gui.Window;
import com.camoga.paint.net.client.ClientSocket;

import javafx.scene.control.Tab;

public class ServerManager {
	public static ArrayList<ServerClient> clients = new ArrayList<ServerClient>();
	public static ServerClient currentsc;
	
	public static void addServer(ClientSocket socket) {
		ServerClient p = new ServerClient(socket);
		clients.add(p);
		socket.paint = p;
		Window.window.serverTabs.getTabs().add(new Tab(socket.getAddress().getHostAddress(), p));
	}

	public static void removeServer(ServerClient paint) {
		paint.stop();
		paint.disconnect();
		Window.window.serverTabs.getTabs().remove(Window.window.serverTabs.getSelectionModel().getSelectedIndex());
	}

	public static void setCurrent(int index) {
		if (index < 0) currentsc = null;
		else currentsc = clients.get(index);
	}
	
	private ServerManager() {}
}