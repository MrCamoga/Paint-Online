package com.camoga.paint.gui;

import com.camoga.paint.ServerManager;
import com.camoga.paint.events.MouseHandler;
import com.camoga.paint.gui.menus.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Window extends Application {
	
	public static String version = "1.1.7";
	
	public MouseHandler mouse = new MouseHandler();

	public static TabPane serverTabs;
	
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 1300, 780);
		scene.getStylesheets().add("style.css");
		
		serverTabs = new TabPane();
		serverTabs.getSelectionModel().selectedIndexProperty().addListener(c -> {
			ServerManager.setCurrent(serverTabs.getSelectionModel().getSelectedIndex());
		});

		MenuBar menuBar = new MenuBar();
		Menu server = new ServerMenu("Server");
		Menu file = new FileMenu("File");
		Menu edit = new EditMenu("Edit");
		Menu image = new ImageMenu("Image");
		Menu tools = new ToolMenu("Tools");

		menuBar.getMenus().addAll(server,file,edit,image,tools);
		
		root.setTop(menuBar);
		root.setCenter(serverTabs);
		
		Platform.setImplicitExit(false);
		primaryStage.setOnCloseRequest(e -> {
			if(ServerManager.clients.size() > 0) {
				if(new Alert(AlertType.CONFIRMATION, "Are you sure you want to disconnect from all servers?").showAndWait().get() == ButtonType.OK) {
					ServerManager.disconnectAll();
					e.consume();
				}
			} else {
				System.exit(0);
			}
		});
		primaryStage.setTitle("Paint Online by MrCamoga " + version);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		ServerManager.login("localhost", "null", "Nagoreey"+(int)(Math.random()*10000));
	}
}