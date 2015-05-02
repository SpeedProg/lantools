package de.speedprog.lantools.modules.poll.freenmaker;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import de.speedprog.lantools.modules.poll.PollOption;
import de.speedprog.lantools.modules.poll.WebPoll;

public class FMPoll {
	/**
	 * Creates a FMPollList from a Collection of WebPolls for use in a freemaker
	 * template.
	 *
	 * @param polls
	 * @param cUser
	 * @param vote
	 * @param result
	 * @param delete
	 * @return
	 */
	public static List<FMPoll> createFromWebPoll(
			final Collection<WebPoll> polls, final InetAddress cUser,
			final String vote, final String result, final String delete) {
		final List<FMPoll> fmPolls = new ArrayList<>(polls.size());
		for (final Iterator<WebPoll> iterator = polls.iterator(); iterator
				.hasNext();) {
			final WebPoll poll = iterator.next();
			fmPolls.add(createFromWebPoll(poll, cUser, vote, result, delete));
		}
		return fmPolls;
	}

	/**
	 * Creates a FMPoll from a WebPoll for use in a freemaker template.
	 *
	 * @param poll
	 * @param cUser
	 * @param vote
	 * @param result
	 * @param delete
	 * @return
	 */
	public static FMPoll createFromWebPoll(final WebPoll poll,
			final InetAddress cUser, final String vote, final String result,
			final String delete) {
		return new FMPoll(poll.getQuestion(), poll.getOptions(), poll
				.getOwner().equals(cUser), poll.getUuid(),
				poll.getRestriction(cUser), poll.getVotes(), vote, result,
				delete);
	}

	private final String question;
	private final List<PollOption> options;
	private final List<String> colors;
	private final boolean isOwner;
	private final UUID uuid;
	private final int restriction;
	private final String voteUrl;
	private final String resultUrl;
	private final String deleteUrl;
	private final int votes;

	/**
	 * Create a poll for FreeMaker template parsing.
	 *
	 * @param question
	 * @param options
	 * @param isOwner
	 * @param id
	 * @param restriction
	 */
	public FMPoll(final String question, final Collection<PollOption> options,
			final boolean isOwner, final UUID id, final int restriction,
			final int votes, final String voteS, final String resultS,
			final String deleteS) {
		this.question = question;
		this.options = new ArrayList<>(options);
		this.isOwner = isOwner;
		this.uuid = id;
		this.restriction = restriction;
		this.voteUrl = voteS + uuid.toString();
		this.resultUrl = resultS + uuid.toString();
		this.deleteUrl = deleteS + uuid.toString();
		this.votes = votes;
		this.colors = new ArrayList<String>();
		for (int i = 0; i < this.options.size() * 2; i++) {
			colors.add(getColor(this.options.size() * 2, i));
		}
	}

	private static final byte[] hsvToRgb(double h, double s, double v) {
		double r = 0.0d, g = 0.0d, b = 0.0d;
		double i = Math.floor(h * 6.0d);
		double f = h * 6.0d - i;
		double p = v * (1.0d - s);
		double q = v * (1.0d - f * s);
		double t = v * (1.0d - (1.0d - f) * s);
		switch (((int) i) % 6) {
		case 0: {
			r = v;
			g = t;
			b = p;
			break;
		}
		case 1: {
			r = q;
			g = v;
			b = p;
			break;
		}
		case 2:
			r = p;
			g = v;
			b = t;
			break;
		case 3:
			r = p;
			g = q;
			b = v;
			break;
		case 4:
			r = t;
			g = p;
			b = v;
			break;
		case 5:
			r = v;
			g = p;
			b = q;
			break;
		}
		return new byte[] { (byte) Math.floor(r * 255),
				(byte) Math.floor(g * 255), (byte) Math.floor(b * 255) };
	}

	private static final String getColor(int stepMax, int stepC) {
		byte[] color = hsvToRgb(((double) stepC / (double) stepMax), 1.0d, 0.5d);
		return "#" + String.format("%02x", color[0])
				+ String.format("%02x", color[1])
				+ String.format("%02x", color[2]);
	}

	public String getDeleteUrl() {
		return deleteUrl;
	}

	public boolean getIsOwner() {
		return isOwner;
	}

	public List<PollOption> getOptions() {
		return options;
	}

	public String getQuestion() {
		return question;
	}

	public int getRestriction() {
		return restriction;
	}

	public String getResultUrl() {
		return resultUrl;
	}

	public UUID getUuid() {
		return uuid;
	}

	/**
	 * @return get number of options one person is allowed to vote for.
	 */
	public int getVotes() {
		return votes;
	}

	public String getVoteUrl() {
		return voteUrl;
	}

	public List<String> getColors() {
		return colors;
	}
}
