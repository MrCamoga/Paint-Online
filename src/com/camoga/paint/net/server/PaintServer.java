package com.camoga.paint.net.server;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PaintServer {

	public static PaintServer ps;
	public ArrayList<int[]> pixels = new ArrayList<>();
	public ArrayList<int[]> size = new ArrayList<>();
	private static final int defaultWidth = 64;
	private static final int defaultHeight = 64;

	public PaintServer() {
		ps = this;
		pixels.add(new int[defaultWidth*defaultHeight]);
		size.add(new int[]{defaultWidth,defaultHeight});
		init(0);
	}

	public static void main(String[] a) {
		new PaintServer();
	}
	
	public void pencil(int xp, int yp, int size, int color, int imageid) {
		int WIDTH = this.size.get(imageid)[0];
		int HEIGHT = this.size.get(imageid)[1];
		for (int y = 0; y < size; y++) {
			int ya = y + yp - size / 2;
			for (int x = 0; x < size; x++) {
				int xa = x + xp - size / 2;
				if (xa < 0 || ya < 0 || xa >= WIDTH || ya >= HEIGHT);
				else if (Math.abs((xa - xp)*(xa - xp) + (ya - yp)*(ya - yp)) < size * size / 4 + 2) {
					if(pixels.get(imageid)[xa + ya*WIDTH] != color) {
						pixels.get(imageid)[xa + ya*WIDTH] = color;
					}
				}
			}
		}
	}
	
	ArrayList<Point> queue = new ArrayList<>();
	public void floodFill(int x, int y, int targetColor, int color, int imageid) {
		int WIDTH = this.size.get(imageid)[0];
		int HEIGHT = this.size.get(imageid)[1];
		if(queue.size()>0) queue.remove(0);
		while(y > 0 && pixels.get(imageid)[x + (y-1)*WIDTH] == targetColor) {
			y--;
		}
		while(y < HEIGHT && pixels.get(imageid)[x + (y)*WIDTH] == targetColor) {
			pixels.get(imageid)[x + y * WIDTH] = color;
			if(pixels.get(imageid)[x-1 + y*WIDTH] == targetColor) {
				int ytemp = y;
				while(pixels.get(imageid)[(x-1) + (ytemp-1)*WIDTH] == targetColor) {
					ytemp--;
				}
				queue.add(new Point(x-1, ytemp));				
			}
			if(pixels.get(imageid)[x+1 + y*WIDTH] == targetColor) {
				int ytemp = y;
				while(pixels.get(imageid)[(x+1) + (ytemp-1)*WIDTH] == targetColor) {
					ytemp--;
				}
				queue.add(new Point(x+1, ytemp));				
			}
			y++;
		}
		
		if(queue.size()>0)
		floodFill(queue.get(0).x, queue.get(0).y, targetColor, color, imageid);
	}

	public void init(int imageid) {
		for (int i = 0; i < pixels.get(imageid).length; i++) {
			pixels.get(imageid)[i] = 0x00ffffff;
		}
	}
	
	public void addImage(int width, int height) {
		pixels.add(new int[width*height]);
		size.add(new int[]{width,height});
	}
	
	public void removeImage(int id) {
		if(pixels.size() <= id) return;
		pixels.remove(id);
		size.remove(id);
	}
	
	public void drawLine(int x0, int y0, int xf, int yf, int size, int color, int imageid) {
		for(int x = x0; x < xf;  x++) {
			int y = Math.round(y0 + (yf - y0)/(xf - x0)*x);
			pencil(x, y, size, color, imageid);
		}
	}
}