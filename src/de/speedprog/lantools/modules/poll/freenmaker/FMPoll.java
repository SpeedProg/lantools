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
     *
     * @param poll
     * @param currentAddress
     * @param vote
     * @param result
     * @param delete
     * @return
     */
    public static List<FMPoll> createFromWebPoll(
            final Collection<WebPoll> polls, final InetAddress currentAddress,
            final String vote, final String result, final String delete) {
        final List<FMPoll> fmPolls = new ArrayList<>(polls.size());
        for (final Iterator iterator = polls.iterator(); iterator.hasNext();) {
            final WebPoll poll = (WebPoll) iterator.next();
            fmPolls.add(createFromWebPoll(poll, currentAddress, vote, result,
                    delete));
        }
        return fmPolls;
    }

    /**
     * Creates a FMPoll from a WebPoll for use in a freemaker template.
     *
     * @param poll
     * @param currentAddress
     * @param vote
     * @param result
     * @param delete
     * @return
     */
    public static FMPoll createFromWebPoll(final WebPoll poll,
            final InetAddress currentAddress, final String vote,
            final String result, final String delete) {
        return new FMPoll(poll.getQuestion(), poll.getOptions(), poll
                .getOwnerAddress().equals(currentAddress), poll.getUuid(),
                poll.getRestriction(currentAddress), poll.getVotes(), vote,
                result, delete);
    }

    private final String question;
    private final List<PollOption> options;
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
}
