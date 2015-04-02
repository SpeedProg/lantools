package de.speedprog.lantools.webserver.user;

import java.io.Serializable;
import java.net.InetAddress;

public class UserImpl implements User, Serializable {
	private String username;
	private InetAddress address;

	/**
	 *
	 * @param username
	 *            the username, null not allowed
	 * @param address
	 *            the InetAddress of the user, null not allowed
	 */
	public UserImpl(final String username, final InetAddress address) {
		if (username == null || address == null) {
			throw new IllegalArgumentException();
		}
		this.username = username;
		this.address = address;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		final User user = (User) obj;
		if (user.getUsername().equals(this.username)
				&& user.getInetAddress().equals(this.address)) {
			return true;
		}
		return false;
	}

	@Override
	public synchronized InetAddress getInetAddress() {
		return address;
	}

	@Override
	public synchronized String getUsername() {
		return username;
	}

	@Override
	public int hashCode() {
		return username.hashCode();
	}

	protected synchronized void setInetAddress(final InetAddress address) {
		this.address = address;
	}

	protected synchronized void setUsername(final String username) {
		this.username = username;
	}
}
