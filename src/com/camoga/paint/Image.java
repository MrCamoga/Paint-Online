package com.camoga.paint;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Image {
	private BufferedImage image;
	public int[] pixels;
	public int width;
	public int height;

	public Image(int width, int height) {
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		this.width = width;
		this.height = height;
	}

	public int[] getPixels() {
		return this.pixels;
	}
	
	public int getPixel(int x, int y) {
		return pixels[x + y * width];
	}

	public BufferedImage getBufferedImage() {
		return image;
	}

	public void setPixel(int x, int y, int color) {
		pixels[x + y * width] = color;
	}
}