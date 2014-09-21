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
package de.speedprog.lantools.webserver;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

public class NamedContainer implements Container {
    private final String name;
    private final Container container;
    private final String basePath;

    public NamedContainer(final String n, final Container container,
            final String bPath) {
        if (container == null || n == null) {
            throw new IllegalArgumentException("");
        }
        this.container = container;
        name = n;
        this.basePath = bPath;
    }

    public String getBasepath() {
        return basePath;
    }

    public Container getContainer() {
        return container;
    }

    public String getName() {
        return name;
    }

    @Override
    public void handle(final Request req, final Response resp) {
        container.handle(req, resp);
    }
}