package com.camoga.paint.gui.menus;

import com.camoga.paint.ServerClient;
import com.camoga.paint.ServerManager;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class ToolMenu extends Menu {
	public ToolMenu(String text) {
		super(text);
		MenuItem pencil = new MenuItem("Pencil");
		MenuItem brush = new MenuItem("Brush");
		MenuItem bucket = new MenuItem("Bucket");
		MenuItem rubber = new MenuItem("Rubber");
		MenuItem pickColor = new MenuItem("Pick Color");

		Menu selection = new Menu("Selection Tools");
		MenuItem rectangular = new MenuItem("Rectangular Select");
		MenuItem ellipse = new MenuItem("Ellipse Select");
		MenuItem free = new MenuItem("Free Select");
		MenuItem bycolor = new MenuItem("By Color Select");
		rectangular.setOnAction(e -> ServerManager.currentsc.tool = ServerClient.Tool.RECTSEL);
		ellipse.setOnAction(e -> ServerManager.currentsc.tool = ServerClient.Tool.ELIPSEL);
//		free.setOnAction(value);
//		bycolor.setOnAction(value);
		selection.getItems().addAll(rectangular, ellipse, free, bycolor);

		pencil.setOnAction(e -> ServerManager.currentsc.tool = ServerClient.Tool.PENCIL);
		brush.setOnAction(e -> ServerManager.currentsc.tool = ServerClient.Tool.BRUSH);
		bucket.setOnAction(e -> ServerManager.currentsc.tool = ServerClient.Tool.BUCKET);
		rubber.setOnAction(e -> ServerManager.currentsc.tool = ServerClient.Tool.RUBBER);
		pickColor.setOnAction(e -> ServerManager.currentsc.tool = ServerClient.Tool.PICKCOLOR);

		getItems().addAll(selection, pencil, brush, bucket, rubber, pickColor);
	}
}