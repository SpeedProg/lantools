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
package de.speedprog.lantools.modules.poll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.simpleframework.http.Form;
import org.simpleframework.http.Part;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import de.speedprog.lantools.LanTools;
import de.speedprog.lantools.modules.Module;
import de.speedprog.lantools.modules.ModuleContainer;
import de.speedprog.lantools.modules.datamodel.MenuModel;
import de.speedprog.lantools.modules.poll.freenmaker.FMPoll;
import de.speedprog.lantools.webserver.user.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class PollModule implements Module, ModuleContainer {
    private static final String BASE_PATH = "/poll";
    private static final String NAME = "Poll";
    private static final String PAR_ACTION = "action";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_LIST = "list";
    private static final String ACTION_VOTE_FORM = "voteform";
    private static final String ACTION_VOTE = "vote";
    private static final String ACTION_RESULT = "result";
    private static final String ACTION_POLL_FORM = "pollform";
    private static final String ACTION_CREATE_POLL = "createpoll";
    private Map<UUID, WebPoll> pollMap;
    private final Configuration cfg;
    private final List<Map<String, String>> menuData;
    private final File serializeFile;

    public PollModule() {
        final MenuModel menuModel = new MenuModel();
        menuModel.addLink("Home", "/");
        menuModel.addLink("Polls", BASE_PATH);
        menuModel.addLink("Create Poll", BASE_PATH + "?" + PAR_ACTION + "="
                + ACTION_POLL_FORM);
        menuModel.addLink("Poll List", BASE_PATH);
        menuData = menuModel.getMenuModel();
        cfg = LanTools.getFreeMakerConfig();
        new PollPanel();
        serializeFile = new File(".", "polls.obj");
        pollMap = null;
        ObjectInputStream ois = null;
        if (serializeFile.exists()) {
            try {
                ois = new ObjectInputStream(new FileInputStream(serializeFile));
                final Object dsObject = ois.readObject();
                if (dsObject instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    final Map<UUID, WebPoll> pollMap = (Map<UUID, WebPoll>) dsObject;
                    this.pollMap = pollMap;
                }
            } catch (final IOException | ClassNotFoundException e1) {
                e1.printStackTrace();
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (final IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        if (pollMap == null) {
            pollMap = Collections.synchronizedMap(new HashMap<UUID, WebPoll>());
        }
    }

    @Override
    public String getBasePath() {
        // TODO Auto-generated method stub
        return BASE_PATH;
    }

    @Override
    public ModuleContainer getModuleContainer() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public Icon getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return NAME;
    }

    @Override
    public JPanel getPanel() {
        return null;
    }

    @Override
    public String getTip() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void handle(final Request request, final Response response,
            final User user) {
        final Path path = request.getPath();
        final String pathString = path.toString();
        if (!pathString.startsWith(BASE_PATH)) {
            // TODO: Write error!
            try {
                response.close();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
        String relPathString = pathString.substring(BASE_PATH.length(),
                pathString.length());
        if (relPathString.equals("")) {
            relPathString = "/";
        }
        final OutputStream os;
        Template template = null;
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("menulinks", getMenuData());
        switch (relPathString) {
        case "/": {
            String action;
            try {
                action = request.getParameter("action");
            } catch (final IOException e1) {
                // TODO Auto-generated catch block
                sendError(response, "Error retreiving parameter.");
                e1.printStackTrace();
                return;
            }
            if (action == null) {
                action = "list";
            }
            switch (action) {
            case ACTION_DELETE: {
                String pollID;
                try {
                    pollID = request.getParameter("poll");
                } catch (final IOException e1) {
                    sendError(response, "Error retreiving parameter.");
                    e1.printStackTrace();
                    return;
                }
                final WebPoll poll;
                try {
                    poll = pollMap.get(UUID.fromString(pollID));
                } catch (final IllegalArgumentException e) {
                    sendError(response, "Poll ID invalid!");
                    return;
                }
                if (poll == null) {
                    sendError(response, "Poll ID invalid");
                    return;
                }
                try {
                    template = cfg.getTemplate("poll" + File.separator
                            + "poll_delete.ftl");
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    sendError(response, "Could not load Template.");
                    return;
                }
                if (!poll.getOwnerAddress().equals(
                        request.getClientAddress().getAddress())) {
                    dataMap.put("msg",
                            "You are not allowed to delete this vote, only the creator can delte votes!");
                    break;
                }
                final WebPoll webPoll = pollMap.remove(poll.getUuid());
                dataMap.put("msg", "Poll " + webPoll.getUuid().toString()
                        + " deleted.");
                dataMap.put(
                        "polls",
                        getFmPolls(pollMap.values(), request.getClientAddress()
                                .getAddress()));
            }
            break;
            case ACTION_VOTE_FORM: {
                String pollID;
                try {
                    pollID = request.getParameter("poll");
                } catch (final IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    sendError(response, "Error retreiving parameter.");
                    return;
                }
                final WebPoll poll;
                try {
                    poll = pollMap.get(UUID.fromString(pollID));
                } catch (final IllegalArgumentException e) {
                    sendError(response, "Poll ID invalid!");
                    return;
                }
                if (poll == null) {
                    sendError(response, "Poll ID invalid");
                    return;
                }
                dataMap.put(
                        "poll",
                        getFmPoll(poll, request.getClientAddress().getAddress()));
                final List<PollOption> options = new LinkedList<>();
                options.addAll(poll.getOptions());
                dataMap.put("action", BASE_PATH + "?action=" + ACTION_VOTE
                        + "&poll=" + poll.getUuid().toString());
                try {
                    template = cfg.getTemplate("poll" + File.separator
                            + "poll_vote_form.ftl");
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    sendError(response, "Template could not be loaded.");
                    return;
                }
            }
            break;
            case ACTION_VOTE: {
                Form form;
                try {
                    form = request.getForm();
                } catch (final IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    sendError(response, "Error retreiving parameters.");
                    return;
                }
                final List<Part> parts = form.getParts();
                final String pollIDString = form.get("poll");
                final UUID pollID;
                try {
                    pollID = UUID.fromString(pollIDString);
                } catch (final IllegalArgumentException e) {
                    sendError(response, "Poll ID not valid!");
                    return;
                }
                final List<Integer> optionsList = new LinkedList<>();
                final WebPoll poll = pollMap.get(pollID);
                if (poll == null) {
                    sendError(response, "Poll ID not valid!");
                    return;
                }
                if (!(poll.getVotes() > 1)) {
                    final String optionID = form.get("option");
                    try {
                        final Integer id = Integer.valueOf(optionID);
                        optionsList.add(id);
                    } catch (final NumberFormatException nfe) {
                    }
                } else {
                    for (final Part part : parts) {
                        if (part.getName().startsWith("option_")) {
                            String idString;
                            try {
                                idString = part.getContent();
                            } catch (final IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                sendError(response,
                                        "Could not reteive a parameter.");
                                return;
                            }
                            try {
                                final Integer id = Integer.valueOf(idString);
                                optionsList.add(id);
                            } catch (final NumberFormatException nfe) {
                                sendError(response, "Error: Invalid option id.");
                                return;
                            }
                        }
                    }
                }
                if (optionsList.size() > poll.getVotes()) {
                    sendError(response, "Invalid Number of Options choosen!");
                    return;
                }
                final List<PollOption> options = poll.getOptions();
                final Set<String> optionNameSet = new HashSet<String>(
                        options.size());
                for (final Integer id : optionsList) {
                    for (final PollOption option : options) {
                        if (option.getId() == id) {
                            optionNameSet.add(option.getName());
                        }
                    }
                }
                poll.vote(request.getClientAddress().getAddress(),
                        optionNameSet);
                savePollDataAsync();
                dataMap.put(
                        "poll",
                        getFmPoll(poll, request.getClientAddress().getAddress()));
                try {
                    template = cfg.getTemplate("poll" + File.separator
                            + "poll_voted.ftl");
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    sendError(response, "Template could not be loaded.");
                    return;
                }
            }
            break;
            case ACTION_RESULT: {
                String pollIDString;
                try {
                    pollIDString = request.getParameter("poll");
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    sendError(response, "Could not retreive poll id.");
                    return;
                }
                final UUID pollID = UUID.fromString(pollIDString);
                final WebPoll poll = pollMap.get(pollID);
                dataMap.put(
                        "poll",
                        getFmPoll(poll, request.getClientAddress().getAddress()));
                try {
                    template = cfg.getTemplate("poll" + File.separator
                            + "poll_result.ftl");
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    sendError(response, "Could not load Template.");
                    return;
                }
            }
            break;
            case ACTION_POLL_FORM: {
                response.set("Content-Type", "text/html");
                try {
                    template = cfg.getTemplate("poll" + File.separator
                            + "poll_create_form.ftl");
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    sendError(response, "Could not load Template.");
                    return;
                }
                dataMap.put("action", BASE_PATH + "?" + PAR_ACTION + "="
                        + ACTION_CREATE_POLL);
            }
            break;
            case ACTION_CREATE_POLL: {
                Form form;
                try {
                    form = request.getForm();
                } catch (final IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    sendError(response, "Could not retreive form value.");
                    return;
                }
                final List<Part> parts = form.getParts();
                String question = null;
                boolean enableIpFilter = false;
                String ipfilter = null;
                boolean oneVotePerIp = false;
                int votes = 0;
                final List<String> optionList = new LinkedList<>();
                try {
                    for (final Part part : parts) {
                        if (!part.isFile()) {
                            switch (part.getName()) {
                            case "question":
                                question = part.getContent();
                                break;
                            case "enableipfilter":
                                enableIpFilter = true;
                                break;
                            case "ipfilter":
                                ipfilter = part.getContent();
                                break;
                            case "onevoteperip":
                                oneVotePerIp = true;
                                break;
                            case "votes":
                                final String votesString = part.getContent();
                                votes = Integer.parseInt(votesString);
                                break;
                            default:
                                if (part.getName().startsWith("option_")) {
                                    final String optionString = part
                                            .getContent();
                                    if (optionString != null
                                            && !optionString.isEmpty()) {
                                        optionList.add(part.getContent());
                                    }
                                }
                                break;
                            }
                        }
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                    sendError(response, "Could not retreive a form option.");
                    return;
                }
                final UUID id = UUID.randomUUID();
                final WebPoll webPoll;
                try {
                    webPoll = new WebPoll(question, enableIpFilter,
                            Pattern.compile(ipfilter), oneVotePerIp, votes,
                            request.getClientAddress().getAddress(), id);
                } catch (final PatternSyntaxException e) {
                    sendError(response, "IP-Filter Pattern had a syntax error!");
                    return;
                }
                for (final String option : optionList) {
                    webPoll.addOption(new PollOption(option));
                }
                pollMap.put(id, webPoll);
                savePollDataAsync();
                synchronized (pollMap) {
                    dataMap.put(
                            "polls",
                            getFmPolls(pollMap.values(), request
                                    .getClientAddress().getAddress()));
                }
                dataMap.put(
                        "createdpoll",
                        getFmPoll(webPoll, request.getClientAddress()
                                .getAddress()));
                try {
                    template = cfg.getTemplate("poll" + File.separator
                            + "poll_created.ftl");
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    sendError(response, "Could not load Template.");
                    return;
                }
            }
            break;
            case ACTION_LIST:
            default: {
                response.set("Content-Type", "text/html");
                try {
                    template = cfg.getTemplate("poll" + File.separator
                            + "poll_list.ftl");
                } catch (final IOException e) {
                    // TODO: logging
                    sendError(response, "Could not open Template.");
                    e.printStackTrace();
                    return;
                }
                synchronized (pollMap) {
                    dataMap.put(
                            "polls",
                            getFmPolls(pollMap.values(), request
                                    .getClientAddress().getAddress()));
                }
            }
            }
        }
        break;
        default:
            sendError(response, "This Page does not exist!");
            return;
        }
        try {
            os = response.getOutputStream();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                response.close();
            } catch (final IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return;
        }
        try {
            template.process(dataMap, new OutputStreamWriter(os));
        } catch (final TemplateException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (final IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            response.close();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onClose() {
        savePollData();
    }

    private FMPoll getFmPoll(final WebPoll poll, final InetAddress address) {
        return FMPoll.createFromWebPoll(poll, address, BASE_PATH + "?"
                + PAR_ACTION + "=" + ACTION_VOTE_FORM + "&poll=", BASE_PATH
                + "?" + PAR_ACTION + "=" + ACTION_RESULT + "&poll=", BASE_PATH
                + "?" + PAR_ACTION + "=" + ACTION_DELETE + "&poll=");
    }

    private List<FMPoll> getFmPolls(final Collection<WebPoll> polls,
            final InetAddress address) {
        return FMPoll.createFromWebPoll(polls, address, BASE_PATH + "?"
                + PAR_ACTION + "=" + ACTION_VOTE_FORM + "&poll=", BASE_PATH
                + "?" + PAR_ACTION + "=" + ACTION_RESULT + "&poll=", BASE_PATH
                + "?" + PAR_ACTION + "=" + ACTION_DELETE + "&poll=");
    }

    private List<Map<String, String>> getMenuData() {
        return menuData;
    }

    private void savePollData() {
        synchronized (pollMap) {
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(
                        new FileOutputStream(serializeFile));
            } catch (final FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
            try {
                oos.writeObject(pollMap);
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                oos.close();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void savePollDataAsync() {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                savePollData();
            }
        })).start();
    }

    private void sendError(final Response response, final String msg) {
        Template template;
        try {
            template = cfg.getTemplate("error.ftl");
        } catch (final IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            try {
                final OutputStream oStream = response.getOutputStream();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                response.close();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("menulinks", getMenuData());
        dataMap.put("errormsg", msg);
        try {
            final OutputStreamWriter writer = new OutputStreamWriter(
                    response.getOutputStream());
            template.process(dataMap, writer);
            response.close();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final TemplateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
