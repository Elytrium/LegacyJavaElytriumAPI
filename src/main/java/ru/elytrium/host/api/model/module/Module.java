package ru.elytrium.host.api.model.module;

import ru.elytrium.host.api.model.module.billing.ModuleBilling;
import ru.elytrium.host.api.model.module.params.ModuleConfigFile;
import ru.elytrium.host.api.model.module.params.ModuleMount;
import ru.elytrium.host.api.model.module.params.ModulePlugin;
import ru.elytrium.host.api.model.module.params.ModuleVersion;

import java.util.List;

public class Module {
    private String displayName;
    private String name;
    private int bindPort;
    private List<ModuleVersion> availableVersions;
    private List<ModuleBilling> availableBillings;
    private List<ModuleMount> mountDefaults;
    private List<ModulePlugin> pluginDefaults;
    private List<ModuleConfigFile> configFiles;

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public int getBindPort() {
        return bindPort;
    }

    public List<ModuleVersion> getAvailableVersions() {
        return availableVersions;
    }

    public List<ModuleBilling> getAvailableBillings() {
        return availableBillings;
    }

    public List<ModuleMount> getMountDefaults() {
        return mountDefaults;
    }

    public List<ModulePlugin> getPluginDefaults() {
        return pluginDefaults;
    }

    public List<ModuleConfigFile> getConfigFiles() {
        return configFiles;
    }
}
