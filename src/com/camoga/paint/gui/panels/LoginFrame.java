package com.camoga.paint.gui.panels;

import com.camoga.paint.gui.Window;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginFrame extends JPanel {
	private JTextField address = new JTextField();
	private JTextField password = new JTextField();
	private JTextField username = new JTextField();

	public LoginFrame() {
		super(new GridLayout(3, 2, 5, 8));
		JLabel lblIp = new JLabel("IP Address");
		JLabel lblPass = new JLabel("Server Password");
		JLabel lblUser = new JLabel("Username");
		add(lblIp);
		add(this.address);
		add(lblPass);
		add(this.password);
		add(lblUser);
		add(this.username);
	}

	public String[] login() {
		int result = JOptionPane.showConfirmDialog(Window.window, this, "Login", 2);
		if ((result == 2) || (result == -1)) {
			return null;
		}
		return new String[] { this.address.getText(), this.password.getText(), this.username.getText() };
	}
}