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

import javax.swing.Icon;
import javax.swing.JPanel;

import org.simpleframework.http.core.Container;

import de.speedprog.lantools.LanTools;
import de.speedprog.lantools.modules.Module;
import de.speedprog.lantools.modules.ModuleSettings;
import de.speedprog.lantools.webserver.WebServer;

public class TorrentTrackerModule implements Module {
    protected static final String SETTING_TRACKERPORT = "tracker_port";
    protected static final String SETTING_TRACKERHOST = "tracker_host";
    private static final String DEF_BASE_PATH = "/ttracker/";
    private static final String TAB_NAME = "Torrent Tracker";
    private final TorrentPanel torrentPanel;
    private final TorrentService torrentService;
    private final String basePath;

    public TorrentTrackerModule(final String basePath, final WebServer webServer) {
        if (basePath == null) {
            this.basePath = DEF_BASE_PATH;
        } else {
            this.basePath = basePath;
        }
        torrentService = new TorrentService(this.basePath, null, null, null,
                webServer);
        torrentPanel = new TorrentPanel(torrentService,
                LanTools.getSettingsFor(this.basePath));
    }

    @Override
    public String getBasePath() {
        return basePath;
    }

    @Override
    public Container getContainer() {
        // TODO Auto-generated method stub
        return torrentService;
    }

    @Override
    public Icon getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return TAB_NAME;
    }

    @Override
    public JPanel getPanel() {
        return torrentPanel;
    }

    @Override
    public String getTip() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onClose() {
        final ModuleSettings moduleSettings = LanTools.getSettingsFor(basePath);
        torrentPanel.onClose(moduleSettings);
    }
}
