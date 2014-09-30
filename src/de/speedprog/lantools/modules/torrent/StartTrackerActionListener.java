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
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

import com.turn.ttorrent.tracker.Tracker;

public class StartTrackerActionListener implements ActionListener {
    public static final String AC_START_TRACKER = "START_TRACKER";
    public static final String AC_STOP_TRACKER = "STOP_TRACKER";
    private static final String BASE_PATH = "/tracker";
    private final JFormattedTextField trackerPortTextField;
    private final JTextField trackerHostJTextField;
    private Tracker tracker;
    private final List<StartStopListener> listeners;

    public StartTrackerActionListener(final JFormattedTextField trackerPort,
            final JTextField trackerHostTextField) {
        listeners = Collections
                .synchronizedList(new LinkedList<StartStopListener>());
        this.trackerPortTextField = trackerPort;
        this.trackerHostJTextField = trackerHostTextField;
        this.tracker = null;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final String ac = e.getActionCommand();
        final JButton button = (JButton) e.getSource();
        switch (ac) {
        case AC_START_TRACKER:
            final int port = Integer.parseInt(trackerPortTextField.getText());
            try {
                tracker = new Tracker(new InetSocketAddress(port));
            } catch (final IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                tracker = null;
                return;
            }
            tracker.start();
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    loadTorrents(tracker);
                }
            })).start();
            button.setText("Stop Tracker");
            button.setActionCommand(AC_STOP_TRACKER);
            fireListenerStarted();
            break;
        case AC_STOP_TRACKER:
            tracker.stop();
            button.setText("Start Tracker");
            button.setActionCommand(AC_START_TRACKER);
            fireListenerStoped();
            break;
        default:
            break;
        }
    }

    public void addListener(final StartStopListener l) {
        listeners.add(l);
    }

    public Tracker getTracker() {
        return tracker;
    }

    public void removeListener(final StartStopListener l) {
        listeners.remove(l);
    }

    private void fireListenerStarted() {
        for (final StartStopListener listener : listeners) {
            listener.started();
        }
    }

    private void fireListenerStoped() {
        for (final StartStopListener listener : listeners) {
            listener.stoped();
        }
    }

    private void loadTorrents(final Tracker tracker) {
    }
}
