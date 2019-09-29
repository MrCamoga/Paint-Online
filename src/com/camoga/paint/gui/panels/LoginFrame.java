package com.camoga.paint.gui.panels;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LoginFrame {
	public static String[] login() {
		System.out.println("Login frame");
		Dialog<?> dialog = new Dialog<>();
		dialog.setTitle("Connect to server");
		
		ButtonType loginbuttontype = new ButtonType("Login", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginbuttontype, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField address = new TextField();
		TextField password = new TextField("null");
		TextField username = new TextField("MrCamoga"+(int)(Math.random()*1000));
		grid.add(new Label("IP Address: "), 0,0);
		grid.add(new Label("Password: "), 0,1);
		grid.add(new Label("Username: "), 0,2);
		grid.add(address, 1,0);
		grid.add(password, 1,1);
		grid.add(username, 1,2);
		
		Node loginbutton = dialog.getDialogPane().lookupButton(loginbuttontype);
		loginbutton.setDisable(true);
		
		username.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginbutton.setDisable(newValue.trim().isEmpty());
		});
		
		dialog.getDialogPane().setContent(grid);
		
		if(dialog.showAndWait().get() == loginbuttontype) return new String[] {address.getText(), password.getText(),username.getText()};
		return null;
	}
}