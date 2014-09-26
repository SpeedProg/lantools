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
package de.speedprog.lantools.webserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.simpleframework.transport.connect.SocketConnection;

import de.speedprog.lantools.modules.ModuleContainer;

public class WebServer {
    private final SocketConnection sConnection;
    private final MainContainer mainContainer;
    private int port;
    private String host;

    public WebServer() throws IOException {
        mainContainer = new MainContainer();
        sConnection = new SocketConnection(mainContainer);
        port = -1;
        host = "";
    }

    /**
     * Adds a Container under a given Path, only request going to that path are
     * going to be relayed to the added container.
     *
     * @param basePath
     * @param container
     */
    public ModuleContainer addContainer(final String basePath,
            final String name, final ModuleContainer container) {
        return mainContainer.addContainer(basePath, name, container);
    }

    public void clearUsers() {
        mainContainer.clearUsers();
    }

    public void close() throws IOException {
        sConnection.close();
        mainContainer.close();
    }

    public void connect(final InetSocketAddress address, final String hostName)
            throws IOException {
        port = address.getPort();
        host = hostName;
        sConnection.connect(address);
    }

    public String getHost() {
        return host;
    }

    public MainContainer getMainContainer() {
        return mainContainer;
    }

    public int getPort() {
        return port;
    }

    public ModuleContainer removeContainer(final String basePath) {
        // TODO: remove from menu
        return mainContainer.removeContainer(basePath);
    }
}
