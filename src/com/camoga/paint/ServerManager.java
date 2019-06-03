package com.camoga.paint;

import java.util.ArrayList;

import com.camoga.paint.gui.Window;
import com.camoga.paint.net.client.ClientSocket;

public class ServerManager {
	public static ArrayList<ServerClient> clients = new ArrayList<ServerClient>();
	public static ServerClient currentsc;
	
	public static void addServer(ClientSocket socket) {
		ServerClient p = new ServerClient(socket);
		clients.add(p);
		socket.paint = p;
		Window.window.serverTabs.addTab(socket.getAddress().getHostAddress(), p);
	}

	public static void removeServer(ServerClient paint) {
		paint.stop();
		paint.disconnect();
		if (clients.size() == 1)
			Window.window.serverTabs.removeAll();
		else
			Window.window.serverTabs.removeTabAt(clients.indexOf(paint));
	}

	public static void setCurrent(int index) {
		if (index < 0) currentsc = null;
		else currentsc = clients.get(index);
	}
	
	private ServerManager() {}
}