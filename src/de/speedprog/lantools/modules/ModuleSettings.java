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
package de.speedprog.lantools.modules;

import tk.speedprog.utils.Settings;

public class ModuleSettings {
    private static final String MODULE_PREFIX = "module";
    private final Settings settings;
    private final String basePath;

    /**
     * Creates a Settings object for the Module with the given basePath.
     *
     * @param settings
     *            General Settings object
     * @param basePath
     *            basePath of the Module this setting is for, needs to start
     *            with a "/" and end with a "/"
     */
    public ModuleSettings(final Settings settings, final String basePath) {
        if (!(basePath.startsWith("/") && basePath.endsWith("/"))) {
            throw new IllegalArgumentException();
        }
        this.settings = settings;
        this.basePath = basePath;
    }

    public String getString(final String id) {
        return settings.getString(MODULE_PREFIX + basePath + id);
    }

    /**
     * Stores a value under the given id, if it contained a value for the id,
     * the value is replaced by the new one.
     *
     * @param id
     *            id of the value, is not allowed to contain "/"
     * @param value
     *            the value to store
     */
    public void putString(final String id, final String value) {
        settings.storeString(MODULE_PREFIX + basePath + id, value);
    }
}
