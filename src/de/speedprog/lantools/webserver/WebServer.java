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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.parse.PathParser;
import org.simpleframework.transport.connect.SocketConnection;

import de.speedprog.lantools.LanTools;
import de.speedprog.lantools.modules.ModuleContainer;
import de.speedprog.lantools.modules.datamodel.MenuModel;
import de.speedprog.lantools.webserver.user.User;
import de.speedprog.lantools.webserver.user.UserMapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class WebServer {
    private final SocketConnection sConnection;
    private final MainContainer mainContainer;
    private int port;
    private String host;
    private List<Map<String, String>> menuData;
    private final MenuModel menuModel;
    private final UserMapper userMapper;

    public WebServer() throws IOException {
        mainContainer = new MainContainer();
        sConnection = new SocketConnection(mainContainer);
        port = -1;
        host = "";
        menuModel = new MenuModel();
        menuModel.addLink("Home", "/");
        menuData = menuModel.getMenuModel();
        userMapper = new UserMapper();
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
        menuModel.addLink(name, basePath);
        menuData = menuModel.getMenuModel();
        return mainContainer.addContainer(basePath, name, container);
    }

    public void close() throws IOException {
        sConnection.close();
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

    public int getPort() {
        return port;
    }

    public ModuleContainer removeContainer(final String basePath) {
        // TODO: remove from menu
        return mainContainer.removeContainer(basePath);
    }

    private class MainContainer implements Container {
        private final ConcurrentHashMap<String, NamedModuleContainer> containerMap;

        public MainContainer() {
            containerMap = new ConcurrentHashMap<String, NamedModuleContainer>();
        }

        /**
         * Adds a Container under a given Path, only request going to that path
         * are
         * going to be relayed to the added container.
         *
         * @param basePath
         * @param container
         */
        public ModuleContainer addContainer(final String basePath,
                final String name, final ModuleContainer container) {
            final Path path = new PathParser(basePath);
            if (path.getSegments().length != 1) {
                throw new IllegalArgumentException(
                        "The base path of a container can only be 1 element long!");
            }
            return containerMap.put(path.toString(), new NamedModuleContainer(
                    name, container, basePath));
        }

        @Override
        public void handle(final Request req, final Response resp) {
            resp.set("Server", LanTools.VERSION_STRING);
            resp.setDate("Date", System.currentTimeMillis());
            final User user = userMapper.getUser(req.getClientAddress()
                    .getAddress());
            final Path reqPath = req.getPath();
            final String[] segmentStrings = reqPath.getSegments();
            if (user == null
                    && !(segmentStrings.length > 0 && segmentStrings[0]
                            .equals("html"))) {
                // html is direct file transfer for stylesheets and stuff
                if (reqPath.getPath().equals("/")) {
                    String usernameParam = null;
                    try {
                        usernameParam = req.getParameter("username");
                    } catch (final IOException e) {
                        e.printStackTrace();
                        resp.setCode(500);
                        try {
                            resp.close();
                        } catch (final IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        return;
                    }
                    if (usernameParam == null) {
                        resp.setCode(Status.TEMPORARY_REDIRECT.getCode());
                        resp.set("Location", "/setusername");
                        resp.setContentLength(0);
                        try {
                            resp.close();
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    } else {
                        userMapper.setUsername(req.getClientAddress()
                                .getAddress(), usernameParam);
                        resp.setCode(Status.TEMPORARY_REDIRECT.getCode());
                        resp.set("Location", "/");
                        resp.setContentLength(0);
                        try {
                            resp.close();
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                } else if (reqPath.getPath().equals("/setusername")) {
                    Template template = null;
                    try {
                        template = LanTools.getFreeMakerConfig().getTemplate(
                                "main/setusername.ftl");
                    } catch (final IOException e) {
                        e.printStackTrace();
                        resp.setCode(404);
                        try {
                            resp.close();
                        } catch (final IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        return;
                    }
                    final Map<String, Object> data = new HashMap<>();
                    data.put("menulinks", menuData);
                    try {
                        template.process(data,
                                new OutputStreamWriter(resp.getOutputStream()));
                    } catch (final TemplateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        resp.setCode(500);
                    } catch (final IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        resp.setCode(500);
                    }
                    try {
                        resp.close();
                    } catch (final IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return;
                } else {
                    resp.setCode(Status.TEMPORARY_REDIRECT.getCode());
                    resp.set("Location", "/setusername");
                    resp.setContentLength(0);
                    try {
                        resp.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            if (segmentStrings.length == 0) {
                // list containers!
                sendContainerList(resp);
            } else if (segmentStrings.length > 0) { // should always go to == 0 or here
                if (segmentStrings[0].equals("html")) { // send the files :)
                    // build the path
                    final java.nio.file.Path path = Paths.get(".",
                            segmentStrings);
                    final File file = path.toFile();
                    if (file.isFile()) {
                        String cTypeString;
                        try {
                            cTypeString = java.nio.file.Files
                                    .probeContentType(path);
                        } catch (final IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                            cTypeString = "text/*";
                        }
                        resp.set("Content-Type", cTypeString);
                        try {
                            final WritableByteChannel wChannel = resp
                                    .getByteChannel();
                            final FileInputStream fis = new FileInputStream(
                                    file);
                            final FileChannel fileChannel = fis.getChannel();
                            final long size = fileChannel.size();
                            if (size <= Integer.MAX_VALUE) {
                                resp.setContentLength((int) size);
                            }
                            fileChannel.transferTo(0, size, wChannel);
                            fileChannel.close();
                            fis.close();
                            wChannel.close();
                            resp.close();
                        } catch (final IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        // send http error! We do not list dirs!
                        try {
                            resp.close();
                        } catch (final IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {
                    final NamedModuleContainer container = containerMap
                            .get(reqPath.getDirectory());
                    if (container != null) {
                        container.handle(req, resp, user);
                    } else {
                        try {
                            resp.close();
                        } catch (final IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        // TODO: send error
                    }
                }
            }
        }

        public ModuleContainer removeContainer(final String basePath) {
            return containerMap.remove(basePath);
        }

        private void sendContainerList(final Response response) {
            try {
                response.set("Content-Type", "text/html");
                final Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap.put("menulinks", menuData);
                final List<NamedModuleContainer> containers = new ArrayList<>(
                        containerMap.values());
                dataMap.put("modules", containers);
                final Template template = LanTools.getFreeMakerConfig()
                        .getTemplate("containerlist.ftl");
                template.process(dataMap,
                        new OutputStreamWriter(response.getOutputStream()));
                response.close();
            } catch (final Exception e) {
                e.printStackTrace();
                try {
                    response.close();
                } catch (final IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    }
}
