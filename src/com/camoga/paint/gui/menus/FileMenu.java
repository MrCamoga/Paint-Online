package com.camoga.paint.gui.menus;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import com.camoga.paint.Image;
import com.camoga.paint.PaintMain;
import com.camoga.paint.ServerManager;
import com.camoga.paint.Utils;
import com.camoga.paint.net.packets.Packet03PixelArray;
import com.camoga.paint.net.packets.Packet08NewImage;
import com.camoga.paint.net.packets.Packet12DeleteImage;
import com.camoga.paint.net.server.ServerSocket;

public class FileMenu extends JMenu implements ActionListener {
	public FileMenu(String text) {
		super(text);
		JMenuItem New = new JMenuItem("New");
		JMenuItem open = new JMenuItem("Open");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem saveas = new JMenuItem("Save as");
		JMenuItem close = new JMenuItem("Close");
		JMenuItem delete = new JMenuItem("Delete current image");
		JMenuItem exit = new JMenuItem("Exit");
		New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 2));
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 2));
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 2));
		close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, 2));
		delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 2));
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 2));
		New.addActionListener(this);
		open.addActionListener(this);
		save.addActionListener(this);
		saveas.addActionListener(this);
		close.addActionListener(this);
		delete.addActionListener(this);
		exit.addActionListener(this);
		add(New);
		add(open);
		add(save);
		add(saveas);
		add(close);
		add(delete);
		add(exit);
	}

	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "New":
				JPanel inputs = new JPanel(new GridLayout(2, 2, 5, 8));
				JSpinner width = new JSpinner(new SpinnerNumberModel(64, 1, 256, 1));
				JSpinner height = new JSpinner(new SpinnerNumberModel(64, 1, 256, 1));
				inputs.add(new JLabel("width: "));
				inputs.add(width);
				inputs.add(Box.createHorizontalStrut(15));
				inputs.add(new JLabel("height: "));
				inputs.add(height);

				int result = JOptionPane.showConfirmDialog(null, inputs, "Please enter image dimensions", 2);

				if (result == 0) {
					Packet08NewImage createImage = new Packet08NewImage((int) width.getValue(), (int)height.getValue(), new Random().nextInt());
					createImage.writeData(ServerManager.currentsc.socketClient);
					System.out.println("new image " + createImage.getUUID());
				}
				break;
			case "Open":
				JFileChooser file = new JFileChooser();
				int i = file.showOpenDialog(null);
				if(i == JFileChooser.APPROVE_OPTION) {
					try {
						BufferedImage image = ImageIO.read(file.getSelectedFile());
						Packet08NewImage newimage = new Packet08NewImage(image.getWidth(), image.getHeight(), new Random().nextInt());
						newimage.writeData(ServerManager.currentsc.socketClient);
						for(Packet03PixelArray p : Utils.sendImage(new Image(image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()), newimage.getWidth(), newimage.getHeight(), newimage.getUUID()))) {
							p.writeData(ServerManager.currentsc.socketClient);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				break;
			case "Exit":
				PaintMain.main.disconnectAll();
				System.exit(0);
				break;
			case "Save":
				Utils.saveImage(ServerManager.currentsc.getCurrentImage());
				break;
			case "Close":
				break;
			case "Save as":
				break;
			case "Delete current image":
				Packet12DeleteImage delete = new Packet12DeleteImage(ServerManager.currentsc.getCurrentImageUUID());
				delete.writeData(ServerManager.currentsc.socketClient);
				break;

		}
	}
}