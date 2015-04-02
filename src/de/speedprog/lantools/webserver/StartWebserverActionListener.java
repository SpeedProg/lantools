/*
 * Copyright 2014 Constantin Wenger
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.speedprog.lantools.webserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

import org.simpleframework.transport.connect.SocketConnection;

public class StartWebserverActionListener implements ActionListener {
	public static final String AC_START = "START";
	public static final String AC_STOP = "STOP";
	private SocketConnection socketConnection;
	private final WebServer webServer;
	private final JFormattedTextField portTextField;
	private final JTextField hostNameJTextField;

	public StartWebserverActionListener(final WebServer webServer,
			final JFormattedTextField portTextField,
			final JTextField hostNameField) {
		this.webServer = webServer;
		this.portTextField = portTextField;
		this.hostNameJTextField = hostNameField;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final String ac = e.getActionCommand();
		final JButton button = (JButton) e.getSource();
		switch (ac) {
		case AC_START:
			try {
				webServer.connect(
						new InetSocketAddress(Integer.parseInt(portTextField
								.getText())), hostNameJTextField.getText());
				button.setText("Stop Webserver");
				button.setActionCommand(AC_STOP);
			} catch (final NumberFormatException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (final IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		case AC_STOP:
			try {
				webServer.close();
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			button.setText("Start Webserver");
			button.setActionCommand(AC_START);
			break;
		default:
			break;
		}
	}
}
