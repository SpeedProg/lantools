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

public class PollOption implements Serializable {
    private final String name;
    private int count;
    private final int hashCode;
    private int id;

    public PollOption(final String name) {
        this.name = name;
        this.hashCode = name.hashCode();
        this.count = 0;
        this.id = -1;
    }

    public synchronized void addVotes(final int c) {
        count += c;
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
        if (option.name.equals(name)) {
            return true;
        }
        return false;
    }

    /**
     * Returns the current vote count.
     *
     * @return the current vote count.
     */
    public synchronized int getCount() {
        return count;
    }

    public synchronized int getId() {
        return id;
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

    public synchronized void setCount(final int c) {
        count = c;
    }

    public synchronized void setId(final int id) {
        this.id = id;
    }

    /**
     * Adds a single vote.
     */
    public synchronized void vote() {
        count++;
    }
}
