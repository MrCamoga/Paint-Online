package com.camoga.paint.events;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.camoga.paint.ServerClient;
import com.camoga.paint.ServerManager;
import com.camoga.paint.gui.Window;
import com.camoga.paint.net.packets.Packet04SelectColor;
import com.camoga.paint.net.packets.Packet13Cursor;

public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {
	private Window window;
	public boolean pressed;
	boolean dragged;
	public int modifier;
	public int wheelrot;
	public Point pos = new Point();
	public Point lastPos = new Point();

	public MouseHandler(Window window) {
		this.window = window;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		modifier = e.getModifiersEx();
		wheelrot = e.getWheelRotation();

		if (this.modifier == 128) {
			if (wheelrot > 0) {
				ServerManager.currentsc.getCurrentPP().scale /= 1.1D;
				System.out.println("++");
			} else {
				ServerManager.currentsc.getCurrentPP().scale *= 1.1D;
				System.out.println("--");
			}
			System.out.println(ServerManager.currentsc.getCurrentPP().scale);
		}
	}

	public void mouseDragged(MouseEvent e) {
		pressed = true;
		dragged = true;
		lastPos = pos;
		pos = e.getPoint();
		pos.x /= ServerManager.currentsc.getCurrentPP().scale;
		pos.y /= ServerManager.currentsc.getCurrentPP().scale;

		moveCursor(e);
	}

	public void moveCursor(MouseEvent e) {
		Packet13Cursor cursor = new Packet13Cursor(ServerManager.currentsc.socketClient.client.getUsername(), e.getPoint().x,
				e.getPoint().y, ServerManager.currentsc.tool.getId(),
				ServerManager.currentsc.getCurrentImageUUID());
		cursor.writeData(ServerManager.currentsc.socketClient);
	}

	public void mouseMoved(MouseEvent e) {
		pos = e.getPoint();
		pos.x /= ServerManager.currentsc.getCurrentPP().scale;
		pos.y /= ServerManager.currentsc.getCurrentPP().scale;

		moveCursor(e);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		modifier = e.getModifiersEx();
		pressed = true;
		if (e.getButton() == 1) {
			pressed = true;
		} else if (e.getButton() == 3) {
			ServerClient sc = ServerManager.clients.get(window.serverTabs.getSelectedIndex());
			int color = sc.getCurrentImage().getPixel(pos.x, pos.y);
			sc.getCurrentPP().changeColor(color);
			Packet04SelectColor colorPacket = new Packet04SelectColor(color);
			colorPacket.writeData(ServerManager.currentsc.socketClient);
		}
	}

	public void mouseReleased(MouseEvent e) {
		pressed = false;
		dragged = false;
	}
}