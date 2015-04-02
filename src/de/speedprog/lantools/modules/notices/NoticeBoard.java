package de.speedprog.lantools.modules.notices;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class NoticeBoard implements Serializable {
	private final String name;
	private final InetAddress owner;
	private final List<BoardEntry> entryList;
	private final UUID id;
	private final String description;

	public NoticeBoard(final InetAddress owner, final String name,
			final String description) {
		if (owner == null || name == null || description == null) {
			throw new IllegalArgumentException();
		}
		this.owner = owner;
		this.entryList = Collections
				.synchronizedList(new LinkedList<BoardEntry>());
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

	public BoardEntry getEntry(final UUID entryID) {
		synchronized (entryList) {
			for (final BoardEntry entry : entryList) {
				if (entry.getId().equals(entryID)) {
					return entry;
				}
			}
		}
		return null;
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

	public InetAddress getOwner() {
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

	public BoardEntry removeBoardEntry(final UUID entryID) {
		synchronized (entryList) {
			for (final Iterator iterator = entryList.iterator(); iterator
					.hasNext();) {
				final BoardEntry boardEntry = (BoardEntry) iterator.next();
				if (boardEntry.getId().equals(entryID)) {
					iterator.remove();
					return boardEntry;
				}
			}
		}
		return null;
	}
}
