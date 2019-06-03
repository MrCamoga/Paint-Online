package com.camoga.paint;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import com.camoga.paint.gui.Window;

public class Utils {
	public static void saveImage(Image image) {
		try {
			JFileChooser filechooser = new JFileChooser();
			int i = filechooser.showSaveDialog(Window.window);
			if(i == JFileChooser.APPROVE_OPTION)
			ImageIO.write(image.getBufferedImage(), "PNG", filechooser.getSelectedFile());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static int getClientMPIndex(String username, ArrayList<ClientMP> clients) {
		int index = 0;
		boolean found = false;
		for (ClientMP c : clients) {
			if (c.getUsername().equals(username)) {
				found = true;
				break;
			}
			index++;
		}

		return found ? index:-1;
	}
	
	public static ClientMP getClientMP(String username, ArrayList<ClientMP> clients) {
		int index = getClientMPIndex(username, clients);

		return index == -1 ? null:clients.get(index);
	}

	public static int getClientMPIndex(InetAddress address, int port, ArrayList<ClientMP> clients) {
		int index = 0;
		for (ClientMP c : clients) {
			if ((c.address.equals(address)) && (c.port == port)) {
				break;
			}
			index++;
		}

		return index;
	}
}