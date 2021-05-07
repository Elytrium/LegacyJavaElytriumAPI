package ru.elytrahost.api.model.module;

import ru.elytrahost.api.model.module.billing.ModuleBilling;
import ru.elytrahost.api.model.module.params.*;

import java.util.List;

public class Module {
    public String displayName;
    public String name;
    public List<ModuleVersion> availableVersions;
    public List<ModuleBilling> availableBillings;
    public List<ModuleMount> mountDefaults;
    public List<ModulePlugin> pluginDefaults;
    public List<ModuleConfigFile> configFiles;
}
