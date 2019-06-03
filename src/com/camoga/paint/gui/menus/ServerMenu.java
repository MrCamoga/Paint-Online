package com.camoga.paint.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.camoga.paint.PaintMain;
import com.camoga.paint.ServerClient;
import com.camoga.paint.ServerManager;

public class ServerMenu extends JMenu implements ActionListener {
	public ServerMenu(String text) {
		super(text);
		JMenuItem connect = new JMenuItem("Connect to server");
		JMenuItem disconnect = new JMenuItem("Disconnect from server");
		JSeparator splitter = new JSeparator();
		JMenuItem offline = new JMenuItem("Work offline");
		connect.addActionListener(this);
		disconnect.addActionListener(this);
		offline.addActionListener(this);
		add(connect);
		add(disconnect);
		add(splitter);
		add(offline);
	}

	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "Work offline":
				break;
			case "Disconnect from server":
				ServerClient current = ServerManager.currentsc;
				if(PaintMain.main.disconnect(current)) ServerManager.clients.remove(current);
				System.out.println(ServerManager.clients.size() + " servers");
				break;
			case "Connect to server":
				try {
					PaintMain.main.loginPanel();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				break;
		}
	}
}