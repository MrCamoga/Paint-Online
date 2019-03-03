package com.camoga.paint.gui.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import com.camoga.paint.ClientMP;

public class Cursor extends JButton {
	public int x;
	public int y;
	public int imageid;
	public CursorTypes cursor;
	public ClientMP client;

	public static enum CursorTypes {
		DEFAULT(0, 0, 0, "default.png"), BUCKET(1, 1, 9, "bucket.png"), 
		RUBBER(2, 6, 12, "rubber2.png"), PICKCOLOR(3, DEFAULT);

		public BufferedImage cursorImage;
		public int tool;
		public int x;
		public int y;
		public String path;

		private CursorTypes(int tool, int x, int y, String path) {
			this.tool = tool;
			this.x = x;
			this.y = y;
			this.path = path;
			try {
				cursorImage = ImageIO.read(new FileInputStream("res/cursors/"+path));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private CursorTypes(int tool, CursorTypes type) {
			this(tool, type.x, type.y, type.path);
		}
	}

	public Cursor(ClientMP client, int x, int y, int tool, int imageid) {
		this.x = x;
		this.y = y;
		this.imageid = imageid;
		this.client = client;
		cursor = getCursorType(tool);
	}

	public Cursor(ClientMP client, int x, int y) {
		this(client, x, y, 0, 0);
	}

	public Cursor(ClientMP client, int tool) {
		this(client, 0, 0, tool, 0);
	}

	public Cursor(ClientMP client) {
		this(client, 0);
	}

	public void update(int x, int y, int tool, int imageid) {
		this.x = x;
		this.y = y;
		this.imageid = imageid;
		this.cursor = getCursorType(tool);
	}

	public void render(Graphics g) {
		g.setFont(new Font("Cambria", 1, 16));
		FontMetrics fm = g.getFontMetrics();
		g.drawImage(cursor.cursorImage, x - cursor.x, y - cursor.y, null);
		g.setColor(Color.lightGray);
		g.fillRoundRect(x + 16, y + 7, 2 + fm.stringWidth(client.getUsername()), g.getFont().getSize(),
				5, 5);
		g.setColor(Color.black);
		g.drawString(client.getUsername(), x + 16, y + 20);
	}

	public static CursorTypes getCursorType(int tool) {
		CursorTypes[] arrayOfCursorTypes;
		int j = (arrayOfCursorTypes = CursorTypes.values()).length;
		for (int i = 0; i < j; i++) {
			CursorTypes t = arrayOfCursorTypes[i];
			if (t.tool == tool) {
				return t;
			}
		}
		return CursorTypes.DEFAULT;
	}
}