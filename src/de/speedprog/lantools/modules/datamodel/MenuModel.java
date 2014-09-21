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
package de.speedprog.lantools.modules.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuModel {
    private final List<Link> links;

    public MenuModel() {
        links = new ArrayList<>();
    }

    public void addLink(final String name, final String url) {
        links.add(new Link(name, url));
    }

    public List<Map<String, String>> getMenuModel() {
        final List<Map<String, String>> linkList = new ArrayList<>();
        for (final Link link : links) {
            final Map<String, String> linkMap = new HashMap<String, String>();
            linkMap.put("name", link.name);
            linkMap.put("url", link.url);
            linkList.add(linkMap);
        }
        return linkList;
    }

    private class Link {
        private final String name;
        private final String url;

        public Link(final String name, final String url) {
            this.name = name;
            this.url = url;
        }
    }
}
