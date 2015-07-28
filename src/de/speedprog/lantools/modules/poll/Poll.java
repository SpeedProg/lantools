/*
 * Copyright 2014 Constantin Wenger
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.speedprog.lantools.modules.poll;

import de.speedprog.lantools.webserver.user.User;

import java.io.Serializable;
import java.util.*;

public class Poll implements Serializable {
	private final String question;
	private final Map<String, PollOption> optionsMap;
	private int maxVotes;
	private int optionIdCounter;

	public Poll(final String question, int maxVotes) {
		this.optionsMap = new HashMap<>();
		this.question = question;
		this.optionIdCounter = 0;
		this.maxVotes = maxVotes;
	}

	public synchronized void addOption(String optionName) {
		optionIdCounter++;
		optionsMap.put(optionName, new PollOption(optionName, optionIdCounter));
	}

	public synchronized void addVote(User user, PollOption option) {
        PollOption option1 = optionsMap.get(option.getName());
        option1.addVote(user);
	}

	public List<PollOption> getOptions() {
		return (new ArrayList<>(optionsMap.values()));
	}

	public String getQuestion() {
		return question;
	}

	public int getMaxVotes() {
		return maxVotes;
	}
}
