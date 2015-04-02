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
package de.speedprog.lantools.modules.about;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.apache.commons.io.IOUtils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.speedprog.lantools.LanTools;

public class AboutPanel extends JPanel {
	private JTabbedPane tabbedPane;
	private JPanel panelAppInfo;
	private JTextPane textPaneAppInfo;
	private JScrollPane scrollPaneAppInfo;
	private static final String APP_INFO = "Application Name: LanTools\nContributors: Constantin Wenger\nVersion: "
			+ LanTools.VERSION_STRING
			+ "\nVersion N: "
			+ LanTools.VERSION_NUM
			+ "\nLicense: Apache License 2.0\n";
	private String DEP_INFO;
	private JPanel panelDepInfo;
	private JTextPane textPaneDepInfo;
	private JScrollPane scrollPaneDepInfo;

	/**
	 * Create the panel.
	 */
	public AboutPanel() {
		final InputStream dInputStream = AboutPanel.class.getClassLoader()
				.getResourceAsStream(
						"de/speedprog/lantools/modules/about/depinfo.html");
		if (dInputStream == null) {
			DEP_INFO = "Info File wasn't found :(";
		} else {
			try {
				DEP_INFO = IOUtils.toString(dInputStream, "UTF-8");
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				DEP_INFO = "Info File wasn't found :(";
			}
		}
		initialize();
	}

	private void initialize() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), }, new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("default:grow"), }));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setPreferredSize(new Dimension(5, 200));
		tabbedPane.setMaximumSize(new Dimension(32767, 300));
		add(tabbedPane, "2, 2, fill, fill");
		panelAppInfo = new JPanel();
		tabbedPane.addTab("Application Info", null, panelAppInfo, null);
		panelAppInfo
				.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec
						.decode("168px:grow"), }, new RowSpec[] {
						FormFactory.LINE_GAP_ROWSPEC,
						RowSpec.decode("default:grow"), }));
		scrollPaneAppInfo = new JScrollPane();
		panelAppInfo.add(scrollPaneAppInfo, "1, 2, fill, fill");
		textPaneAppInfo = new JTextPane();
		textPaneAppInfo.setEditable(false);
		textPaneAppInfo.setText(APP_INFO);
		scrollPaneAppInfo.setViewportView(textPaneAppInfo);
		panelDepInfo = new JPanel();
		tabbedPane.addTab("Dependency Information", null, panelDepInfo, null);
		panelDepInfo
				.setLayout(new FormLayout(new ColumnSpec[] {
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"), }, new RowSpec[] {
						FormFactory.LINE_GAP_ROWSPEC,
						RowSpec.decode("default:grow"), }));
		scrollPaneDepInfo = new JScrollPane();
		panelDepInfo.add(scrollPaneDepInfo, "2, 2, fill, fill");
		textPaneDepInfo = new JTextPane();
		textPaneDepInfo.setEditable(false);
		textPaneDepInfo.setContentType("text/html");
		textPaneDepInfo.setText(DEP_INFO);
		scrollPaneDepInfo.setViewportView(textPaneDepInfo);
	}
}
