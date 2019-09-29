package com.camoga.paint;

import com.camoga.paint.net.packets.Packet03PixelArray;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class Utils {
	public static void saveImage(Image image) {
		try {
			FileChooser filechooser = new FileChooser();
			int i = filechooser.showSaveDialog();
			if(i == FileChooser.APPROVE_OPTION)
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
	
	public static Packet03PixelArray[] sendImage(Image image) {
		int imagesize = image.pixels.length;
		int l = Packet03PixelArray.packetsize;
		Packet03PixelArray[] subimages = new Packet03PixelArray[(int)Math.ceil(imagesize/(double)l)];
		
		for(int pid = 0; pid < subimages.length; pid++) {
			int[] pack = new int[l];
			if(imagesize - pid*l <= l) {
				pack = new int[imagesize-pid*l];
			}
			for(int i = 0; i < pack.length; i++) {
				pack[i] = image.getPixel(pid*l+i);
			}
			subimages[pid] = new Packet03PixelArray(pid, image.UUID, pack);
		}
		
		return subimages;
	}
}