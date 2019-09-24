package com.camoga.paint.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class EditMenu extends Menu {
	public EditMenu(String text) {
		super(text);

		MenuItem undo = new MenuItem("Undo");
		MenuItem redo = new MenuItem("Redo");
		MenuItem copy = new MenuItem("Copy");
		MenuItem cut = new MenuItem("Cut");
		MenuItem paste = new MenuItem("Paste");
//		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 2));
//		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 2));
//		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, 2));
//		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, 2));
//		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, 2));
//		undo.setOnAction(value);
//		redo.setOnAction(value);
//		copy.setOnAction(value);
//		cut.setOnAction(value);
//		paste.setOnAction(value);
		getItems().addAll(undo, redo, copy, cut, paste);
	}
}