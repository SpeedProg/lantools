package de.speedprog.lantools.modules.notices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
import de.speedprog.lantools.modules.MenuEntry;
import de.speedprog.lantools.modules.ModuleContainer;
import de.speedprog.lantools.webserver.MainContainer;
import de.speedprog.lantools.webserver.user.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class NoticesContainer implements ModuleContainer {
	private Map<UUID, NoticeBoard> boardMap;
	private final String basePath;
	private static final String PARAM_ACTION = "action";
	private static final String PARAM_BOARDID = "boardid";
	private static final String ACTION_SHOW_BOARD = "board";
	private static final String ACTION_NEW_BOARD = "newboard";
	private static final String ACTION_NEW_NOTICE = "newnotice";
	private static final String ACTION_DEL_NOTICE = "del_notice";
	private static final String ACTION_AJAX_NOTICES = "ajax_notices";
	private static final Configuration CFG = LanTools.getFreeMakerConfig();
	private static final Logger LOGGER = Logger
			.getLogger(NoticesContainer.class.getName());
	private final java.nio.file.Path cfgDirPath;
	private static final String BOARDMAPFILENAME_STRING = "boardMap.obj";
	private final File boardMapFile;
	private final MainContainer mainContainer;
	private final List<MenuEntry> menu;

	public NoticesContainer(final MainContainer container, final String basePath) {
		this.mainContainer = container;
		this.basePath = basePath;
		this.menu = new ArrayList<MenuEntry>();
		this.cfgDirPath = LanTools.getModuleConfigPath(this.basePath);
		this.boardMapFile = cfgDirPath.resolve(BOARDMAPFILENAME_STRING)
				.toFile();
		ObjectInputStream ois = null;
		if (this.boardMapFile.exists()) {
			try {
				ois = new ObjectInputStream(new FileInputStream(
						this.boardMapFile));
				final Object dsObject = ois.readObject();
				if (dsObject instanceof ConcurrentHashMap<?, ?>) {
					@SuppressWarnings("unchecked")
					final Map<UUID, NoticeBoard> boardMap = (ConcurrentHashMap<UUID, NoticeBoard>) dsObject;
					this.boardMap = boardMap;
				}
			} catch (final IOException | ClassNotFoundException e1) {
				LOGGER.log(Level.FINE, "Error reading boardMap from file.", e1);
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (final IOException e) {
						LOGGER.log(Level.FINE, "Error closing file.", e);
					}
				}
			}
		}
		if (this.boardMap == null) {
			this.boardMap = new ConcurrentHashMap<>();
		}
	}

	public void close() {
		final ObjectOutputStream outputStream;
		try {
			outputStream = new ObjectOutputStream(new FileOutputStream(
					boardMapFile));
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, "Error opening file to save notice data.",
					e);
			return;
		}
		try {
			outputStream.writeObject(boardMap);
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, "Error saving notice data.", e);
		} finally {
			try {
				outputStream.close();
			} catch (final IOException e) {
				LOGGER.log(Level.SEVERE,
						"Error closing stream to save notice data.", e);
			}
		}
	}

	@Override
	public void handle(final Request request, final Response response,
			final User user, List<MenuEntry> menu) {
		try {
			final Path path = request.getPath();
			final String pathString = path.getDirectory();
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
			// we make this one param based
			if (relPathString.equals("/")) {
				String action = null;
				try {
					action = request.getParameter(PARAM_ACTION);
				} catch (final IOException e) {
					sendInternalServerError(response, e);
					return;
				}
				final Template template = null;
				final Map<String, Object> data = new HashMap<String, Object>();
				data.put("basepath", basePath);
				data.put("param_action", PARAM_ACTION);
				data.put("param_boardid", PARAM_BOARDID);
				data.put("menu", menu);
				data.put("user", user);
				data.put("a_show_board", ACTION_SHOW_BOARD);
				data.put("a_new_board", ACTION_NEW_BOARD);
				data.put("a_new_notice", ACTION_NEW_NOTICE);
				data.put("a_del_notice", ACTION_DEL_NOTICE);
				data.put("a_ajax_notices", ACTION_AJAX_NOTICES);
				data.put("userMapper", mainContainer.getUserMapper());
				if (action == null) {
					handleBoardListView(request, response, user, data);
					return;
				} else {
					switch (action) {
					case ACTION_SHOW_BOARD:
						handleBoardView(request, response, user, data);
						break;
					case ACTION_NEW_BOARD:
						handleBoardCreate(request, response, user, data);
						break;
					case ACTION_NEW_NOTICE:
						handleNoticeCreate(request, response, user, data);
						break;
					case ACTION_DEL_NOTICE:
						handleNoticeDelete(request, response, user, data);
						break;
					case ACTION_AJAX_NOTICES:
						handleAjaxNotices(request, response, user, data);
						break;
					default:
						response.setCode(Status.NOT_IMPLEMENTED.getCode());
						response.setContentLength(0);
						try {
							response.close();
						} catch (final IOException e) {
							LOGGER.log(Level.SEVERE,
									"Error sending notimplemted code.", e);
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
					sendInternalServerError(response, e);
					return;
				}
			}
		} catch (final Exception exception) {
			exception.printStackTrace();
		}
	}

	public void usersCleared() {
		boardMap.clear();
	}

	private void handleAjaxNotices(final Request request,
			final Response response, final User user,
			final Map<String, Object> data) {
		final String boardidString;
		try {
			boardidString = request.getParameter(PARAM_BOARDID);
		} catch (final IOException e1) {
			sendBadRequest(response);
			return;
		}
		if (boardidString == null) {
			sendBadRequest(response);
			return;
		}
		final UUID boardID;
		try {
			boardID = UUID.fromString(boardidString);
		} catch (final IllegalArgumentException e) {
			sendBadRequest(response);
			return;
		}
		final NoticeBoard board = boardMap.get(boardID);
		data.put("board", board);
		Template template;
		try {
			template = CFG.getTemplate("/notices/ajax_notices.ftl");
		} catch (final IOException e) {
			sendInternalServerError(response, e);
			return;
		}
		process(template, data, response);
	}

	private void handleBoardCreate(final Request request,
			final Response response, final User user,
			final Map<String, Object> data) {
		final Form form;
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
		final NoticeBoard board = new NoticeBoard(user.getInetAddress(),
				boardName, boardDesc);
		boardMap.put(board.getId(), board);
		handleBoardListView(request, response, user, data); // show list of
															// boards
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
		board.addBoardEntry(new BoardEntry(user.getInetAddress(), noticeTitle,
				noticeContent));
		// redirect to board
		response.setCode(Status.SEE_OTHER.getCode());
		response.set("Location", basePath + "?" + PARAM_ACTION + "="
				+ ACTION_SHOW_BOARD + "&" + PARAM_BOARDID + "=" + boardIdString);
		response.setContentLength(0);
		try {
			response.close();
		} catch (final IOException e) {
			LOGGER.log(Level.INFO, "Error sending answer to notice create.", e);
		}
	}

	private void handleNoticeDelete(final Request request,
			final Response response, final User user,
			final Map<String, Object> data) {
		final Form form;
		try {
			form = request.getForm();
		} catch (final IOException e) {
			sendInternalServerError(response, e);
			return;
		}
		final String boardidString = form.get("boardid");
		final String noticeidString = form.get("noticeid");
		if (boardidString == null | noticeidString == null) {
			sendBadRequest(response);
			return;
		}
		final UUID boardID;
		final UUID noticeID;
		try {
			boardID = UUID.fromString(boardidString);
			noticeID = UUID.fromString(noticeidString);
		} catch (final IllegalArgumentException e) {
			sendBadRequest(response);
			return;
		}
		final NoticeBoard board = boardMap.get(boardID);
		if (board == null) {
			sendBadRequest(response);
			return;
		}
		board.removeBoardEntry(noticeID);
		// redirect to board
		response.setCode(Status.SEE_OTHER.getCode());
		response.set("Location", basePath + "?" + PARAM_ACTION + "="
				+ ACTION_SHOW_BOARD + "&" + PARAM_BOARDID + "=" + boardidString);
		response.setContentLength(0);
		try {
			response.close();
		} catch (final IOException e) {
			LOGGER.log(Level.INFO, "Error sending answer to notice create.", e);
		}
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
			LOGGER.log(Level.SEVERE, "Error sending internal server error.", e);
		}
	}

	@Override
	public List<MenuEntry> getMenuEntrys() {
		return menu;
	}
}
