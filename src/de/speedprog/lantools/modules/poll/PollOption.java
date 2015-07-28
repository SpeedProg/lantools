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

import java.io.Serializable;
import java.util.List;

public class PollOption implements Serializable {
    private final String name;
    private int id;
    private final int hashCode;
    private List<Vote> voteList;

    public PollOption(final String name, int id) {
        this.name = name;
        this.hashCode = name.hashCode();
        this.id = id;
    }



    public int getId() {
        return id;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PollOption)) {
            return false;
        }
        final PollOption option = (PollOption) obj;
        return option.name.equals(name);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public List<Vote> getVoteList() {
        return voteList;
    }

    public void addVote(Vote v) {
        voteList.add(v);
    }
}
