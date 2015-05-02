package de.speedprog.lantools.modules;

import java.util.List;

public class MenuEntry {

	private String[] data;
	private boolean hasSubmenu;
	private List<MenuEntry> submenu;
	
	public MenuEntry(String name, String link) {
		data = new String[2];
		data[0] = link;
		data[1] = name;
		hasSubmenu = false;
	}
	
	public MenuEntry(String name, String link, List<MenuEntry> entries) {
		this(link, name);
		data[0] = link;
		data[1] = name;
		submenu = entries;
		hasSubmenu = true;
	}
	
	public String getLink() {
		return data[0];
	}
	
	public String getName() {
		return data[1];
	}
	
	public boolean getHassubmenu() {
		return hasSubmenu;
	}
	
	public List<MenuEntry> getSubmenu() {
		return submenu;
	}
	
	public String toString() {
		return String.format("<MenuEntry(%s,%s,%s)>", data[0], data[1], submenu);
	}
}
