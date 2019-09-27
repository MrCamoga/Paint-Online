package com.camoga.paint.gui.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.UIManager;

import com.camoga.paint.Image;
import com.camoga.paint.ServerClient;
import com.camoga.paint.ServerManager;
import com.camoga.paint.events.MouseHandler;
import com.camoga.paint.gui.elements.Cursor;
import com.camoga.paint.net.packets.Packet04SelectColor;

import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class PaintPanel extends BorderPane {
	private ServerClient sc;
	//TODO selections
	public int[] pixelsSelected;
	public Button[] colors;
	public Button[] recent;

	public Button colorPicked;

	public Slider slider = new Slider(1, 100, 2);
	public Label brushSize = new Label("2");

	public Button pencil;
	public Button bucket;
	public Button rubber;
	public Button colorpicker;

	public Canvas canvas;
	public Image image;
	public Dimension DIMENSION;
	public int scale;
	public AnimationTimer timer;

	public PaintPanel(final ServerClient sc, int width, int height, int UUID) {
		this.sc = sc;
		
//		setLayout(new GroupLayout(this));

		canvas = new Canvas(512,512);
		canvas.setOnMouseDragged(e -> MouseHandler.mouseDragged(e));
		canvas.setOnMouseMoved(e -> MouseHandler.mouseMoved(e));
		canvas.setOnMousePressed(e -> MouseHandler.mousePressed(e));
		canvas.setOnMouseReleased(e -> MouseHandler.mouseReleased(e));
		canvas.setCursor(javafx.scene.Cursor.NONE);
		setCenter(canvas);
		slider.setOnDragDetected(e -> {
			brushSize.setText(slider.getValue() + "");
			sc.brushSize = (int) slider.getValue();
		});
		
		scale = (512 / height);
		DIMENSION = new Dimension(width, height);
		image = new Image(width, height, UUID);
		GraphicsContext g = canvas.getGraphicsContext2D();
		timer = new AnimationTimer() {
			public void handle(long now) {
				render();
			}
		};
		timer.start();
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

//		palette(3,2,3);
//		recentColors(10, 10);
//		colorPicked = new Button();
//		colorPicked.setBounds(200, 512, 100, 100);
//		colorPicked.setEnabled(false);
//		colorPicked.setFocusPainted(false);
//		colorPicked.setBorderPainted(false);
//		add(colorPicked);
		brushSize.setVisible(true);
		setBottom(brushSize);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		setBottom(slider);
	}
	
	public void recentColors(int width, int height) {
		recent = new Button[width*height];
		for (int i = 0; i < recent.length; i++) {
			recent[i] = new Button();
			recent[i].setFocusPainted(false);
			recent[i].setBorderPainted(false);
			recent[i].setBounds(532 + i % width * 8, 540 + i / width * 8, 8, 8);
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
			getChildren().add(recent[i]);
		}
	}

	public void palette(int red, int green, int blue) {
		if(colors != null) {
			for(Button button : colors) {
				getChildren().remove(button);
			}
			//TODO update jframe
		}
		int buttonSize = 12;
		int noc = 1<<(red+green+blue);
		colors = new Button[noc];
		int width = 8 * (1 << green);
		int height = (1 << red + blue) / width;
		for (int i = 0; i < colors.length; i++) {
			colors[i] = new Button();
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
			getChildren().add(colors[i]);
		}
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
		if(sc.getCurrentImageUUID()==null) return;
	
		GraphicsContext g = canvas.getGraphicsContext2D();

		//Alpha transparency background
		for(int i = 0; i < DIMENSION.height; i++) {
			for(int j = 0; j < DIMENSION.width; j++) {
				g.setFill((i+j)%2==0 ? Color.SILVER:Color.grayRgb(40));
				g.fillRect(j*scale, i*scale, scale, scale);
			}
		}
		
//		System.out.println(sc.getCurrentImage().UUID);
		g.
		g.drawImage(SwingFXUtils.toFXImage(sc.getCurrentImage().getBufferedImage(),null), 0, 0, DIMENSION.width*scale,
				DIMENSION.height*scale);

		g.setFill(Color.RED);
		if (sc.brushSize % 2 == 1)
			g.strokeOval((int) (MouseHandler.x - sc.brushSize * 0.5 + 1.0) * 1,
					(int) (MouseHandler.y - sc.brushSize * 0.5 + 1.0) * 1, sc.brushSize * scale,
					sc.brushSize * scale);
		else {
			g.strokeOval((int) (MouseHandler.x - sc.brushSize * 0.5) * 1,
					(int) (MouseHandler.y - sc.brushSize * 0.5) * 1, sc.brushSize * scale,
					sc.brushSize * scale);
		}

		for (Cursor c : sc.cursors.values()) {
			if (c.uuid == sc.getCurrentImageUUID()) {
				c.render(g);
			}
		}
		g.restore();
	}
}