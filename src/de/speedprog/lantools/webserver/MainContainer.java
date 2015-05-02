package de.speedprog.lantools.webserver;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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

import de.speedprog.lantools.LanTools;
import de.speedprog.lantools.modules.MenuEntry;
import de.speedprog.lantools.modules.ModuleContainer;
import de.speedprog.lantools.webserver.user.User;
import de.speedprog.lantools.webserver.user.UserImpl;
import de.speedprog.lantools.webserver.user.UserMapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MainContainer implements Container, Closeable {
	private final ConcurrentHashMap<String, NamedModuleContainer> containerMap;
	private final UserMapper userMapper;
	private List<MenuEntry> menu;
	private final File userFile;
	private static final String USERFILENAME = "users.obj";
	private static final String ACTION_CHANGE_NAME = "changename";
	private volatile String customHTML;

	public MainContainer() {
		containerMap = new ConcurrentHashMap<String, NamedModuleContainer>();
		userMapper = new UserMapper();
		menu = new ArrayList<MenuEntry>();
		menu.add(new MenuEntry("Home", "/"));
		menu.add(new MenuEntry("Change Name", "/?action=" + ACTION_CHANGE_NAME));
		this.userFile = LanTools.getModuleConfigPath("/").resolve(USERFILENAME)
				.toFile();
		try {
			loadUsers();
		} catch (final IOException e) {
		}
		this.customHTML = "";
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

		List<MenuEntry> relativeMenu = container.getMenuEntrys();
		List<MenuEntry> containerMenu = new ArrayList<MenuEntry>(relativeMenu.size()+1);
		
		System.out.println(container.getClass().getCanonicalName());
		// add home menu entry
		containerMenu.add(new MenuEntry("Home", basePath));
		containerMenu.addAll(makeMenuEntryListAbsolute(basePath, relativeMenu));

		menu.add(new MenuEntry(name, basePath, containerMenu));
		System.out.println(menu);
		return containerMap.put(path.toString(), new NamedModuleContainer(name,
				container, basePath));
	}
	
	public List<MenuEntry> makeMenuEntryListAbsolute(String base, List<MenuEntry> list) {
		List<MenuEntry> absoluteMenu = new ArrayList<MenuEntry>();
		for (MenuEntry e : list) {
			MenuEntry n;
			if (e.getHassubmenu()) {
				n = new MenuEntry(e.getName(), base + e.getLink(),
						makeMenuEntryListAbsolute(base, e.getSubmenu()));
			} else {
				n = new MenuEntry(e.getName(), base + e.getLink());
			}
			absoluteMenu.add(n);
		}
		return absoluteMenu;
	}

	public void clearUsers() {
		userMapper.clearUsers();
		userFile.delete();
	}

	@Override
	public void close() throws IOException {
		saveUsers();
	}

	public UserMapper getUserMapper() {
		return userMapper;
	}

	@Override
	public void handle(final Request req, final Response resp) {
		resp.set("Server", LanTools.VERSION_STRING);
		resp.setDate("Date", System.currentTimeMillis());
		final User user = userMapper.getUser(req.getClientAddress()
				.getAddress());
		final Path reqPath = req.getPath();
		final String[] segmentStrings = reqPath.getSegments();
		// no username set
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
					userMapper.setUsername(req.getClientAddress().getAddress(),
							usernameParam);
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
				data.put("title", "Set Username");
				data.put("menu", menu);
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
		// this is base page /
		if (segmentStrings.length == 0) {
			// list containers!
			String actionString = null;
			try {
				actionString = req.getParameter("action");
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (actionString == null) {
				actionString = "";
			}
			switch (actionString) {
			case ACTION_CHANGE_NAME:
				userMapper.removeUserByInetAddress(req.getClientAddress()
						.getAddress());
				resp.setCode(Status.SEE_OTHER.getCode());
				resp.set("Location", "/");
				resp.setContentLength(0);
				try {
					resp.close();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				sendContainerList(resp);
				break;
			}
		} else if (segmentStrings.length > 0) { // should always go to == 0 or
												// here
			if (segmentStrings[0].equals("html")) { // send the files :)
				// build the path
				final java.nio.file.Path path = Paths.get(".", segmentStrings);
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
						final FileInputStream fis = new FileInputStream(file);
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
				final NamedModuleContainer container = containerMap.get(reqPath
						.getDirectory());
				if (container != null) {
					container.handle(req, resp, user, menu);
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

	public void setCustomHTML(final String html) {
		this.customHTML = html;
	}

	private void loadUsers() throws IOException {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(userFile));
		Object object;
		try {
			object = ois.readObject();
		} catch (final ClassNotFoundException e) {
			ois.close();
			return;
		} finally {
			ois.close();
		}
		if (object instanceof Collection<?>) {
			@SuppressWarnings("unchecked")
			final Collection<UserImpl> users = (Collection<UserImpl>) object;
			userMapper.setUsers(users);
		}
	}

	private void saveUsers() throws IOException {
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(userFile));
		final Collection<User> users = userMapper.getUsers();
		oos.writeObject(users);
		oos.close();
	}

	private void sendContainerList(final Response response) {
		try {
			response.set("Content-Type", "text/html");
			final Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("title", "Module List");
			dataMap.put("customhtml", customHTML);
			dataMap.put("menu", menu);
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
