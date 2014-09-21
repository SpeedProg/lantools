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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Poll implements Serializable {
    private final String question;
    private final Map<String, PollOption> options;
    private final List<PollOption> optionsList;
    private final int votes;
    private int optionIdCounter;

    public Poll(final String question, final int votes) {
        this.options = new HashMap<String, PollOption>();
        this.optionsList = new LinkedList<PollOption>();
        this.question = question;
        this.votes = votes;
        this.optionIdCounter = 0;
    }

    public synchronized void addOption(final PollOption option) {
        option.setId(optionIdCounter);
        optionIdCounter++;
        options.put(option.getName(), option);
        optionsList.add(option);
    }

    public synchronized void addVote(final String name) {
        addVote(name, 1);
    }

    public synchronized void addVote(final String name, final int count) {
        final PollOption option = options.get(name);
        if (option == null) {
            return;
        }
        option.addVotes(count);
    }

    public synchronized void addVotes(final Set<String> optionSet) {
        addVotes(optionSet, 1);
    }

    public synchronized void addVotes(final Set<String> optionSet,
            final int count) {
        for (final String opString : optionSet) {
            addVote(opString, count);
        }
    }

    public List<PollOption> getOptions() {
        return (new LinkedList<PollOption>(optionsList));
    }

    public String getQuestion() {
        return question;
    }

    public int getVotes() {
        return votes;
    }
}
