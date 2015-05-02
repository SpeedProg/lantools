package de.speedprog.lantools.modules;

import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import de.speedprog.lantools.webserver.user.User;

public interface ModuleContainer {
	public void handle(Request request, Response response, User user, List<MenuEntry> menu);
	
	
	public List<MenuEntry> getMenuEntrys();
}
