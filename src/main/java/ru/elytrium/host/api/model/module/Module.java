package ru.elytrium.host.api.model.module;

import ru.elytrium.host.api.model.module.billing.ModuleBilling;
import ru.elytrium.host.api.model.module.params.*;

import java.util.List;

public class Module {
    private String displayName;
    private String name;
    private int bindPort;
    private List<ModuleVersion> availableVersions;
    private List<ModuleBilling> availableBillings;
    private List<ModuleMount> mountDefaults;
    private List<ModulePlugin> pluginDefaults;
    private List<ModuleConfig> configDefaults;
    private List<ModuleConfigFile> configFiles;
    private List<String> availableTariffs;

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

    public List<ModuleConfig> getConfigDefaults() {
        return configDefaults;
    }

    public List<ModuleConfigFile> getConfigFiles() {
        return configFiles;
    }

    public List<String> getAvailableTariffs() {
        return availableTariffs;
    }
}
