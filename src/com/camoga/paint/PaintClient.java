package com.camoga.paint;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;
import javax.swing.text.IconView;

import com.camoga.paint.gui.panels.Chat;
import com.camoga.paint.gui.panels.PaintPanel;
import com.camoga.paint.listeners.FileActionListener;
import com.camoga.paint.net.packets.Packet01Paint;
import com.camoga.paint.net.packets.Packet07FillBucket;

@SuppressWarnings("serial")
@Deprecated
public class PaintClient extends JFrame implements Runnable, MouseListener, MouseMotionListener {
	public int SCALE;
	
	public Dimension DIMENSION;
	public Point mousePos = new Point();

	public int color = 0;
	public int brushSize = 2;
	public int tool = 0;

	public PaintPanel paipan;
	public Chat chat;
	
	public boolean running;
	public boolean painting;
	public boolean dragging;
	public Thread thread;
	public ArrayList<BufferedImage> image = new ArrayList<>();
	public ArrayList<int[]> pixels = new ArrayList<>();
	public ArrayList<int[]> size = new ArrayList<>();
	
	private PaintMain main;
	public static PaintClient pc;
	
	private int current = 0;

	public PaintClient(final PaintMain main) {
		System.out.println("Paint Client!");
		this.main = main;
		this.pc = this;
		
		setSize(1280, 720);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//main.disconnect();
			}
		});
		
		paipan = new Paint(this);
		chat = new Chat();
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		FileActionListener filea = new FileActionListener();
		
		JMenuItem New = new JMenuItem("New");
		New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		JMenuItem open = new JMenuItem("Open");
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		JMenuItem save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		JMenuItem saveas = new JMenuItem("Save as");
		JMenuItem close = new JMenuItem("Close");
		close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		JMenuItem exit = new JMenuItem("Exit");
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		
		JMenu edit = new JMenu("Edit");
		
		JMenuItem copy = new JMenuItem("Copy");
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		JMenuItem paste = new JMenuItem("Paste");
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		JMenuItem cut = new JMenuItem("Cut");
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		
		New.addActionListener(filea);
		open.addActionListener(filea);
		save.addActionListener(filea);
		saveas.addActionListener(filea);
		close.addActionListener(filea);
		exit.addActionListener(filea);
		
		file.add(New);
		file.add(open);
		file.add(save);
		file.add(saveas);
		file.add(close);
		file.add(exit);
		
		menuBar.add(file);
		add(menuBar, BorderLayout.NORTH);
		
		add(paipan);
		add(chat, BorderLayout.EAST);
	}

	//FIXME change init method
	public void init(int width, int height, int SCALE, int imageid) {
		System.out.println("INITIIII");
		size.add(new int[]{width, height});
		this.SCALE = SCALE;
		image.add(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
		pixels.add(((DataBufferInt) image.get(imageid).getRaster().getDataBuffer()).getData());
		DIMENSION = new Dimension(SCALE*width, SCALE*width);
		paipan.canvas.setSize(DIMENSION);
		for (int i = 0; i < pixels.get(imageid).length; i++) {
			pixels.get(imageid)[i] = 0xffffff;
		}
		imageid++;
	}

	public void run() {
		long last = System.nanoTime();
		double ns = 1e6;
		double delta = 0;
		while (running) {
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
	
	int lastX = -1;
	int lastY = -1;
	public void tick() {
		if (painting) {
			int WIDTH = this.size.get(current)[0];
			int HEIGHT = this.size.get(current)[1];
			int ys = mousePos.y;
			int xs = mousePos.x;
			if (xs < 0 || ys < 0 || xs >= WIDTH || ys >= HEIGHT) return;
			if(ys != lastY || xs != lastX) {
				switch(tool) {
				case 0:
					Packet01Paint paintPacket = new Packet01Paint(xs, ys, brushSize, color, current);							
					paintPacket.writeData(main.socketClient);
					paintBrush(xs, ys, brushSize, color, current);
					break;
				case 1:
					int target = pixels.get(current)[xs + ys*WIDTH];
					if(target == color) return;
					System.out.println("fillbucket");
					Packet07FillBucket bucketPacket = new Packet07FillBucket(xs, ys, target, color, current);
					bucketPacket.writeData(main.socketClient);
					//floodFill(xs, ys, target, color);
					break;
				case 2:
					Packet01Paint clearPacket = new Packet01Paint(xs, ys, brushSize, 0x00, current);
					clearPacket.writeData(main.socketClient);
					paintBrush(xs, ys, brushSize, 0x0, current);
					break;
				}
			}
			lastX = xs;
			lastY = ys;
		}
	}
	
	public void paintBrush(int xp, int yp, int size, int color, int imageid) {
		int WIDTH = this.size.get(imageid)[0];
		int HEIGHT = this.size.get(imageid)[1];
		for (int y = 0; y < size; y++) {
			int ya = y + yp - size / 2;
			for (int x = 0; x < size; x++) {
				int xa = x + xp - size / 2;
				if (xa < 0 || ya < 0 || xa >= WIDTH || ya >= HEIGHT);
				else if (Math.abs((xa - xp)*(xa - xp) + (ya - yp)*(ya - yp)) < size * size / 4 + 2) {
					if(pixels.get(imageid)[xa + ya*WIDTH] != color) {
						setRGB(xa, ya, color, imageid);
					}
				}
			}
		}
	}
	
	public void setRGB(int x, int y, int color, int imageid) {
		pixels.get(imageid)[x + y * size.get(current)[0]] = color;
	}

	public void render() {
		BufferStrategy buffer = paipan.canvas.getBufferStrategy();
		if (buffer == null) {
			paipan.canvas.createBufferStrategy(3);
			return;
		}

		Graphics g = buffer.getDrawGraphics();
		g.drawImage(image.get(current), paipan.canvas.getX(), paipan.canvas.getY(), DIMENSION.width, DIMENSION.height, null);
		g.dispose();
		buffer.show();
	}

	public void start() {
		if (running) return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public Point lastPos = new Point();
	public void mouseDragged(MouseEvent e) {
		painting = true;
		dragging = true;
		lastPos = mousePos;
		mousePos = e.getPoint();
		mousePos.x /= SCALE;
		mousePos.y /= SCALE;
	}

	public void mouseMoved(MouseEvent e) {
		mousePos = e.getPoint();
		mousePos.x /= SCALE;
		mousePos.y /= SCALE;
	}

	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		System.out.println(pixels.get(current)[mousePos.x + mousePos.y * size.get(current)[0]]);
		System.out.println(mousePos.x + mousePos.y * size.get(current)[0]);
		painting = true;
	}

	public void mouseReleased(MouseEvent e) {
		painting = false;
	}
	
	ArrayList<Point> queue = new ArrayList<>();
	public void floodFill(int x, int y, int targetColor, int color, int imageid) {
		int WIDTH = size.get(imageid)[0];
		int HEIGHT = size.get(imageid)[1];
		if(queue.size()>0) queue.remove(0);
		while(pixels.get(imageid)[x + (y-1)*WIDTH] == targetColor) {
			y--;
		}
		while(pixels.get(imageid)[x + y*WIDTH] == targetColor) {
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
	
	public void imagepacket(int[] pixels, int num, int imageid) {
		for(int i = 0; i < pixels.length; i++) {
			this.pixels.get(imageid)[num*254 + i] = pixels[i];
		}
	}
	
	public BufferedImage getCurrentImage() {
		return image.get(current);
	}
	
	//TODO draw line
	public void drawLine(int x0, int y0, int xf, int yf, int size, int color, int imageid) {
		for(int x = x0; x < xf;  x++) {
			int y = Math.round(y0 + (yf - y0)/(xf - x0)*x);
			paintBrush(x, y, size, color, imageid);
		}
	}
}