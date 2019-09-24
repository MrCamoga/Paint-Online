package com.camoga.paint.gui.menus;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.camoga.paint.ServerManager;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class PaletteMenu extends Menu {
	public PaletteMenu(String text) {
		super(text);
		MenuItem newPalette = new MenuItem("New Palette");
		MenuItem customPalette = new MenuItem("Create Custom Palette");
		newPalette.setOnAction(e->newPalette());
//		customPalette.setOnAction(null);
		getItems().addAll(newPalette, customPalette);
	}
	
	private void newPalette() {
		SpinnerNumberModel model1 = new SpinnerNumberModel(1, 0, 4, 1);
		SpinnerNumberModel model2 = new SpinnerNumberModel(1, 0, 4, 1);
		SpinnerNumberModel model3 = new SpinnerNumberModel(1, 0, 4, 1);
		JPanel cP = new JPanel(new GridLayout(3, 2));
		cP.add(new JLabel("Red: "));
		JSpinner r = new JSpinner(model1);
		cP.add(r);
		cP.add(new JLabel("Green: "));
		JSpinner g = new JSpinner(model2);
		cP.add(g);
		cP.add(new JLabel("Blue: "));
		JSpinner b = new JSpinner(model3);
		cP.add(b);
		int i = JOptionPane.showConfirmDialog(ServerManager.currentsc, cP, "Enter num of bits per channel", JOptionPane.OK_CANCEL_OPTION);
		if(i == JOptionPane.OK_OPTION)	ServerManager.currentsc.getCurrentPP().palette((int)r.getValue(), (int)g.getValue(), (int)b.getValue());
		
	}
}