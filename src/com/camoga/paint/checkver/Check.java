package com.camoga.paint.checkver;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Check {
	public static String version(String app) throws Exception {
		URL url = new URL("http://www.wicmoga.esy.es/checkversion.php?key=0451b089ebbc431249179f74ce6a7ffe&app=" + app);
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		String version = br.readLine();

		if (version.equals("APPNULL"))
			throw new Exception("The app is null");
		if (version.equals("KEYINCORRECT"))
			throw new Exception("The key you entered is incorrect");
		if (version.equals("APPNOTINLIST")) {
			throw new Exception("The app is not listed");
		}

		return version;
	}

	public static void download(String download, String save) throws IOException {
		URL url = new URL("http://www.wicmoga.esy.es/download/" + download);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + save);
		fos.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);
		fos.close();
		System.out.println("downloaded to: " + System.getProperty("user.dir") + save);
	}
}