package com.camoga.paint;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.camoga.paint.gui.Window;
import com.camoga.paint.gui.elements.Cursor;
import com.camoga.paint.gui.panels.Chat;
import com.camoga.paint.gui.panels.PaintPanel;
import com.camoga.paint.net.client.ClientSocket;
import com.camoga.paint.net.packets.Packet01Paint;
import com.camoga.paint.net.packets.Packet04SelectColor;
import com.camoga.paint.net.packets.Packet07FillBucket;

//FIXME if client connected to same server from to tabs, he'll draw from the two servers simultaneously
public class ServerClient extends JPanel implements Runnable {
	public static enum Tool {
		PENCIL(0), BUCKET(1), RUBBER(2), PICKCOLOR(3), BRUSH(4), RECTSEL(5), ELIPSEL(6), COLORSEL(7);

		private int id;

		private Tool(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	public int color = 0xffffff;
	public int brushSize = 2;
	public Tool tool = Tool.PENCIL;
	public Chat chat;
	public boolean running;
	public Thread thread;
	public JTabbedPane tabImages = new JTabbedPane();

	public ClientSocket socketClient;

	public ArrayList<Image> image = new ArrayList<Image>();
	public ArrayList<PaintPanel> paipanels = new ArrayList<PaintPanel>();
	public ArrayList<ClientMP> connectedClients = new ArrayList<ClientMP>();
	public HashMap<String, Cursor> cursors = new HashMap<String, Cursor>();

	public ServerClient(ClientSocket socket) {
		socketClient = socket;
		setLayout(new BorderLayout());
		chat = new Chat();
		add(tabImages);
		add(chat, BorderLayout.EAST);
	}

	public void init(int width, int height, int imageid) {
		PaintPanel pp = new PaintPanel(this);
		Image img = new Image(width, height);
		pp.scale = (512 / height);
		image.add(img);
		pp.DIMENSION = new Dimension(width, height);
		pp.canvas.setSize(512, 512);

		for (int i = 0; i < image.get(imageid).getPixels().length; i++) {
			image.get(imageid).getPixels()[i] = 0xffffff;
		}
		paipanels.add(pp);
		tabImages.addTab(imageid + "", pp);
	}

	public void run() {
		long last = System.nanoTime();
		double ns = 1e6;
		double delta = 0;
		while (running) {
			if (Window.window.serverTabs.getTitleAt(Window.window.serverTabs.getSelectedIndex())
					.equals(socketClient.getAddress().getHostAddress())) {
				long now = System.nanoTime();
				delta += (now - last) / ns;
				last = now;
				while (delta >= 1) {
					delta--;
					tick();
				}
				render();
			}
		}
	}

	int lastX = -1;
	int lastY = -1;

	public void tick() {
		// TODO
		if (Window.window.mouse.pressed) {
			int WIDTH = image.get(getImageID()).width;
			int HEIGHT = image.get(getImageID()).height;
			int ys = Window.window.mouse.pos.y;
			int xs = Window.window.mouse.pos.x;
			if ((xs < 0) || (ys < 0) || (xs >= WIDTH) || (ys >= HEIGHT))
				return;
			if ((ys != lastY) || (xs != lastX)) {
				switch (tool) {
				case PENCIL:
					Packet01Paint paintPacket = new Packet01Paint(xs, ys, brushSize, color, getImageID());
					paintPacket.writeData(socketClient);
					pencil(xs, ys, brushSize, color, getImageID());
					break;
				case BUCKET:
					int target = image.get(getImageID()).getPixels()[(xs + ys * WIDTH)];
					if (target == color)
						return;
					System.out.println("fillbucket");
					Packet07FillBucket bucketPacket = new Packet07FillBucket(xs, ys, target, color, getImageID());
					bucketPacket.writeData(socketClient);
					floodFill(xs, ys, target, color, getImageID());
					break;
				case RUBBER:
					Packet01Paint rubberPacket = new Packet01Paint(xs, ys, brushSize, 0x00, getImageID());
					rubberPacket.writeData(socketClient);
					pencil(xs, ys, brushSize, 0x00, getImageID());
					break;
				case PICKCOLOR:
					color = image.get(getImageID()).getPixels()[(xs + ys * HEIGHT)];
					Packet04SelectColor packet = new Packet04SelectColor(color);
					packet.writeData(socketClient);
					break;
				case BRUSH:
					break;
				case RECTSEL:
					break;
				case ELIPSEL:
					break;
				case COLORSEL:
					for (int i = 0; i < getImage().pixels.length; i++) {
						// getImage().pixels[i];
					}
					break;
				}

			}

			lastX = xs;
			lastY = ys;
		}
	}

	public void pencil(int xp, int yp, int size, int color, int imageid) {
		int WIDTH = image.get(imageid).width;
		int HEIGHT = image.get(imageid).height;
		for (int y = 0; y < size; y++) {
			int ya = y + yp - size / 2;
			for (int x = 0; x < size; x++) {
				int xa = x + xp - size / 2;
				if (xa < 0 || ya < 0 || xa >= WIDTH || ya >= HEIGHT)
					;
				else if (Math.abs((xa - xp) * (xa - xp) + (ya - yp) * (ya - yp)) < size * size / 4 + 2) {
					if (image.get(imageid).getPixels()[(xa + ya * WIDTH)] != color) {
						setRGB(xa, ya, color, imageid);
					}
				}
			}
		}
	}

	public void setRGB(int x, int y, int color, int imageid) {
		image.get(imageid).getPixels()[(x + y * image.get(imageid).width)] = color;
	}

	public void render() {
		getCurrentPP().render();
	}

	public void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		if (!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	ArrayList<Point> queue = new ArrayList();

	// TODO change floodFill algorithm
	public void floodFill(int x, int y, int targetColor, int color, int imageid) {
		int WIDTH = image.get(imageid).width;
		int HEIGHT = image.get(imageid).height;
		if (queue.size() > 0)
			queue.remove(0);
		do {
			y--;
			if (y <= 0)
				break;
		} while (image.get(imageid).getPixels()[(x + (y - 1) * WIDTH)] == targetColor);

		while ((y < HEIGHT) && (image.get(imageid).getPixels()[(x + y * WIDTH)] == targetColor)) {
			System.out.println("x" + x + ", y: " + y);
			image.get(imageid).getPixels()[(x + y * WIDTH)] = color;
			if ((x > 0) && (image.get(imageid).getPixels()[(x - 1 + y * WIDTH)] == targetColor)) {
				int ytemp = y;
				while ((ytemp > 0) && (x - 1 >= 0) && (image.get(imageid))
						.getPixels()[(x - 1 + (ytemp - 1) * WIDTH)] == targetColor) {
					ytemp--;
				}
				queue.add(new Point(x - 1, ytemp));
			}
			if ((x < getBufferedImage().getWidth() - 1)
					&& (image.get(imageid).getPixels()[(x + 1 + y * WIDTH)] == targetColor)) {
				int ytemp = y;
				while ((ytemp > 0) && (image.get(imageid))
						.getPixels()[(x + 1 + (ytemp - 1) * WIDTH)] == targetColor) {
					ytemp--;
				}
				queue.add(new Point(x + 1, ytemp));
			}
			y++;
		}
		if (queue.size() > 0)
			floodFill(((Point) queue.get(0)).x, ((Point) queue.get(0)).y, targetColor, color, imageid);
	}

	public void imagepacket(int[] pixels, int num, int imageid) {
		for (int i = 0; i < pixels.length; i++) {
			image.get(imageid).getPixels()[(num * 254 + i)] = pixels[i];
		}
	}

	public void addImage(int width, int height) {
		image.add(new Image(width, height));
	}

	public void removeImage(int imageid) {
		image.remove(imageid);
	}

	public BufferedImage getBufferedImage() {
		return getImage().getBufferedImage();
	}

	public Image getImage() {
		return image.get(getImageID());
	}

	// TODO draw line
	public void drawLine(int x0, int y0, int xf, int yf, int size, int color, int imageid) {
		for (int x = x0; x < xf; x++) {
			int y = Math.round(y0 + (yf - y0) / (xf - x0) * x);
			pencil(x, y, size, color, imageid);
		}
	}

	public void addClient(ClientMP c) {
		connectedClients.add(c);

		chat.modifyList(c.getUsername(), -1);
		chat.addText(c.getUsername() + " has joined...");

		addCursor(c);
	}

	public void removeClient(String username) {
		chat.modifyList(username, Utils.getClientMPIndex(username, connectedClients));
		connectedClients.remove(Utils.getClientMPIndex(username, connectedClients));

		chat.addText(username + " has disconnected...");

		removeCursor(username);
	}

	public void addCursor(ClientMP c) {
		cursors.put(c.getUsername(), new Cursor(c));
		System.out.println(c.getUsername() + " cursor added");
	}

	public void updateCursor(String username, int x, int y, int tool, int imageid) {
		cursors.get(username).update(x, y, tool, imageid);
	}

	public void removeCursor(String username) {
		cursors.remove(username);
		System.out.println(username + " cursor removed");
	}

	public PaintPanel getCurrentPP() {
		return paipanels.get(getImageID());
	}

	public int getImageID() {
		return tabImages.getSelectedIndex();
	}

	public void disconnect() {
		image.clear();
	}
}