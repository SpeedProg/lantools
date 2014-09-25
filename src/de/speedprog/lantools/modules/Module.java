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

import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * Interface to create modules for the server.
 * Each module needs to have a BasePath inside which it should live.
 * Eg. /notices/ is the BasePath of the Notices Module.
 * If you need to store simple data, like Integer, Boolean, String
 * you should obtain a ModuleSettings instance using LanTools#getSettingsFor
 * which takes your BasePath.
 * If you need to store more complex informations and
 * for this create your own files you should do this in cfgBasePath.
 * Eg. the Notices Module should store it's files in cfg/notices/ .
 * This path can be obtained by calling LanTools#getModuleConfigPath with your
 * BasePath.
 */
public interface Module {
    /**
     * Can return null if no container should be added.
     * The basePath always needs to be a folder in the form of /PATH/ so it
     * needs to start with an / and end with /.
     *
     * @return
     */
    public String getBasePath();

    public Icon getIcon();

    /**
     * Can return null if no container should be added.
     *
     * @return
     */
    public ModuleContainer getModuleContainer();

    public String getName();

    /**
     * Allowed to return null, if no panel should be displayed!
     *
     * @return
     */
    public JPanel getPanel();

    public String getTip();

    public void onClose();

    public void usersCleared();
}
