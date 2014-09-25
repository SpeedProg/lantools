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
package de.speedprog.lantools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.SwingUtilities;

import tk.speedprog.utils.Settings;
import de.speedprog.lantools.modules.ModuleSettings;
import de.speedprog.lantools.ui.LanToolWindow;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class LanTools {
    public static void closeSettings() {
        SETTINGS.close();
    }

    public static Configuration getFreeMakerConfig() {
        synchronized (LanTools.class) {
            if (CFG == null) {
                final Configuration cfg = new Configuration();
                try {
                    cfg.setDirectoryForTemplateLoading(new File("./templates/"));
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                cfg.setObjectWrapper(new DefaultObjectWrapper());
                cfg.setDefaultEncoding("UTF-8");
                cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
                cfg.setIncompatibleImprovements(new Version(2, 3, 20));
                CFG = cfg;
            }
            return CFG;
        }
    }

    public static Path getModuleConfigPath(final String basePath) {
        final Path modulePath = CFG_PATH.resolve(basePath.replace('/',
                File.separatorChar).substring(1));
        final File cfgFile = modulePath.toFile();
        if (cfgFile.exists()) {
            if (!cfgFile.isDirectory()) {
                cfgFile.delete();
                cfgFile.mkdirs();
            }
        } else {
            cfgFile.mkdirs();
        }
        return modulePath;
    }

    public static Settings getSettings() {
        return SETTINGS;
    }

    /**
     * Creates a Settings object for the Module with the given basePath.
     *
     * @param basePath
     *            basePath of the Module this setting is for, needs to start
     *            with a "/" and end without a "/"
     */
    public static ModuleSettings getSettingsFor(final String basePath) {
        return new ModuleSettings(SETTINGS, basePath);
    }

    public static void main(final String[] args) {
        prepareForStart();
        if (args.length > 0) {
            if (args[0].equals("--sversion")) {
                System.out.print(VERSION_STRING);
                return;
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LanToolWindow window;
                try {
                    window = new LanToolWindow();
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }
                window.setVisible(true);
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                LanTools.closeSettings();
            }
        }));
    }

    private static void prepareForStart() {
        final File cfgFile = CFG_PATH.toFile();
        if (cfgFile.exists()) {
            if (!cfgFile.isDirectory()) {
                cfgFile.delete();
                cfgFile.mkdirs();
            }
        } else {
            cfgFile.mkdirs();
        }
    }

    private static final Path CFG_PATH = Paths.get(".", "cfg");
    public static final String VERSION_STRING = "1.0.0-RC3";
    public static final Integer VERSION_NUM = 5;
    private static Configuration CFG = null;
    private static final Settings SETTINGS = new Settings(getModuleConfigPath(
            "/").resolve("settings.xml").toString());

    private LanTools() {
    }
}
