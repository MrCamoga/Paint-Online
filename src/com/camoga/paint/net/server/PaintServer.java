package com.camoga.paint.net.server;

import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Random;

import com.camoga.paint.Image;
import com.camoga.paint.net.packets.Packet03PixelArray;

public class PaintServer {

	public static PaintServer ps;
	public ArrayList<Image> images = new ArrayList<>();
	private static final int defaultWidth = 64;
	private static final int defaultHeight = 64;

	public PaintServer() {
		ps = this;
		images.add(new Image(defaultWidth, defaultHeight, getNewUUID()));
		System.out.println(images.get(0).UUID);
	}
	
	public int getNewUUID() {
		int UUID;
		do {
			UUID = new Random().nextInt();
		} while(getImage(UUID) != null);
		return UUID;
	}

	public static void main(String[] a) {
		new PaintServer();
	}
	
	public void pencil(int xp, int yp, int size, int color, int uuid) {
		Image image = getImage(uuid);
		int width = image.width;
		int height = image.height;
		for (int y = 0; y < size; y++) {
			int ya = y + yp - size / 2;
			for (int x = 0; x < size; x++) {
				int xa = x + xp - size / 2;
				if (xa < 0 || ya < 0 || xa >= width || ya >= height);
				else if (Math.abs((xa - xp)*(xa - xp) + (ya - yp)*(ya - yp)) < size * size / 4 + 2) {
					if(image.getPixel(xa, ya) != color) {
						image.setPixel(xa, ya, color);
					}
				}
			}
		}
	}
	
	ArrayList<Point> queue = new ArrayList<>();
	public void floodFill(int x, int y, int targetColor, int color, int uuid) {
		Image image = getImage(uuid);
		int width = image.width;
		Toolkit.getDefaultToolkit().beep();
		int height = image.height;
		if(queue.size()>0) queue.remove(0);
		while(y > 0 && image.getPixel(x, y-1) == targetColor) {
			y--;
		}
		boolean left = false, right = false;
		while(y < height && image.getPixel(x, y) == targetColor) {
			image.setPixel(x,y, color);
			
			if(!left && x > 0 && image.getPixel(x-1, y) == targetColor) {
				int ytemp = y;
				while(ytemp > 0 && image.getPixel(x-1, ytemp-1) == targetColor) {
					ytemp--;
				}
				queue.add(new Point(x-1, ytemp));
				left = true;
			} else if(left && x > 0 && image.getPixel(x-1, y) != targetColor) left = false;
			if(!right && x < width - 1 && image.getPixel(x+1, y) == targetColor) {
				int ytemp = y;
				while(ytemp > 0 && image.getPixel(x+1, ytemp-1) == targetColor) {
					ytemp--;
				}
				queue.add(new Point(x+1, ytemp));
				right = true;
			} else if(right && x < width - 1 && image.getPixel(x+1, y) != targetColor) right = false;
			
			
			y++;
		}
		if(queue.size()>0)
		floodFill(queue.get(0).x, queue.get(0).y, targetColor, color, uuid);
	}
	
	public void addImage(int width, int height, int uuid) {
		images.add(new Image(width, height, uuid));
	}
	
	public void removeImage(int uuid) {
		Image img = getImage(uuid);
		if(img == null) return;
		images.remove(img);
	}
	
	public Image getImage(int UUID) {
		for(Image img : images) {
			if(img.UUID == UUID) return img;
		}
		System.out.println("getImage(uuid) null " + UUID);
		return null;
	}
	
	public void drawLine(int x0, int y0, int xf, int yf, int size, int color, int uuid) {
		for(int x = x0; x < xf;  x++) {
			int y = Math.round(y0 + (yf - y0)/(xf - x0)*x);
			pencil(x, y, size, color, uuid);
		}
	}

	public void handlePixelArray(int[] pixels, int num, int uuid) {
		for (int i = 0; i < pixels.length; i++) {
			getImage(uuid).setPixel(num * Packet03PixelArray.packetsize + i, pixels[i]);
		}
	}
}