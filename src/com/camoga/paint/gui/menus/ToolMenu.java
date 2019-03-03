package com.camoga.paint.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.camoga.paint.ServerClient;
import com.camoga.paint.ServerManager;

public class ToolMenu extends JMenu implements ActionListener {
	public ToolMenu(String text) {
		super(text);
		JMenuItem pencil = new JMenuItem("Pencil");
		JMenuItem brush = new JMenuItem("Brush");
		JMenuItem bucket = new JMenuItem("Bucket");
		JMenuItem rubber = new JMenuItem("Rubber");
		JMenuItem pickColor = new JMenuItem("Pick Color");

		JMenu selection = new JMenu("Selection Tools");
		JMenuItem rectangular = new JMenuItem("Rectangular Select");
		JMenuItem ellipse = new JMenuItem("Ellipse Select");
		JMenuItem free = new JMenuItem("Free Select");
		JMenuItem bycolor = new JMenuItem("By Color Select");
		rectangular.addActionListener(this);
		ellipse.addActionListener(this);
		free.addActionListener(this);
		bycolor.addActionListener(this);
		selection.add(rectangular);
		selection.add(ellipse);
		selection.add(free);
		selection.add(bycolor);

		pencil.addActionListener(this);
		brush.addActionListener(this);
		bucket.addActionListener(this);
		rubber.addActionListener(this);
		pickColor.addActionListener(this);

		add(selection);
		add(pencil);
		add(brush);
		add(bucket);
		add(rubber);
		add(pickColor);
	}

	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
			case "Pencil":
				ServerManager.currentsc.tool = ServerClient.Tool.PENCIL;
				break;
			case "Rubber":
				ServerManager.currentsc.tool = ServerClient.Tool.RUBBER;
				break;
			case "Rectangular Select":
				ServerManager.currentsc.tool = ServerClient.Tool.RECTSEL;
				break;
			case "Pick Color":
				ServerManager.currentsc.tool = ServerClient.Tool.PICKCOLOR;
				break;
			case "Brush":
				ServerManager.currentsc.tool = ServerClient.Tool.BRUSH;
				break;
			case "Bucket":
				ServerManager.currentsc.tool = ServerClient.Tool.BUCKET;
				break;

		}
	}
}