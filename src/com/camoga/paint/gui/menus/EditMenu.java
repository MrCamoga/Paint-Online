package com.camoga.paint.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

public class EditMenu extends JMenu implements ActionListener {
	public EditMenu(String text) {
		super(text);

		JMenuItem undo = new JMenuItem("Undo");
		JMenuItem redo = new JMenuItem("Redo");
		JMenuItem copy = new JMenuItem("Copy");
		JMenuItem cut = new JMenuItem("Cut");
		JMenuItem paste = new JMenuItem("Paste");
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 2));
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 2));
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, 2));
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, 2));
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, 2));
		undo.addActionListener(this);
		redo.addActionListener(this);
		copy.addActionListener(this);
		cut.addActionListener(this);
		paste.addActionListener(this);
		add(undo);
		add(redo);
		add(copy);
		add(cut);
		add(paste);
	}

	public void actionPerformed(ActionEvent e) {
		//TODO edit menu
	}
}