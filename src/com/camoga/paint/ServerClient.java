package com.camoga.paint;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
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
import com.camoga.paint.net.packets.Packet03PixelArray;
import com.camoga.paint.net.packets.Packet04SelectColor;
import com.camoga.paint.net.packets.Packet07FillBucket;

//FIXME if client connected to same server from two tabs, he'll draw from the two tabs simultaneously
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

	public ArrayList<PaintPanel> paintpanels = new ArrayList<PaintPanel>();
	public ArrayList<ClientMP> connectedClients = new ArrayList<ClientMP>();
	public HashMap<String, Cursor> cursors = new HashMap<String, Cursor>();

	public ServerClient(ClientSocket socket) {
		socketClient = socket;
		setLayout(new BorderLayout());
		chat = new Chat();
		add(tabImages);
		add(chat, BorderLayout.EAST);
	}

	public void init(int width, int height, int UUID) {
		PaintPanel pp = new PaintPanel(this);
		pp.scale = (512 / height);
		pp.canvas.setSize(512, 512);
		pp.DIMENSION = new Dimension(width, height);
		Image img = new Image(width, height, UUID);
		pp.image = img;
		paintpanels.add(pp);

		for (int i = 0; i < img.getPixels().length; i++) {
			img.setPixel(i, 0xffffff);
		}
		tabImages.addTab(UUID + "", pp);
	}

	public void run() {
		long last = System.nanoTime();
		double ns = 1e6;
		double delta = 0;
		while (running) {
//			System.out.println(Window.window.serverTabs.getSelectedIndex());
//			System.out.println(getCurrentPP().image.UUID);
			System.out.println(ServerManager.currentsc.socketClient.getAddress().getHostAddress());
			if (Window.window.serverTabs.getTitleAt(Window.window.serverTabs.getSelectedIndex()).equals(socketClient.getAddress().getHostAddress())) {
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
		// TODO different right - left click actions
		if (Window.window.mouse.pressed) {
			Integer UUID = getCurrentImageUUID();
			if(UUID == null) return;
			Image image = getCurrentImage();
			int WIDTH = image.width;
			int HEIGHT = image.height;
			int ys = Window.window.mouse.pos.y;
			int xs = Window.window.mouse.pos.x;
			if ((xs < 0) || (ys < 0) || (xs >= WIDTH) || (ys >= HEIGHT))
				return;
			if ((ys != lastY) || (xs != lastX)) {
				switch (tool) {
				case PENCIL:
					Packet01Paint paintPacket = new Packet01Paint(xs, ys, brushSize, color, UUID);
					paintPacket.writeData(socketClient);
					pencil(xs, ys, brushSize, color, UUID);
					break;
				case BUCKET:
					int target = image.getPixel(xs,ys);
					if (target == color)
						return;
					System.out.println("fillbucket");
					Packet07FillBucket bucketPacket = new Packet07FillBucket(xs, ys, color, UUID);
					bucketPacket.writeData(socketClient);
					floodFill(xs, ys, target, color, UUID);
					break;
				case RUBBER:
					Packet01Paint rubberPacket = new Packet01Paint(xs, ys, brushSize, 0x00, UUID);
					rubberPacket.writeData(socketClient);
					pencil(xs, ys, brushSize, 0x00, UUID);
					break;
				case PICKCOLOR:
					color = image.getPixel(xs, ys);
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
					for (int i = 0; i < image.pixels.length; i++) {
						// getImage().pixels[i];
					}
					break;
				}

			}

			lastX = xs;
			lastY = ys;
		}
	}

	public void pencil(int xp, int yp, int size, int color, int UUID) {
		Image image = getImage(UUID);
		int WIDTH = image.width;
		int HEIGHT = image.height;
		for (int y = 0; y < size; y++) {
			int ya = y + yp - size / 2;
			for (int x = 0; x < size; x++) {
				int xa = x + xp - size / 2;
				if (xa < 0 || ya < 0 || xa >= WIDTH || ya >= HEIGHT)
					;
				else if (Math.abs((xa - xp) * (xa - xp) + (ya - yp) * (ya - yp)) < size * size / 4 + 2) {
					if (image.getPixel(xa, ya) != color) {
						image.setPixel(xa, ya, color);
					}
				}
			}
		}
	}
	
	public void render() {
		if(tabImages.getSelectedIndex() < paintpanels.size() && tabImages.getSelectedIndex() >= 0) {
//			System.out.println(tabImages.getSelectedIndex());
			getCurrentPP().render();
//			System.out.println(getCurrentPP().render);
		}
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


	// TODO change floodFill algorithm
	
	ArrayList<Point> queue = new ArrayList<Point>();
	public void floodFill(int x, int y, int targetColor, int color, int UUID) {
		Image image = getImage(UUID);
		int WIDTH = image.width;
		int HEIGHT = image.height;
		if(queue.size()>0) queue.remove(0);
		while(y > 0 && image.getPixel(x, y-1) == targetColor) {
			y--;
		}
		boolean left = false, right = false;
		while(y < HEIGHT && image.getPixel(x, y) == targetColor) {
			image.setPixel(x,y, color);
			
			if(!left && x > 0 && image.getPixel(x-1, y) == targetColor) {
				int ytemp = y;
				while(ytemp > 0 && image.getPixel(x-1, ytemp-1) == targetColor) {
					ytemp--;
				}
				queue.add(new Point(x-1, ytemp));
				left = true;
			} else if(left && x > 0 && image.getPixel(x-1, y) != targetColor) left = false;
			if(!right && x < WIDTH - 1 && image.getPixel(x+1,y) == targetColor) {
				int ytemp = y;
				while(ytemp > 0 && image.getPixel(x+1, ytemp-1) == targetColor) {
					ytemp--;
				}
				queue.add(new Point(x+1, ytemp));
				right = true;
			} else if(right && x < WIDTH - 1 && image.getPixel(x+1, y) != targetColor) right = false;
			
			y++;
		}
		if(queue.size()>0)
		floodFill(queue.get(0).x, queue.get(0).y, targetColor, color, UUID);
	}

	public void imagepacket(int[] pixels, int num, int UUID) {
		for (int i = 0; i < pixels.length; i++) {
			getImage(UUID).setPixel(num * Packet03PixelArray.packetsize + i, pixels[i]);
		}
	}

	public void removeImage(int UUID) {
		System.out.println(UUID);
		//DONE Paint freezes for 15s after deleting image
		PaintPanel pp = getPP(UUID);
		System.out.println("Remove PaintPanel " + pp.image.UUID);
		pp.render = false;
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int i = 0;
		for(PaintPanel p : paintpanels) {
			if(p.image.UUID == UUID) break;
			i++;
		}
		paintpanels.remove(pp);
		if(paintpanels.size() == 0) {
			tabImages.removeAll();
			System.out.println("remove all images");
		} else {
			if(i == 0) tabImages.setSelectedIndex(1);
			else tabImages.setSelectedIndex(i-1);
			tabImages.removeTabAt(i);
		}

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
		return paintpanels.get(tabImages.getSelectedIndex());
	}
	
	public PaintPanel getPP(int uuid) {
		for(PaintPanel pp : paintpanels) {
			if(pp.image.UUID == uuid) return pp;
		}
		
		return null;
	}

	public Integer getCurrentImageUUID() {
		if(tabImages.getSelectedIndex() < 0) return null;
//		System.out.println(tabImages.getSelectedIndex());
//		System.out.println(paintpanels.size());
		int uuid = paintpanels.get(tabImages.getSelectedIndex()).image.UUID;
		return uuid;
	}
	
	public Image getImage(Integer uuid) {
		if(uuid == null) return null;
		PaintPanel pp = getPP(uuid);
		if(pp != null) return pp.image;
		return null;
	}
	
	public Image getCurrentImage() {
		return getImage(getCurrentImageUUID());
	}

	public void disconnect() {
		paintpanels.clear();
	}
}