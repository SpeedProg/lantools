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
package de.speedprog.lantools.modules.torrent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.turn.ttorrent.common.Torrent;

import de.speedprog.lantools.modules.ModuleSettings;
import de.speedprog.lantools.webserver.WebServer;

public class TorrentPanel extends JPanel {
    private JFormattedTextField ftfTrackerPort;
    private JTextField textFieldTrackerHost;
    private JButton buttonStartTracker;
    private final StartTrackerActionListener startTrackerActionListener;
    private final WebServer webServer;
    private final TorrentService torrentService;
    private final JButton btnCreateTorrent;

    /**
     * Create the panel.
     */
    public TorrentPanel(final WebServer webServer,
            final TorrentService torrentService, final ModuleSettings settings) {
        this.webServer = webServer;
        this.torrentService = torrentService;
        initialize(settings);
        startTrackerActionListener = new StartTrackerActionListener(
                ftfTrackerPort, textFieldTrackerHost);
        startTrackerActionListener.addListener(new TrackerStartStopListener());
        buttonStartTracker
        .setActionCommand(StartTrackerActionListener.AC_START_TRACKER);
        btnCreateTorrent = new JButton("Create Torrent");
        btnCreateTorrent
        .addActionListener(new BtnCreateTorrentActionListener());
        add(btnCreateTorrent, "2, 6, 5, 1");
        buttonStartTracker.addActionListener(startTrackerActionListener);
    }

    public void onClose(final ModuleSettings settings) {
        settings.putString(TorrentTrackerModule.SETTING_TRACKERPORT,
                ftfTrackerPort.getText());
        settings.putString(TorrentTrackerModule.SETTING_TRACKERHOST,
                textFieldTrackerHost.getText());
    }

    private void initialize(final ModuleSettings settings) {
        String defPortString = settings
                .getString(TorrentTrackerModule.SETTING_TRACKERPORT);
        if (defPortString == null) {
            defPortString = "6969";
        }
        String defHostString = settings
                .getString(TorrentTrackerModule.SETTING_TRACKERHOST);
        if (defHostString == null) {
            try {
                defHostString = InetAddress.getLocalHost().getHostName();
            } catch (final UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                defHostString = "";
            }
        }
        setLayout(new FormLayout(
                new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC,
                        FormFactory.RELATED_GAP_COLSPEC,
                        ColumnSpec.decode("default:grow"),
                        FormFactory.RELATED_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC, }));
        final JLabel labelTrackerPort = new JLabel("Tracker Port:");
        add(labelTrackerPort, "2, 2, right, default");
        ftfTrackerPort = new JFormattedTextField();
        ftfTrackerPort.setText(defPortString);
        add(ftfTrackerPort, "4, 2, fill, default");
        buttonStartTracker = new JButton("Start Tracker");
        buttonStartTracker.setActionCommand("START_TRACKER");
        add(buttonStartTracker, "6, 2");
        final JLabel labelTrackerHost = new JLabel("Tracker Host:");
        add(labelTrackerHost, "2, 4, right, default");
        textFieldTrackerHost = new JTextField();
        textFieldTrackerHost.setText(defHostString);
        textFieldTrackerHost.setColumns(10);
        add(textFieldTrackerHost, "4, 4, fill, default");
    }

    private class BtnCreateTorrentActionListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            final JButton button = (JButton) e.getSource();
            button.setText("Creating torrent...");
            button.setEnabled(false);
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    final JFileChooser fileChooser = new JFileChooser();
                    fileChooser
                    .setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    fileChooser.setMultiSelectionEnabled(false);
                    int opt = fileChooser.showOpenDialog(null);
                    if (opt != JFileChooser.APPROVE_OPTION) {
                        onEnd();
                        return;
                    }
                    final File file = fileChooser.getSelectedFile();
                    Torrent torrent = null;
                    if (file.isDirectory()) {
                        // multifile torrent
                        try {
                            final List<File> files = new ArrayList<>();
                            Files.walkFileTree(file.toPath(),
                                    new FileVisitor<Path>() {
                                        @Override
                                        public FileVisitResult postVisitDirectory(
                                                final Path dir,
                                                final IOException exc)
                                                throws IOException {
                                            return FileVisitResult.CONTINUE;
                                        }

                                        @Override
                                        public FileVisitResult preVisitDirectory(
                                                final Path dir,
                                                final BasicFileAttributes attrs)
                                                throws IOException {
                                            // TODO Auto-generated method stub
                                            return FileVisitResult.CONTINUE;
                                        }

                                        @Override
                                        public FileVisitResult visitFile(
                                                final Path file,
                                                final BasicFileAttributes attrs)
                                                throws IOException {
                                            files.add(file.toFile());
                                            return FileVisitResult.CONTINUE;
                                        }

                                        @Override
                                        public FileVisitResult visitFileFailed(
                                                final Path file,
                                                final IOException exc)
                                                throws IOException {
                                            return FileVisitResult.CONTINUE;
                                        }
                                    });
                            torrent = Torrent.create(file, files, new URI(
                                    "http://" + textFieldTrackerHost.getText()
                                            + ":" + ftfTrackerPort.getText()
                                            + "/announce"), "LanTools");
                        } catch (final InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (final IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (final URISyntaxException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    } else {
                        try {
                            torrent = Torrent.create(file, new URI("http://"
                                    + textFieldTrackerHost.getText() + ":"
                                    + ftfTrackerPort.getText() + "/announce"),
                                    "LanTools");
                        } catch (final InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (final IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (final URISyntaxException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                    if (torrent == null) {
                        onEnd();
                        return;
                    }
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    opt = fileChooser.showSaveDialog(null);
                    if (opt != JFileChooser.APPROVE_OPTION) {
                        onEnd();
                        return;
                    }
                    final File tFile = fileChooser.getSelectedFile();
                    try {
                        torrent.save(new FileOutputStream(tFile));
                    } catch (final FileNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (final IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    onEnd();
                }

                private void onEnd() {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            button.setText("Create torrent");
                            button.setEnabled(true);
                        }
                    });
                }
            })).start();
        }
    }

    private class TrackerStartStopListener implements StartStopListener {
        @Override
        public void started() {
            torrentService.setTracker(startTrackerActionListener.getTracker());
            torrentService.setTrackerHost(textFieldTrackerHost.getText());
            torrentService.setTrackerPort(ftfTrackerPort.getText());
        }

        @Override
        public void stoped() {
            torrentService.setTracker(null);
        }
    }
}
