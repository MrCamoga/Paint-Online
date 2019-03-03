package com.camoga.paint.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.camoga.paint.PaintMain;
import com.camoga.paint.ServerManager;
import com.camoga.paint.events.MouseHandler;
import com.camoga.paint.gui.menus.EditMenu;
import com.camoga.paint.gui.menus.FileMenu;
import com.camoga.paint.gui.menus.ImageMenu;
import com.camoga.paint.gui.menus.PaletteMenu;
import com.camoga.paint.gui.menus.ServerMenu;
import com.camoga.paint.gui.menus.ToolMenu;

public class Window extends JFrame {
	public MouseHandler mouse = new MouseHandler(this);

	public JTabbedPane serverTabs;
	public static Window window;

	public Window(PaintMain main) {
		super("Paint Online by MrCamoga " + PaintMain.main.version);
		window = this;
		getRootPane().setWindowDecorationStyle(0);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(ServerManager.clients.size() > 0) {
					int i = JOptionPane.showConfirmDialog(Window.window, "Are you sure you want to disconnect from all servers?");
					if(i==JOptionPane.OK_OPTION) PaintMain.main.disconnectAll();					
				} else {
					System.exit(0);
				}
			}

		});
		serverTabs = new JTabbedPane();
		JPanel north = new JPanel(new BorderLayout());

		JMenuBar menuBar = new JMenuBar();
		JMenu server = new ServerMenu("Server");
		JMenu file = new FileMenu("File");
		JMenu edit = new EditMenu("Edit");
		JMenu image = new ImageMenu("Image");
		JMenu tools = new ToolMenu("Tools");
		JMenu palette = new PaletteMenu("Pallete");
		menuBar.add(server);
		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(image);
		menuBar.add(tools);
		menuBar.add(palette);

		north.add(menuBar, BorderLayout.NORTH);

		serverTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ServerManager.setCurrent(serverTabs.getSelectedIndex());
			}
		});
		add(serverTabs);

		add(north, BorderLayout.NORTH);
		pack();
		setSize(1300, 780);
		setLocationRelativeTo(null);
		setResizable(true);
		setVisible(true);
	}
}