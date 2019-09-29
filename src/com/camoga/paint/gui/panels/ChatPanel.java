package com.camoga.paint.gui.panels;

import com.camoga.paint.ServerManager;
import com.camoga.paint.net.packets.Packet06Chat;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class ChatPanel extends BorderPane {
	public TextArea chatBox;
	public ScrollPane scrollPane;
	public TextField chatType;
	public ListView<String> connectedUsers = new ListView<>();

	public ChatPanel() {
//		setLayout(new BoxLayout(this, 1));
//		setBackground(Color.gray);
		chatBox = new TextArea(); /// 20x50
		scrollPane = new ScrollPane(chatBox);
		chatBox.setEditable(false);
		chatType = new TextField(); /// 50
		chatType.setOnAction(e -> {
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
		});

		connectedUsers.setPrefHeight(0);
		
		connectedUsers.setOrientation(Orientation.VERTICAL);
		
		setTop(scrollPane);
		setCenter(connectedUsers);
		setBottom(chatType);
	}

	//TODO Clientside commands
	public boolean command() {
		if(!chatType.getText().startsWith("/")) return false;
		String[] params = chatType.getText().split(" ");
		switch (params[0]) {
			case "/clear":
				chatBox.setText("ChatPanel cleared");
				return true;
//			default:
//				addText("Command " + params[0] + " was not found");
		}
		return false;
	}

	public void modifyList(String username, int i) {
		Platform.runLater(()->{
			if (i == -1)
				connectedUsers.getItems().add(username);
			else
				connectedUsers.getItems().remove(username);			
		});
	}

	public void addText(String msg) {
		chatBox.appendText(msg + "\n");
//		chatBox.setCaretPosition(chatBox.getDocument().getLength());
	}
}