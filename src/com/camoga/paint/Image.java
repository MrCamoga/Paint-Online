package com.camoga.paint;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Image {
	private BufferedImage image;
	public int[] pixels;
	public int width;
	public int height;
	public int UUID;

	public Image(int width, int height, int UUID) {
		this(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), UUID);
	}
	
	public Image(int[] pixels, int width, int height, int UUID) {
		this.width = width;
		this.height = height;
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(pixels, 0, this.pixels, 0, pixels.length);
		this.UUID = UUID;
	}
	
	public Image(BufferedImage image, int UUID) {
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.image = image;
		this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		this.UUID = UUID;
	}

	public int[] getPixels() {
		return this.pixels;
	}
	
	public int getPixel(int x, int y) {
		return pixels[x + y * width];
	}
	
	public int getPixel(int i) {
		return pixels[i];
	}

	public BufferedImage getBufferedImage() {
		return image;
	}

	public void setPixel(int index, int color) {
		pixels[index] = color;
	}
	
	public void setPixel(int x, int y, int color) {
		pixels[x + y * width] = color;
	}
}