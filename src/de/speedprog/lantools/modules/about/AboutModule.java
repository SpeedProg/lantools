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
package de.speedprog.lantools.modules.about;

import javax.swing.Icon;
import javax.swing.JPanel;

import de.speedprog.lantools.modules.Module;
import de.speedprog.lantools.modules.ModuleContainer;

public class AboutModule implements Module {
    private final AboutPanel panel;

    /**
     * Create the panel.
     */
    public AboutModule() {
        panel = new AboutPanel();
    }

    @Override
    public String getBasePath() {
        return null;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public ModuleContainer getModuleContainer() {
        return null;
    }

    @Override
    public String getName() {
        return "About";
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public String getTip() {
        return null;
    }

    @Override
    public void onClose() {
    }

    @Override
    public void usersCleared() {
    }
}
