package com.camoga.paint.gui;

import com.camoga.paint.ServerManager;
import com.camoga.paint.events.MouseHandler;
import com.camoga.paint.gui.menus.EditMenu;
import com.camoga.paint.gui.menus.FileMenu;
import com.camoga.paint.gui.menus.ImageMenu;
import com.camoga.paint.gui.menus.PaletteMenu;
import com.camoga.paint.gui.menus.ServerMenu;
import com.camoga.paint.gui.menus.ToolMenu;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
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
		
		serverTabs = new TabPane();
		serverTabs.getSelectionModel().selectedIndexProperty().addListener(c -> {
			int i = serverTabs.getSelectionModel().getSelectedIndex();
			System.out.println(i);
			ServerManager.setCurrent(i);
		});

		MenuBar menuBar = new MenuBar();
		Menu server = new ServerMenu("Server");
		Menu file = new FileMenu("File");
		Menu edit = new EditMenu("Edit");
		Menu image = new ImageMenu("Image");
		Menu tools = new ToolMenu("Tools");
		Menu palette = new PaletteMenu("Pallete");
		
		menuBar.getMenus().addAll(server,file,edit,image,tools,palette);
		
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
		
		ServerManager.login("localhost", "null", "MrCamoga"+(int)(Math.random()*10000));
	}
}