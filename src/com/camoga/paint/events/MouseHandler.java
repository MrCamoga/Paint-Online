package com.camoga.paint.events;

import java.awt.event.MouseWheelEvent;

import com.camoga.paint.ServerClient;
import com.camoga.paint.ServerManager;
import com.camoga.paint.gui.Window;
import com.camoga.paint.net.packets.Packet04SelectColor;
import com.camoga.paint.net.packets.Packet13Cursor;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MouseHandler {
	
	public static boolean pressed;
	public static boolean dragged;
	public static int modifier;
	public static int wheelrot;
	public static int x,y;
	public static int lx, ly;

	public void mouseWheelMoved(MouseWheelEvent e) {
		modifier = e.getModifiersEx();
		wheelrot = e.getWheelRotation();

		if (modifier == 128) {
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

	public static void mouseDragged(MouseEvent e) {
		pressed = true;
		dragged = true;
		lx = x; ly = y;
		x = (int)e.getX();
		y = (int)e.getY();
//		pos.x /= ServerManager.currentsc.getCurrentPP().scale;
//		pos.y /= ServerManager.currentsc.getCurrentPP().scale;

		moveCursor();
		e.consume();
	}

	private static void moveCursor() {
		ServerClient sc = ServerManager.currentsc;
		Packet13Cursor cursor = new Packet13Cursor(sc.socketClient.client.getUsername(), x, y, sc.tool.getId(),	sc.getCurrentImageUUID());
		cursor.writeData(sc.socketClient);
	}

	public static void mouseMoved(MouseEvent e) {
		x = (int)e.getX();
		y = (int)e.getY();
//		pos.x /= ServerManager.currentsc.getCurrentPP().scale;
//		pos.y /= ServerManager.currentsc.getCurrentPP().scale;

		moveCursor();
		e.consume();
	}

	public static void mousePressed(MouseEvent e) {
//		modifier = e.getModifiersEx();
		pressed = true;
		if (e.getButton() == MouseButton.PRIMARY) {
			pressed = true;
		} else if (e.getButton() == MouseButton.SECONDARY) {
			ServerClient sc = ServerManager.clients.get(Window.serverTabs.getSelectionModel().getSelectedIndex());
			int color = sc.getCurrentImage().getPixel(x, y);
			sc.getCurrentPP().changeColor(color);
			Packet04SelectColor colorPacket = new Packet04SelectColor(color);
			colorPacket.writeData(ServerManager.currentsc.socketClient);
		}
		e.consume();
	}

	public static void mouseReleased(MouseEvent e) {
		pressed = false;
		dragged = false;
		e.consume();
	}
}