package com.camoga.paint.gui.panels;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.camoga.paint.PaintMain;
import com.camoga.paint.ServerClient;
import com.camoga.paint.ServerManager;
import com.camoga.paint.gui.Window;
import com.camoga.paint.gui.elements.Cursor;
import com.camoga.paint.net.packets.Packet04SelectColor;

public class PaintPanel extends JPanel {
	private ServerClient sc;
	//TODO selections
	public int[] pixelsSelected;
	public JButton[] colors;
	public JButton[] recent = new JButton[400];

	public JButton colorPicked;

	public JSlider slider = new JSlider(0, 1, 100, 2);
	public JLabel brushSize = new JLabel("2");

	public JButton pencil;
	public JButton bucket;
	public JButton rubber;
	public JButton colorpicker;

	public Canvas canvas;
	public Dimension DIMENSION;
	public int scale;

	public PaintPanel(final ServerClient sc) {
		this.sc = sc;

		setLayout(new GroupLayout(this));

		canvas = new Canvas();
		canvas.setBounds(0, 0, 450, 450);
		canvas.addMouseListener(Window.window.mouse);
		canvas.addMouseMotionListener(Window.window.mouse);
		canvas.addMouseWheelListener(Window.window.mouse);
		canvas.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, 2), new Point(0, 0),
				"transparent"));
		add(canvas);
		slider.setBounds(20, 512, 100, 40);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				brushSize.setText(slider.getValue() + "");
				sc.brushSize = slider.getValue();
			}
		});
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		palette(3,2,3);
		colorPicked = new JButton();
		colorPicked.setBounds(200, 512, 100, 100);
		colorPicked.setEnabled(false);
		colorPicked.setFocusPainted(false);
		colorPicked.setBorderPainted(false);
		add(colorPicked);
		brushSize.setBounds(60, 542, 400, 10);
		brushSize.setVisible(true);
		add(brushSize);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		add(slider);
	}

	public void palette(int red, int green, int blue) {
		if(colors != null) {
			for(JButton button : colors) {
				remove(button);
			}
			//TODO update jframe
		}
		int buttonSize = 12;
		int noc = 1<<(red+green+blue);
		colors = new JButton[noc];
		int width = 8 * (1 << green);
		int height = (1 << red + blue) / width;
		//DONE
		for (int i = 0; i < colors.length; i++) {
			colors[i] = new JButton();
			colors[i].setFocusPainted(false);
			colors[i].setBorderPainted(false);
			colors[i].setBounds(
					512 + i / (1 << blue) * buttonSize - i / (width * (1 << red)) * (height * (1 << red) * buttonSize),
					i % (1 << blue) * buttonSize + i / (width * (1 << red)) * ((1 << blue) * buttonSize), buttonSize,
					buttonSize);
			float rf = 1.0f/((1<<red));
			float rfp = 1.0f/((1<<red)-1);
			float gf = 1.0f/((1<<green));	
			float gfp = 1.0f/((1<<green)-1);			
			float bf = 1.0f/((1<<blue));
			float bfp = 1.0f/((1<<blue)-1);
			colors[i].setBackground(new Color(
					rfp*(i/(int)(noc*rf)),
					gfp*(int)((int)(i%(1/(bf*gf)))/(1/bf)),
					bfp*(i%(1/bf))					
					));
			final int c = i;
			colors[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ServerManager.currentsc.getCurrentPP().changeColor(colors[c].getBackground().getRGB());
					Packet04SelectColor colorPacket = new Packet04SelectColor(colors[c].getBackground().getRGB());
					colorPacket.writeData(sc.socketClient);
				}
			});
			add(colors[i]);
		}

		for (int i = 0; i < recent.length; i++) {
			recent[i] = new JButton();
			recent[i].setFocusPainted(false);
			recent[i].setBorderPainted(false);
			recent[i].setBounds(532 + i % 20 * 8, 540 + i / 20 * 8, 8, 8);
			recent[i].setBackground(new Color(-1));
			final int c = i;
			recent[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int rgb = recent[c].getBackground().getRGB();
					if (rgb == -1)
						changeColor(-1);
					else
						changeColor(rgb);
				}

			});
			add(recent[i]);
		}
		revalidate();
		repaint();
	}

	public void changeColor(int color) {
		sc.color = color;
		colorPicked.setBackground(new Color(color));
	}

	public void addRecentColor(int color) {
		for (int i = recent.length - 2; i >= 0; i--) {
			recent[(i + 1)].setBackground(recent[i].getBackground());
		}
		recent[0].setBackground(new Color(color));
	}

	public void render() {
		BufferStrategy buffer = canvas.getBufferStrategy();
		if (buffer == null) {
			canvas.createBufferStrategy(3);
			return;
		}
		Graphics g = buffer.getDrawGraphics();

//		g.setColor(Color.RED);
//		g.fillRect(0, 0, 512, 512);
		for(int i = 0; i < DIMENSION.height; i++) {
			for(int j = 0; j < DIMENSION.width; j++) {
				g.setColor((i+j)%2==0 ? Color.LIGHT_GRAY:Color.DARK_GRAY);
				g.fillRect(j*scale, i*scale, scale, scale);
			}
		}

		g.drawImage(sc.getBufferedImage(), canvas.getX(), canvas.getY(), DIMENSION.width * scale,
				DIMENSION.height * scale, null);

		g.setColor(Color.red);
		if (sc.brushSize % 2 == 1)
			g.drawOval((int) (Window.window.mouse.pos.x - sc.brushSize * 0.5D + 1.0D) * scale,
					(int) (Window.window.mouse.pos.y - sc.brushSize * 0.5D + 1.0D) * scale, sc.brushSize * scale,
					sc.brushSize * scale);
		else {
			g.drawOval((int) (Window.window.mouse.pos.x - sc.brushSize * 0.5D) * scale,
					(int) (Window.window.mouse.pos.y - sc.brushSize * 0.5D) * scale, sc.brushSize * scale,
					sc.brushSize * scale);
		}
		for (Cursor c : sc.cursors.values()) {
			if (c.imageid == sc.getImageID()) {
				c.render(g);
			}
		}
		g.dispose();
		buffer.show();
	}
}