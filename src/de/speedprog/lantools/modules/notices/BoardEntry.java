package de.speedprog.lantools.modules.notices;

import java.io.Serializable;
import java.util.UUID;

import de.speedprog.lantools.webserver.user.User;

public class BoardEntry implements Serializable {
    private final User owner;
    private final String content;
    private final String title;
    private final UUID id;

    /**
     * Creates a BoardEntry with the given owner, content and title. Gives it
     * self a random UUID.
     *
     * @param owner
     *            the owner
     * @param content
     *            the content
     * @param title
     *            the title
     */
    public BoardEntry(final User owner, final String title, final String content) {
        if (owner == null || content == null || title == null) {
            throw new IllegalArgumentException();
        }
        this.owner = owner;
        this.content = content;
        this.title = title;
        this.id = UUID.randomUUID();
    }

    public String getContent() {
        return content;
    }

    public UUID getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }
}
