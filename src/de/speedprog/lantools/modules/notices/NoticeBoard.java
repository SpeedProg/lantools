package de.speedprog.lantools.modules.notices;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.speedprog.lantools.webserver.user.User;

public class NoticeBoard implements Serializable {
    private final String name;
    private final User owner;
    private final List<BoardEntry> entryList;
    private final UUID id;
    private final String description;

    public NoticeBoard(final User owner, final String name,
            final String description) {
        if (owner == null || name == null || description == null) {
            throw new IllegalArgumentException();
        }
        this.owner = owner;
        this.entryList = new LinkedList<>();
        this.name = name;
        this.id = UUID.randomUUID();
        this.description = description;
    }

    public void addBoardEntry(final BoardEntry entry) {
        entryList.add(entry);
    }

    public String getDescription() {
        return description;
    }

    public List<BoardEntry> getEntryList() {
        return Collections.unmodifiableList(entryList);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    /**
     * Removes this exact object from the entries on this board.
     *
     * @param entry
     *            the entry to remove
     */
    public void removeBoardEntry(final BoardEntry entry) {
        entryList.remove(entry);
    }
}
