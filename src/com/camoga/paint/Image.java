package com.camoga.paint;

import java.awt.image.BufferedImage;

public class Image {
	private BufferedImage image;
	public int[] pixels;
	public int width;
	public int height;

	public Image(int width, int height) {
		this.image = new BufferedImage(width, height, 2);
		this.pixels = ((java.awt.image.DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
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
		return this.image;
	}
}