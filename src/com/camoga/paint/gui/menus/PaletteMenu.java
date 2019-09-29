package com.camoga.paint.gui.menus;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

@Deprecated
public class PaletteMenu extends Menu {
	public PaletteMenu(String text) {
        super(text);
        MenuItem newPalette = new MenuItem("New Palette");
        MenuItem customPalette = new MenuItem("Create Custom Palette");
//        newPalette.setOnAction(e -> newPalette());
//		customPalette.setOnAction(null);
        getItems().addAll(newPalette, customPalette);
    }
}