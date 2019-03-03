package com.camoga.paint.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.camoga.paint.ServerClient;
import com.camoga.paint.ServerManager;


public class ToolsPanel extends JPanel {
	public JButton pencil;
	public JButton bucket;
	public JButton rubber;
	public JButton colorpicker;

	public ToolsPanel() {
		pencil = new JButton("Pencil");
		pencil.setBounds(10, 560, 80, 40);
		pencil.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ServerManager.currentsc.tool = ServerClient.Tool.PENCIL;
			}
		});
		bucket = new JButton("Bucket");
		bucket.setBounds(10, 600, 80, 40);
		bucket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ServerManager.currentsc.tool = ServerClient.Tool.BUCKET;
			}
		});
		rubber = new JButton("Rubber");
		rubber.setBounds(10, 640, 80, 40);
		rubber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ServerManager.currentsc.tool = ServerClient.Tool.RUBBER;
			}
		});
		colorpicker = new JButton("Pick color");
		colorpicker.setBounds(10, 680, 80, 40);
		colorpicker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ServerManager.currentsc.tool = ServerClient.Tool.PICKCOLOR;
			}
		});
		add(pencil);
		add(bucket);
		add(rubber);
		add(colorpicker);
	}
}