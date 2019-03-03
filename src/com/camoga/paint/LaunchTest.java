package com.camoga.paint;

import java.awt.HeadlessException;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JFrame;

import com.camoga.paint.net.server.ServerSocket;

public class LaunchTest {
	public static void main(String[] args) throws HeadlessException, UnknownHostException {
		new ServerSocket().start();
		new PaintMain("localhost", "null", "MrCamoga" + new Random().nextInt(1000)).start();
	}
}
