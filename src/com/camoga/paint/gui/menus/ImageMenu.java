package com.camoga.paint.gui.menus;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class ImageMenu extends Menu {
	public ImageMenu(String text) {
		super(text);

		MenuItem canvasSize = new MenuItem("Canvas Size");
		MenuItem scale = new MenuItem("Scale Image");
		
		Menu grid = new Menu("Grid");
			MenuItem activate = new MenuItem("Activate/Deactivate");
			MenuItem settings = new MenuItem("Settings");
			
//			activate.setOnAction(value);
//			settings.setOnAction(value);
			grid.getItems().addAll(activate, settings);	

//		canvasSize.setOnAction(value);
//		scale.setOnAction(value);
//		grid.setOnAction(value);
		getItems().addAll(canvasSize, scale, grid);
	}
}