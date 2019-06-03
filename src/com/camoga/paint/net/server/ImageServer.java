package com.camoga.paint.net.server;

@Deprecated
public class ImageServer {
	
	public int UUID;
	public int[] pixels;
	public int width, height;
	
	public ImageServer(int[] pixels, int width, int height, int uuid) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		this.UUID = uuid;
	}
	
	public void setPixel(int x, int y, int color) {
		pixels[x+y*width] = color;
	}
	
	public int getPixel(int x, int y) {
		return pixels[x+y*width];
	}

	public int getPixel(int index) {
		return pixels[index];
	}
}