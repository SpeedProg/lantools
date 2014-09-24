package de.speedprog.lantools.modules.notices;

import javax.swing.Icon;
import javax.swing.JPanel;

import de.speedprog.lantools.modules.Module;
import de.speedprog.lantools.modules.ModuleContainer;

public class NoticesModule implements Module {
    private static final String DEF_BASE_PATH = "/notices/";
    private String basePath;
    private final ModuleContainer moduleContainer;

    public NoticesModule(final String basePath) {
        if (basePath == null) {
            this.basePath = DEF_BASE_PATH;
        } else {
            this.basePath = basePath;
        }
        this.moduleContainer = new NoticesContainer(this.basePath);
    }

    @Override
    public String getBasePath() {
        return basePath;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public ModuleContainer getModuleContainer() {
        return moduleContainer;
    }

    @Override
    public String getName() {
        return "Notices";
    }

    @Override
    public JPanel getPanel() {
        return null;
    }

    @Override
    public String getTip() {
        return null;
    }

    @Override
    public void onClose() {
        // TODO Auto-generated method stub
    }
}
