/*
Copyright 2014 Constantin Wenger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package de.speedprog.lantools.modules.poll;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebPoll implements Serializable {
    private final boolean restrictByIp;
    private final Pattern ipPattern;
    private final boolean oneVotePerIp;
    private final Poll poll;
    private final Set<InetAddress> ips;
    private final InetAddress owner;
    private final UUID uuid;

    public WebPoll(final String question, final boolean restrictByIp,
            final Pattern ipPattern, final boolean oneVotePerIp,
            final int votes, final InetAddress owner, final UUID id) {
        this.poll = new Poll(question, votes);
        this.restrictByIp = restrictByIp;
        this.oneVotePerIp = oneVotePerIp;
        this.ipPattern = ipPattern;
        this.ips = new HashSet<>();
        this.owner = owner;
        this.uuid = id;
    }

    public synchronized void addOption(final PollOption option) {
        poll.addOption(option);
    }

    public List<PollOption> getOptions() {
        return poll.getOptions();
    }

    public InetAddress getOwner() {
        return owner;
    }

    public String getQuestion() {
        return poll.getQuestion();
    }

    /**
     * Get reason for a vote being denied.
     *
     * @return 0 if he is allowed to vote, 1 restricted by ipfilter, 2 already
     *         voted and only one vote alowed.
     */
    public int getRestriction(final InetAddress user) {
        if (restrictByIp) { // check if pattern matches ip
            final Matcher matcher = ipPattern.matcher(user.getHostAddress());
            if (!matcher.matches()) {
                return 1;
            }
        }
        if (oneVotePerIp) {
            if (ips.contains(user)) { // check if ip already voted
                return 2;
            }
        }
        return 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getVotes() {
        return poll.getVotes();
    }

    public boolean isAllowed(final InetAddress user) {
        if (restrictByIp) { // check if pattern matches ip
            final Matcher matcher = ipPattern.matcher(user.getHostAddress());
            if (!matcher.matches()) {
                return false;
            }
        }
        if (oneVotePerIp) {
            if (ips.contains(user)) { // check if ip already voted
                return false;
            }
        }
        return true;
    }

    public synchronized boolean vote(final InetAddress user,
            final Set<String> options) {
        return vote(user, options, 1);
    }

    public synchronized boolean vote(final InetAddress user,
            final Set<String> options, final int count) {
        if (isAllowed(user)) {
            poll.addVotes(options, count);
            ips.add(user);
            return true;
        }
        return false;
    }

    public synchronized boolean vote(final InetAddress user, final String option) {
        if (isAllowed(user)) {
            poll.addVote(option);
            ips.add(user);
            return true;
        }
        return false;
    }

    public synchronized boolean vote(final InetAddress user,
            final String option, final int count) {
        if (isAllowed(user)) {
            poll.addVote(option, count);
            ips.add(user);
            return true;
        }
        return false;
    }
}
