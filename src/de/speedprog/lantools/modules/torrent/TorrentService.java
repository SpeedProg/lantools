/*
 * Copyright 2014 Constantin Wenger
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.speedprog.lantools.modules.torrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.simpleframework.http.Part;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;

import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEnclosureImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedOutput;
import com.turn.ttorrent.bcodec.InvalidBEncodingException;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

import de.speedprog.lantools.LanTools;
import de.speedprog.lantools.modules.ModuleContainer;
import de.speedprog.lantools.modules.datamodel.MenuModel;
import de.speedprog.lantools.webserver.WebServer;
import de.speedprog.lantools.webserver.user.User;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class TorrentService implements ModuleContainer {
	private static final String SITE_ENC = "UTF-8";
	public static final String TORRENTLOAD_URL = "load.torrent";
	public static final String TORRENTUPLOAD_URL = "upload";
	// private static final String TORRENTBASE_URL = "";
	public static final String TORRENT_ATOM = "torrents.atom";
	public static final String TORRENT_RSS = "torrents.rss";
	private static final String TEMPLATE_DIR = "torrentracker" + File.separator;
	private static final String PARAM_ACTION = "action";
	private static final String ACTION_DELETE = "del_torrent";
	private static final String PARAM_TORRENT_INFOHASH = "torrenthash";
	private String basePath;
	private Tracker tracker;
	private String trackerHost;
	private String trackerPort;
	private final Abdera abdera;
	private final List<Map<String, String>> menuData;
	private final static Configuration CFG = LanTools.getFreeMakerConfig();
	private final WebServer webServer;
	private final HashMap<String, File> infoHashToFile;

	public TorrentService(final String basePath, final Tracker tracker,
			final String trackerHost, final String trackerPort,
			final WebServer webServer) {
		this.webServer = webServer;
		this.basePath = basePath;
		this.tracker = tracker;
		this.trackerHost = trackerHost;
		this.trackerPort = trackerPort;
		abdera = new Abdera();
		final MenuModel menuModel = new MenuModel();
		menuModel.addLink("Home", "/");
		menuModel.addLink("Torrent Tracker", basePath);
		menuModel.addLink("Upload Torrent", basePath + TORRENTUPLOAD_URL);
		menuModel.addLink("RSS 2.0 Feed", basePath + TORRENT_RSS);
		menuModel.addLink("Atom 1.0 Feed", basePath + TORRENT_ATOM);
		menuData = menuModel.getMenuModel();
		infoHashToFile = new HashMap<>();
	}

	@Override
	public void handle(final Request req, final Response response,
			final User user) {
		try {
			OutputStream outputStream;
			try {
				outputStream = response.getOutputStream();
			} catch (final IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				try {
					response.close();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			final Map<String, Object> dataMap = new HashMap<String, Object>();
			final Template template;
			dataMap.put("menulinks", menuData);
			dataMap.put("basepath", basePath);
			dataMap.put("param_action", PARAM_ACTION);
			dataMap.put("a_del_torrent", ACTION_DELETE);
			dataMap.put("param_torrenthash", PARAM_TORRENT_INFOHASH);
			if (tracker == null) {
				response.set("Content-Type", "text/html");
				template = CFG.getTemplate(TEMPLATE_DIR + "notrunning.ftl");
			} else {
				final Path path = req.getPath();
				final String pathString = path.toString();
				if (!pathString.startsWith(basePath)) {
					// TODO: Write error!
					try {
						response.close();
					} catch (final IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				}
				String relPathString = pathString.substring(basePath.length(),
						pathString.length());
				if (relPathString.equals("")) {
					relPathString = "/";
				}
				switch (relPathString) {
				case "/": {
					System.out.println("at /");
					final String actionString = req.getParameter(PARAM_ACTION);
					if (actionString != null) {
						System.out.println("Action: " + actionString);
						if (actionString.equals(ACTION_DELETE)) {
							System.out.println("Action delte!");
							final String torrenthash = req
									.getParameter(PARAM_TORRENT_INFOHASH);
							if (torrenthash != null) {
								final Collection<TrackedTorrent> torrents = tracker
										.getTrackedTorrents();
								for (final TrackedTorrent torrent : torrents) {
									if (torrent.getHexInfoHash().equals(
											torrenthash)) {
										tracker.remove(torrent);
										infoHashToFile.remove(torrenthash)
												.delete();
										break;
									}
								}
							}
							response.setCode(Status.SEE_OTHER.getCode());
							response.set("Location", basePath);
							response.setContentLength(0);
							response.close();
							return;
						} else {
							response.setCode(Status.BAD_REQUEST.getCode());
							response.set("Location", basePath);
							response.setContentLength(0);
							response.close();
							return;
						}
					} else {
						response.set("Content-Type", "text/html");
						dataMap.put("tdlurl", basePath + TORRENTLOAD_URL
								+ "?hash=");
						final List<TrackedTorrent> torrents = new ArrayList<>(
								tracker.getTrackedTorrents());
						dataMap.put("torrents", torrents);
						template = CFG.getTemplate(TEMPLATE_DIR
								+ "torrentlist.ftl");
					}
				}
					break;
				case TORRENTLOAD_URL: {
					response.set("Content-Type", "application/x-bittorrent");

					try {
						final String hexHash = req.getParameter("hash");
						final Collection<TrackedTorrent> torrents = tracker
								.getTrackedTorrents();
						for (final Iterator<TrackedTorrent> iterator = torrents
								.iterator(); iterator.hasNext();) {
							final TrackedTorrent trackedTorrent = iterator
									.next();
							if (trackedTorrent.getHexInfoHash().equals(hexHash)) {
								response.set("Content-Disposition",
										"attachment; filename=\""
												+ trackedTorrent.getName()
												+ ".torrent\"");
								trackedTorrent.save(outputStream);
							}
						}
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
					return;
				}
				case TORRENTUPLOAD_URL:
					response.set("Content-Type", "text/html");
					String site;
					try {
						site = req.getParameter("site");
					} catch (final IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						site = "form";
					}
					if (site == null) {
						site = "form";
					}
					template = CFG.getTemplate(TEMPLATE_DIR + "uploadform.ftl");
					switch (site) {
					case "form":
						dataMap.put("showform", true);
						dataMap.put("action", basePath + TORRENTUPLOAD_URL
								+ "?site=upload");
						break;
					case "upload":
						final Part part = req.getPart("file");
						if (part.isFile()) {
							// always make files end with .torrent
							String fileNameString = part.getFileName();
							if (!(fileNameString.endsWith(".torrent"))) {
								fileNameString = fileNameString + ".torrent";
							}
							final File file = new File(
									getTorrentDir().toFile(), fileNameString);
							// if that file is already there send failed!
							if (file.exists()) {
								dataMap.put("uploaded", true);
								dataMap.put("success", false);
								dataMap.put("msg", "A file with "
										+ fileNameString
										+ " as filename already exists.");
								dataMap.put("showform", true);
								dataMap.put("action", basePath
										+ TORRENTUPLOAD_URL + "?site=upload");
								break;
							}
							final FileOutputStream FileOutputStream = new FileOutputStream(
									file);
							final InputStream iStream = part.getInputStream();
							final FileChannel fileChannel = FileOutputStream
									.getChannel();
							fileChannel.transferFrom(
									Channels.newChannel(iStream), 0,
									Long.MAX_VALUE);
							fileChannel.close();
							iStream.close();
							FileOutputStream.close();
							TrackedTorrent torrent;
							try {
								torrent = TrackedTorrent.load(file);
							} catch (final InvalidBEncodingException e) {
								// that wasn't a torrent file
								file.delete();
								torrent = null;
							}
							if (torrent != null) {
								infoHashToFile.put(torrent.getHexInfoHash(),
										file);
								tracker.announce(torrent);
							}
							dataMap.put("uploaded", true);
							dataMap.put("success", (torrent != null));
							if (torrent != null) {
								dataMap.put("tdlurl", basePath
										+ TORRENTLOAD_URL + "?hash=");
								dataMap.put("torrent", torrent);
							}
							dataMap.put("showform", true);
							dataMap.put("action", basePath + TORRENTUPLOAD_URL
									+ "?site=upload");
						}
						break;
					default:
						break;
					}
					break;
				case TORRENT_ATOM: {
					response.set("Content-Type", "application/atom+xml");
					final Feed feed = abdera.newFeed();
					feed.setId("tag:lantool.speedprog.de,2014-01-01:/tracker/torrent.atom");
					feed.setTitle("LanTools Torrent Feed");
					feed.setSubtitle("spread the fun");
					feed.setUpdated(new Date());
					final Collection<TrackedTorrent> torrents = tracker
							.getTrackedTorrents();
					for (final TrackedTorrent torrent : torrents) {
						final Entry entry = feed.addEntry();
						entry.setTitle(torrent.getName());
						entry.addLink("http://" + webServer.getHost() + ":"
								+ webServer.getPort() + basePath
								+ TORRENTLOAD_URL + "?hash="
								+ torrent.getHexInfoHash());
						final IRI iri = new IRI("http://" + webServer.getHost()
								+ ":" + webServer.getPort() + basePath
								+ TORRENTLOAD_URL + "?hash="
								+ torrent.getHexInfoHash());
						entry.setContent(iri, "application/x-bittorrent");
						entry.setUpdated(new Date());
					}
					try {
						feed.writeTo(outputStream);
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
				}
					return;
				case TORRENT_RSS: {
					response.set("Content-Type", "application/rss+xml");
					final SyndFeed feed = new SyndFeedImpl();
					feed.setFeedType("rss_2.0");
					feed.setTitle("LanTools Torrent Feed");
					feed.setLink(basePath + TORRENT_RSS);
					feed.setDescription("Torrent Feed");
					final List<SyndEntry> entries = new ArrayList<>();
					final Collection<TrackedTorrent> torrents = tracker
							.getTrackedTorrents();
					for (final TrackedTorrent torrent : torrents) {
						final SyndEntry entry = new SyndEntryImpl();
						entry.setTitle(torrent.getName());
						entry.setLink("http://" + webServer.getHost() + ":"
								+ webServer.getPort() + basePath
								+ TORRENTLOAD_URL + "?hash="
								+ torrent.getHexInfoHash());
						final SyndEnclosure enclosure = new SyndEnclosureImpl();
						enclosure.setType("application/x-bittorrent");
						enclosure.setUrl("http://" + webServer.getHost() + ":"
								+ webServer.getPort() + basePath
								+ TORRENTLOAD_URL + "?hash="
								+ torrent.getHexInfoHash());
						final List<SyndEnclosure> list = new LinkedList<>();
						list.add(enclosure);
						entry.setEnclosures(list);
						entries.add(entry);
					}
					feed.setEntries(entries);
					final SyndFeedOutput output = new SyndFeedOutput();
					output.output(feed, new OutputStreamWriter(outputStream));
					response.close();
				}
					return;
				default:
					template = CFG.getTemplate("error.ftl");
					dataMap.put("errormsg", "No valid page!");
					break;
				}
			}
			template.process(dataMap, new OutputStreamWriter(outputStream));
			try {
				response.close();
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void loadTorrents() {
		final File baseFile = getTorrentDir().toFile();
		final File[] torrentFiles = baseFile
				.listFiles(new java.io.FileFilter() {
					@Override
					public boolean accept(final File pathname) {
						return (pathname.isFile() && pathname.getName()
								.endsWith(".torrent"));
					}
				});
		for (final File file : torrentFiles) {
			TrackedTorrent trackedTorrent;
			try {
				trackedTorrent = TrackedTorrent.load(file);
				infoHashToFile.put(trackedTorrent.getHexInfoHash(), file);
				tracker.announce(trackedTorrent);
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				file.delete();
			}
		}
	}

	public void setBasePath(final String basePath) {
		this.basePath = basePath;
	}

	public void setTracker(final Tracker tracker) {
		this.tracker = tracker;
	}

	public void setTrackerHost(final String host) {
		trackerHost = host;
	}

	public void setTrackerPort(final String port) {
		trackerPort = port;
	}

	private String getHTML5TorrentList(final Collection<TrackedTorrent> torrList) {
		final StringBuilder builder = new StringBuilder();
		if (torrList.size() <= 0) {
			builder.append("<p>");
			builder.append("No Torrents");
			builder.append("</p>");
		} else {
			builder.append("<ul>");
			for (final TrackedTorrent torrent : torrList) {
				builder.append("<li>");
				builder.append("<a href=\"/load?hash=");
				builder.append(torrent.getHexInfoHash());
				builder.append("\" download=\"");
				builder.append(torrent.getName());
				builder.append(".torrent");
				builder.append("\">");
				builder.append(torrent.getName());
				builder.append("</a>");
				builder.append("</li>");
			}
			builder.append("</ul>");
		}
		return builder.toString();
	}

	private String getHTML5UploadForm() {
		return "<form action=\""
				+ basePath
				+ TORRENTUPLOAD_URL
				+ "?site=upload\" method=\"post\" enctype=\"multipart/form-data\"><label for=\"file\">Torrent File:</label><input type=\"file\" name=\"file\" id=\"file\" /><input type=\"submit\" value=\"Upload\"></form>";
	}

	private java.nio.file.Path getTorrentDir() {
		final java.nio.file.Path modulePath = LanTools
				.getModuleConfigPath(basePath);
		final java.nio.file.Path torrentDirPath = modulePath.resolve("torrent");
		// create dirs if they do not exist
		if (!torrentDirPath.toFile().exists()) {
			torrentDirPath.toFile().mkdirs();
		}
		return torrentDirPath;
	}
}
