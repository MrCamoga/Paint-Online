package com.camoga.paint.gui.menus;

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
		connect.setOnAction(e -> ServerManager.loginForm());
		disconnect.setOnAction(e -> ServerManager.disconnect(ServerManager.currentsc));
//		offline.setOnAction(this);
		getItems().addAll(connect, disconnect, new SeparatorMenuItem(), offline);
	}
}