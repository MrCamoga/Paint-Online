package com.camoga.paint.gui.menus;

import com.camoga.paint.PaintMain;
import com.camoga.paint.ServerClient;
import com.camoga.paint.ServerManager;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class ServerMenu extends Menu {
	public ServerMenu(String text) {
		super(text);
		MenuItem connect = new MenuItem("Connect to server");
		MenuItem disconnect = new MenuItem("Disconnect from server");
		MenuItem offline = new MenuItem("Work offline");
		connect.setOnAction(e -> PaintMain.main.loginPanel());
		disconnect.setOnAction(e -> {
			ServerClient current = ServerManager.currentsc;
			if(PaintMain.main.disconnect(current)) ServerManager.clients.remove(current);
		});
//		offline.setOnAction(this);
		getItems().addAll(connect, disconnect, new SeparatorMenuItem(), offline);
	}
}