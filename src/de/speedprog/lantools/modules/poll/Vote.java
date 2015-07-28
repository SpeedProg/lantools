package de.speedprog.lantools.modules.poll;

import de.speedprog.lantools.webserver.user.User;

public class Vote {
    private User voter;
    private PollOption option;
    public Vote(User user, PollOption option) {
        this.voter = user;
        this.option = option;
    }

    public User getVoter() {
        return voter;
    }

    public PollOption getOption() {
        return option;
    }
}
