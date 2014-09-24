package de.speedprog.lantools.webserver.user;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class UserMapper {
    private final Map<InetAddress, UserImpl> inetToUserMap;
    private final Map<String, InetAddress> nameToInetMap;

    public UserMapper() {
        inetToUserMap = new HashMap<>();
        nameToInetMap = new HashMap<>();
    }

    /**
     * Change the username related to the given address.
     * You can not use a username that is already mapped to an address.
     *
     * @param address
     *            the address of the user
     * @param newname
     *            the name for the user
     * @return true if it succeeded, otherwise false
     */
    public synchronized boolean changeUsername(final InetAddress address,
            final String newname) {
        final UserImpl user = inetToUserMap.get(address);
        if (user == null) { // contains user for the address
            return false;
        }
        if (nameToInetMap.containsKey(newname)) { // contains no user with the new name
            return false;
        }
        user.setUsername(newname);
        return true;
    }

    /**
     * Get the address to the given name
     *
     * @param unsername
     *            the username to lookup
     * @return the address, or null if no address is mapped to the given
     *         username
     */
    public synchronized InetAddress getInetAddress(final String username) {
        return nameToInetMap.get(username);
    }

    /**
     * Get the username mapped to a given address.
     *
     * @param address
     *            the address
     * @return the mapped username
     */
    public synchronized User getUser(final InetAddress address) {
        return inetToUserMap.get(address);
    }

    /**
     * Set the username corresponding to a specific InetAddress.
     * You can not set a username that is already used, or set the username of
     * an address that is already used.
     *
     * @param address
     *            the address of the user
     * @param username
     *            the name for the user
     * @return true if it succeeded, otherwise false
     */
    public synchronized boolean setUsername(final InetAddress address,
            final String username) {
        if (inetToUserMap.containsKey(address)
                || nameToInetMap.containsKey(username)) {
            return false;
        }
        final User user = new UserImpl(username, address);
        addUser(user);
        return true;
    }

    private void addUser(final User user) {
        final UserImpl userImpl = new UserImpl(user.getUsername(),
                user.getInetAddress());
        inetToUserMap.put(user.getInetAddress(), userImpl);
        nameToInetMap.put(user.getUsername(), user.getInetAddress());
    }
}
