/*
Copyright 2014 Constantin Wenger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package de.speedprog.lantools.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import tk.speedprog.utils.Settings;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

import de.speedprog.lantools.LanTools;
import de.speedprog.lantools.modules.Module;
import de.speedprog.lantools.modules.ModuleContainer;
import de.speedprog.lantools.modules.about.AboutModule;
import de.speedprog.lantools.modules.notices.NoticesModule;
import de.speedprog.lantools.modules.poll.PollModule;
import de.speedprog.lantools.modules.torrent.TorrentTrackerModule;
import de.speedprog.lantools.webserver.StartWebserverActionListener;
import de.speedprog.lantools.webserver.WebServer;

public class LanToolWindow {
    private static final String SETTINGS_WEBPORT = "web_port";
    private static final String SETTINGS_WEBHOST = "web_host";
    private static final String SETTINGS_CUSTOMHTML = "custom_html";
    private JFrame frmLantorrenttracker;
    private JTabbedPane tabbedPane;
    private JPanel panelSettings;
    private JLabel labelWebPort;
    private JFormattedTextField ftfWebPort;
    private JButton btnStartWebserver;
    private final WebServer webServer;
    private JTextField textFieldWebServerHostName;
    private final Settings settings;
    private final Collection<Module> modules;
    private final JButton btnClearUsers;
    private final JTextArea textAreaCustomHTML;

    /**
     * Create the application.
     *
     * @throws IOException
     */
    public LanToolWindow() throws IOException {
        settings = LanTools.getSettings();
        webServer = new WebServer();
        btnClearUsers = new JButton("Clear Users");
        textAreaCustomHTML = new JTextArea();
        initialize();
        modules = getModules();
        for (final Module module : modules) {
            final JPanel panel = module.getPanel();
            if (panel == null) {
                continue;
            }
            tabbedPane.addTab(module.getName(), module.getIcon(), panel,
                    module.getTip());
        }
        for (final Module module : modules) {
            final ModuleContainer container = module.getModuleContainer();
            final String basePathString = module.getBasePath();
            if (container == null || basePathString == null) {
                continue;
            }
            webServer.addContainer(module.getBasePath(), module.getName(),
                    module.getModuleContainer());
        }
        btnStartWebserver.addActionListener(new StartWebserverActionListener(
                webServer, ftfWebPort, textFieldWebServerHostName));
        btnStartWebserver
                .setActionCommand(StartWebserverActionListener.AC_START);
        btnClearUsers.addActionListener(new BtnClearUsersActionListener());
        textAreaCustomHTML.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void changedUpdate(final DocumentEvent e) {
                    }

                    @Override
                    public void insertUpdate(final DocumentEvent e) {
                        update(e.getDocument());
                    }

                    @Override
                    public void removeUpdate(final DocumentEvent e) {
                        update(e.getDocument());
                    }

                    private void update(final Document doc) {
                        try {
                            webServer.setCustomHTML(doc.getText(0,
                                    doc.getLength()));
                        } catch (final BadLocationException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
        frmLantorrenttracker.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                settings.storeString(SETTINGS_WEBHOST,
                        textFieldWebServerHostName.getText());
                settings.storeString(SETTINGS_WEBPORT, ftfWebPort.getText());
                settings.storeString(SETTINGS_CUSTOMHTML,
                        textAreaCustomHTML.getText());
                for (final Module module : modules) {
                    module.onClose();
                }
                try {
                    webServer.close();
                } catch (final IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                super.windowClosing(e);
            }
        });
        frmLantorrenttracker.pack();
    }

    public void setVisible(final boolean v) {
        frmLantorrenttracker.setVisible(v);
    }

    private void addTorrent(final File f, final Tracker tracker)
            throws IOException {
        tracker.announce(TrackedTorrent.load(f));
    }

    private Collection<Module> getModules() {
        final LinkedList<Module> modules = new LinkedList<>();
        modules.add(new TorrentTrackerModule(null, webServer));
        modules.add(new PollModule(null));
        modules.add(new AboutModule());
        modules.add(new NoticesModule(webServer.getMainContainer()));
        return modules;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        String defWebPortString = settings.getString(SETTINGS_WEBPORT);
        if (defWebPortString == null) {
            defWebPortString = "80";
        }
        String defWebHostString = settings.getString(SETTINGS_WEBHOST);
        if (defWebHostString == null) {
            try {
                defWebHostString = InetAddress.getLocalHost().getHostName();
            } catch (final UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                defWebHostString = "";
            }
        }
        String customHTML = settings.getString(SETTINGS_CUSTOMHTML);
        if (customHTML == null) {
            customHTML = "<p>You can place your custom message here!</p>";
        }
        frmLantorrenttracker = new JFrame();
        frmLantorrenttracker.setMaximumSize(new Dimension(300, 500));
        frmLantorrenttracker.setTitle("LanTools v" + LanTools.VERSION_STRING
                + " b" + LanTools.VERSION_NUM);
        frmLantorrenttracker.setBounds(100, 100, 450, 300);
        frmLantorrenttracker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmLantorrenttracker.getContentPane().setLayout(new BorderLayout(0, 0));
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frmLantorrenttracker.getContentPane().add(tabbedPane);
        panelSettings = new JPanel();
        tabbedPane.addTab("Settings", null, panelSettings, null);
        panelSettings.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC, },
                new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"), }));
        labelWebPort = new JLabel("Server Port:");
        panelSettings.add(labelWebPort, "2, 2, right, default");
        ftfWebPort = new JFormattedTextField();
        ftfWebPort.setText(defWebPortString);
        panelSettings.add(ftfWebPort, "4, 2, fill, default");
        btnStartWebserver = new JButton("Start Webserver");
        panelSettings.add(btnStartWebserver, "6, 2");
        final JLabel lblServerHost = new JLabel("Server Host:");
        panelSettings.add(lblServerHost, "2, 4, right, default");
        textFieldWebServerHostName = new JTextField();
        textFieldWebServerHostName.setText(defWebHostString);
        panelSettings.add(textFieldWebServerHostName, "4, 4, fill, default");
        textFieldWebServerHostName.setColumns(10);
        panelSettings.add(btnClearUsers, "6, 4");
        final JLabel lblCustomHtml = new JLabel("Custom HTML:");
        panelSettings.add(lblCustomHtml, "2, 6");
        final JScrollPane scrollPane = new JScrollPane();
        panelSettings.add(scrollPane, "4, 6, 3, 1, fill, fill");
        textAreaCustomHTML.setText(customHTML);
        webServer.setCustomHTML(customHTML);
        scrollPane.setViewportView(textAreaCustomHTML);
    }

    private class BtnClearUsersActionListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            webServer.clearUsers();
            for (final Module module : modules) {
                module.usersCleared();
            }
        }
    }
}
