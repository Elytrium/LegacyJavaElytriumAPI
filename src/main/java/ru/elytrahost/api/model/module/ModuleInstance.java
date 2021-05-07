package ru.elytrahost.api.model.module;

import ru.elytrahost.api.model.module.billing.ModuleBilling;
import ru.elytrahost.api.model.module.params.*;
import ru.elytrahost.api.model.user.User;

import java.util.ArrayList;
import java.util.List;

public class ModuleInstance {
    public final User user;
    public ModuleVersion version;
    public ModuleBilling billing;
    public final List<ModuleVersion> availableVersions;
    public final List<ModuleBilling> availableBillings;
    public final List<ModuleMount> mounts;
    public final List<ModuleConfigFile> configs;
    public final List<ModulePlugin> plugins;

    public ModuleInstance(User user, Module module) {
        this.user = user;
        this.availableVersions = module.availableVersions;
        this.availableBillings = module.availableBillings;
        this.mounts = new ArrayList<>(module.mountDefaults);
        this.configs = new ArrayList<>(module.configFiles);
        this.plugins = new ArrayList<>(module.pluginDefaults);
    }
}
