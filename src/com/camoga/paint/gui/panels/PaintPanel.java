package com.camoga.paint.gui.panels;

import com.camoga.paint.Image;
import com.camoga.paint.ServerClient;
import com.camoga.paint.events.MouseHandler;
import com.camoga.paint.gui.elements.Cursor;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.awt.*;

public class PaintPanel extends BorderPane {
	private ServerClient sc;
	//TODO selections
	public int[] pixelsSelected;

	public Slider slider = new Slider(1, 100, 2);
	public Label brushSize = new Label("2");

	public Canvas canvas;
	public Image image;
	public Dimension DIMENSION;
	public int scale;
	public AnimationTimer timer;

	public PaintPanel(final ServerClient sc, int width, int height, int UUID) {
        this.sc = sc;

        canvas = new Canvas(512, 512);
        canvas.setOnMouseDragged(MouseHandler::mouseDragged);
        canvas.setOnMouseMoved(MouseHandler::mouseMoved);
        canvas.setOnMousePressed(MouseHandler::mousePressed);
        canvas.setOnMouseReleased(MouseHandler::mouseReleased);
        canvas.setCursor(javafx.scene.Cursor.NONE);
        setCenter(canvas);
        slider.valueProperty().addListener(c -> {
            brushSize.setText(slider.getValue() + "");
            sc.brushSize = (int) slider.getValue();
        });

        scale = (512 / height);
        DIMENSION = new Dimension(width, height);
        image = new Image(width, height, UUID);
        GraphicsContext g = canvas.getGraphicsContext2D();
        timer = new AnimationTimer() {
            public void handle(long now) {
                sc.tick();
                render();
            }
        };
        timer.start();

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e1) {
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
        } catch(Exception e1) {
            e1.printStackTrace();
        }
        setBottom(slider);
    }

	public void changeSelectedColor(int color) {
		sc.color = color;
	}

	public void render() {
		if(sc.getCurrentImageUUID()==null) return;
	
		GraphicsContext g = canvas.getGraphicsContext2D();

		//Alpha transparency background
		for(int i = 0; i < DIMENSION.height; i++) {
			for(int j = 0; j < DIMENSION.width; j++) {
				g.setFill((i+j)%2==0 ? Color.SILVER:Color.grayRgb(70));
				g.fillRect(j*scale, i*scale, scale, scale);
			}
		}

		g.setImageSmoothing(false);
//		System.out.println(sc.getCurrentImage().UUID);
		g.drawImage(SwingFXUtils.toFXImage(sc.getCurrentImage().getBufferedImage(),null), 0, 0, DIMENSION.width*scale,
				DIMENSION.height*scale);

		g.setFill(Color.RED);
		if (sc.brushSize % 2 == 1)
			g.strokeOval((int) (MouseHandler.x/scale - sc.brushSize * 0.5 + 1.0) * scale,
					(int) (MouseHandler.y/scale - sc.brushSize * 0.5 + 1.0) * scale, sc.brushSize * scale,
					sc.brushSize * scale);
		else {
			g.strokeOval((int) (MouseHandler.x/scale - sc.brushSize * 0.5) * scale,
					(int) (MouseHandler.y/scale - sc.brushSize * 0.5) * scale, sc.brushSize * scale,
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