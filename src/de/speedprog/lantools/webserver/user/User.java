package de.speedprog.lantools.webserver.user;

import java.net.InetAddress;

public interface User {
    public InetAddress getInetAddress();

    public String getUsername();
}
