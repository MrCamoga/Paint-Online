package com.camoga.paint.net.server;

import com.camoga.paint.ClientMP;
import com.camoga.paint.Image;
import com.camoga.paint.Utils;
import com.camoga.paint.checkver.Check;
import com.camoga.paint.net.packets.Packet05Disconnect;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.FileInputStream;
import java.io.IOException;

public class Console implements ActionListener {
	private ServerSocket server;

	public Console(ServerSocket server) {
		this.server = server;
	}

	public void actionPerformed(ActionEvent e) {
		JTextField intro = (JTextField) e.getSource();
		String[] params = intro.getText().split(" ");
		print("->" + intro.getText());
		exec(params);

		intro.setText("");
	}

	public void exec(String[] params) {
		switch (params[0]) {
			case "/password":
				server.setPassword(params[1]);
				print("Password set to " + server.getPassword());
				break;
			case "/update":
				if (params[1].equals("help")) {
					print("/update <filename> //filename of this program");
				} else {
					try {
						Check.download("Paint/latest_server.jar", params[1]);
						print("Downloaded successfully, please restart the program to complete the update");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				break;
			case "/ban":
				Packet05Disconnect disconnect = new Packet05Disconnect(params[1]);
				disconnect.writeData(this.server);
				break;
			case "/help":
				help(new String[] { "commands" });
				break;
			case "/list":
				if (params[1].equals("admin")) {
					print("Currently there are " + server.getAdmins().size() + " admins");
					for (ClientMP c : server.getAdmins()) {
						print(c.getUsername());
					}
					print("//////////////////");
					return;
				}
				if (params[1].equals("client")) {
					print("Currently there are " + server.getClients().size() + " clients connected");
					for (ClientMP c : server.getClients()) {
						print(c.getUsername());
					}
					print("////////////////");
				}
				break;
			case "/save":
				try {
					Utils.saveImage(server.paint.images.get(Integer.parseInt(params[1])));
				} catch (NumberFormatException e) {
					print("image id must be a number");
					return;
				}
				break;
			case "/view":
				if (GraphicsEnvironment.isHeadless())
					print("Cannot connect to window server");
				else {
					try {
					final int index = Integer.parseInt(params[1]);
						if (server.paint.images.size() <= index)
							return;
						
						Image img = server.paint.images.get(index);
						final int width = img.width;
						final int height = img.height;
						
						JFrame view = new JFrame("Image " + index + " visualization") {
							BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
							int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
							
							public void paint(Graphics g) {
								super.paint(g);
								setSize(640, 640);
								for (int i = 0; i < pixels.length; i++) {
									pixels[i] = img.getPixel(i);
								}
								g.drawImage(image, 20, 40, getContentPane().getWidth(), getContentPane().getHeight(), null);
								g.dispose();
							}
							
						};
						view.setVisible(true);
						view.setResizable(false);
					} catch (NumberFormatException e) {
						print("image id must be a number");
						return;
					}


					// JFrame frame = new JFrame();
					// {
					// BufferedImage image;
					//
					// public void paint(Graphics g) {
					// super.paint(g);
					// setSize(640, 640);
					// g.drawImage(this.image, 0, 0, getContentPane().getWidth(),
					// getContentPane().getHeight(), null);
					// g.dispose();
					// }

					// };
				}
				break;
			case "/admin":
				boolean isAlready = false;
				int index = 0;
				for (ClientMP c : server.getAdmins()) {
					if (params[2].equals(c.getUsername())) {
						isAlready = true;
					}
					index++;
				}
				if (params[1].equals("add")) {
					if (!isAlready) {
						server.getAdmins().add(new ClientMP(params[2], null, -1));
						print("User " + params[2] + " added to admins");
						return;
					}
					print("User is already an admin");
					return;
				}
				if (params[1].equals("remove")) {
					if (isAlready) {
						print("User " + ((ClientMP) server.getAdmins().get(index)).getUsername()
								+ " is no longer an admin");
						server.getAdmins().remove(index);
						return;
					}
					print("[ERROR] User " + ((ClientMP) server.getAdmins().get(index)).getUsername()
							+ " wasn't an admin");
				}
				break;
			case "/check":
				server.check();
				break;
			case "/state":
				Runtime runtime = Runtime.getRuntime();
				print("Connected users: " + server.getClients().size());
				print("//////////////////// MEMORY /////////////////");
				print("Free memory: " + runtime.freeMemory() / 1048576L + " MB");
				print("Used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / 1048576L + " MB");
				break;
			case "/delete":
				server.paint.removeImage(Integer.parseInt(params[1]));
				break;
			default:
				print("Couldn't find the command");
		}
	}

	public void print(String msg) {
		server.console.append(msg + "\n");
		System.out.println(msg);
	}

	//TODO /help xml
	public void help(String[] params) {
		print("/admin <add>/<remove> <username> /Adds or removes that username from the admins\n" + 
				"/delete <index> //Deletes the image with that index\n" + 
				"/list \n" + 
				"   admin //Shows every admin in the server\n" + 
				"   client //Shows every client connected\n" + 
				"/password <password> //Sets the password to <password>\n" + 
				"/state //Shows the state of the server (clients connected, memory usage,...)\n" + 
				"/update <filename> //filename of this program\n" + 
				"/view <index> //Creates a view of the image\n\n");
		Document xml = null;
		try {
			DocumentBuilder doc = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xml = doc.parse(new FileInputStream("/help.xml"));
			xml.getDocumentElement().normalize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}