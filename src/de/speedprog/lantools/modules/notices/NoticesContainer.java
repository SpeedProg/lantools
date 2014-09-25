package de.speedprog.lantools.modules.notices;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.simpleframework.http.Form;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;

import de.speedprog.lantools.LanTools;
import de.speedprog.lantools.modules.ModuleContainer;
import de.speedprog.lantools.modules.datamodel.MenuModel;
import de.speedprog.lantools.webserver.user.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class NoticesContainer implements ModuleContainer {
    private final Map<UUID, NoticeBoard> boardMap;
    private final String basePath;
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_BOARDID = "boardid";
    private static final String ACTION_SHOW_BOARD = "board";
    private static final String ACTION_NEW_BOARD = "newboard";
    private static final String ACTION_NEW_NOTICE = "newnotice";
    private static final Configuration CFG = LanTools.getFreeMakerConfig();
    private static final Logger LOGGER = Logger
            .getLogger(NoticesContainer.class.getName());
    private final List<Map<String, String>> menuData;
    private boolean generated;

    public NoticesContainer(final String basePath) {
        this.boardMap = new ConcurrentHashMap<>();
        this.basePath = basePath;
        final MenuModel menuModel = new MenuModel();
        menuModel.addLink("Home", "/");
        menuModel.addLink("Notice Boards", basePath);
        this.menuData = menuModel.getMenuModel();
        this.generated = false;
    }

    @Override
    public void handle(final Request request, final Response response,
            final User user) {
        try {
            System.out.println("Notices Container!");
            if (!generated) {
                for (int i = 0; i < 10; i++) {
                    final NoticeBoard board = new NoticeBoard(user,
                            "Test " + i, "Desc " + i);
                    for (int n = 0; n < 10; n++) {
                        final BoardEntry entry = new BoardEntry(user,
                                "Entry Title: " + n, "Entry Content " + n);
                        board.addBoardEntry(entry);
                    }
                    boardMap.put(board.getId(), board);
                }
                generated = true;
            }
            System.out.println("Get Paths!");
            final Path path = request.getPath();
            final String pathString = path.getDirectory();
            System.out.println("Path String: " + pathString);
            if (!pathString.startsWith(basePath)) {
                return;
            }
            final String relPathString;
            if (pathString.equals(basePath)) {
                relPathString = "/";
            } else {
                relPathString = pathString.substring(basePath.length(),
                        pathString.length() - 1);
            }
            System.out.println("RelPath: " + relPathString);
            // we make this one param based
            if (relPathString.equals("/")) {
                String action = null;
                try {
                    action = request.getParameter(PARAM_ACTION);
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    sendInternalServerError(response, e);
                    return;
                }
                final Template template = null;
                final Map<String, Object> data = new HashMap<String, Object>();
                data.put("basepath", basePath);
                data.put("param_action", PARAM_ACTION);
                data.put("param_boardid", PARAM_BOARDID);
                data.put("menulinks", menuData);
                data.put("user", user);
                data.put("a_show_board", ACTION_SHOW_BOARD);
                data.put("a_new_board", ACTION_NEW_BOARD);
                data.put("a_new_notice", ACTION_NEW_NOTICE);
                if (action == null) {
                    System.out.println("Action is null");
                    handleBoardListView(request, response, user, data);
                    return;
                } else {
                    switch (action) {
                    case ACTION_SHOW_BOARD:
                        System.out.println("Action Board view.");
                        handleBoardView(request, response, user, data);
                        break;
                    case ACTION_NEW_BOARD:
                        handleBoardCreate(request, response, user, data);
                        break;
                    case ACTION_NEW_NOTICE:
                        handleNoticeCreate(request, response, user, data);
                        break;
                    default:
                        response.setCode(Status.NOT_IMPLEMENTED.getCode());
                        response.setContentLength(0);
                        try {
                            response.close();
                        } catch (final IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    }
                    return;
                }
            } else {
                response.setCode(Status.NOT_FOUND.getCode());
                response.set("Location", basePath);
                response.setContentLength(0);
                try {
                    response.close();
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    sendInternalServerError(response, e);
                    return;
                }
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    private void handleBoardCreate(final Request request,
            final Response response, final User user,
            final Map<String, Object> data) {
        final org.simpleframework.http.Form form;
        try {
            form = request.getForm();
        } catch (final IOException e) {
            sendInternalServerError(response, e);
            return;
        }
        final String boardName = form.get("name");
        final String boardDesc = form.get("desc");
        if (boardName == null || boardDesc == null) {
            sendBadRequest(response);
            return;
        }
        final NoticeBoard board = new NoticeBoard(user, boardName, boardDesc);
        boardMap.put(board.getId(), board);
        handleBoardListView(request, response, user, data); // show list of boards
    }

    private void handleBoardListView(final Request request,
            final Response response, final User user,
            final Map<String, Object> data) {
        try {
            final Template template = CFG.getTemplate("/notices/index.ftl");
            data.put("boards", boardMap.values());
            process(template, data, response);
        } catch (final IOException e) {
            sendInternalServerError(response, e);
            return;
        }
    }

    private void handleBoardView(final Request request,
            final Response response, final User user,
            final Map<String, Object> data) {
        final String boardId;
        try {
            boardId = request.getParameter("boardid");
        } catch (final IOException e) {
            sendInternalServerError(response, e);
            return;
        }
        if (boardId == null) {
            sendBadRequest(response);
            return;
        }
        final NoticeBoard board;
        try {
            board = boardMap.get(UUID.fromString(boardId));
        } catch (final IllegalArgumentException e) {
            sendBadRequest(response);
            return;
        }
        if (board == null) {
            sendBadRequest(response);
            return;
        }
        data.put("board", board);
        final Template template;
        try {
            template = CFG.getTemplate("/notices/board.ftl");
        } catch (final IOException e) {
            sendInternalServerError(response, e);
            return;
        }
        process(template, data, response);
        return;
    }

    private void handleNoticeCreate(final Request request,
            final Response response, final User user,
            final Map<String, Object> data) {
        Form form;
        try {
            form = request.getForm();
        } catch (final IOException e) {
            sendInternalServerError(response, e);
            return;
        }
        final String noticeTitle = form.get("title");
        final String noticeContent = form.get("content");
        final String boardIdString = form.get(PARAM_BOARDID);
        if (noticeTitle == null || noticeContent == null
                || boardIdString == null) {
            sendBadRequest(response);
            return;
        }
        UUID boardID;
        try {
            boardID = UUID.fromString(boardIdString);
        } catch (final IllegalArgumentException e) {
            sendBadRequest(response);
            return;
        }
        final NoticeBoard board = boardMap.get(boardID);
        if (board == null) {
            sendBadRequest(response);
            return;
        }
        board.addBoardEntry(new BoardEntry(user, noticeTitle, noticeContent));
        // we can do this since the param boardid is the same
        handleBoardView(request, response, user, data);
    }

    private void process(final Template template,
            final Map<String, Object> data, final Response response) {
        try {
            template.process(data,
                    new OutputStreamWriter(response.getOutputStream()));
        } catch (final TemplateException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Template Error.", e);
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "IO Error processing template.", e);
        }
        try {
            response.close();
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Error closing response.", e);
        }
    }

    private void sendBadRequest(final Response response) {
        response.setCode(Status.BAD_REQUEST.getCode());
        response.setContentLength(0);
        try {
            response.close();
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Error Send Bad Request Message.", e);
        }
    }

    private void sendInternalServerError(final Response response,
            final Throwable throwable) {
        if (throwable != null) {
            LOGGER.log(Level.SEVERE, "Internal Server Error.", throwable);
        }
        response.setCode(Status.INTERNAL_SERVER_ERROR.getCode());
        response.setContentLength(0);
        try {
            response.close();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Error sending internal server error.", e);
        }
    }
}
