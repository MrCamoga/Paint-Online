package com.camoga.paint.gui.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.camoga.paint.PaintMain;
import com.camoga.paint.ServerManager;
import com.camoga.paint.net.packets.Packet06Chat;

public class Chat extends JPanel {
	public JTextArea chatBox;
	public JScrollPane scrollPane;
	public JTextField chatType;
	public DefaultListModel<String> listOfUsers = new DefaultListModel();
	public JList<String> connectedUsers = new JList(listOfUsers);

	public Chat() {
		setLayout(new BoxLayout(this, 1));
		setBackground(Color.gray);
		chatBox = new JTextArea(20, 50);
		scrollPane = new JScrollPane(chatBox);
		chatBox.setEditable(false);
		chatType = new JTextField(50);
		chatType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((chatType.getText().equals("")) || (command())) {
					chatType.setText("");
					return;
				}
				if(chatType.getText().startsWith("/msg " + ServerManager.currentsc.socketClient.client.getUsername())) {
					chatType.setText("");
					addText("Cannot send pm to yourself");
				}
				Packet06Chat sendMessage = new Packet06Chat(ServerManager.currentsc.socketClient.client.getUsername(),
						chatType.getText());
				sendMessage.writeData(ServerManager.currentsc.socketClient);
				chatType.setText("");
			}

		});
		add(scrollPane, "North");
		add(connectedUsers, "West");
		add(chatType, "South");
	}

	//TODO Clientside commands
	public boolean command() {
		if(!chatType.getText().startsWith("/")) return false;
		String[] params = chatType.getText().split(" ");
		switch (params[0]) {
			case "/clear":
				chatBox.setText("Chat cleared");
				return true;
//			default:
//				addText("Command " + params[0] + " was not found");
		}
		return false;
	}

	public void modifyList(String username, int i) {
		if (i == -1)
			listOfUsers.addElement(username);
		else
			listOfUsers.remove(i);
	}

	public void addText(String msg) {
		chatBox.append(msg + "\n");
		chatBox.setCaretPosition(chatBox.getDocument().getLength());
	}
}