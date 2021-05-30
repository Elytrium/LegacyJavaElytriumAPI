package ru.elytrium.host.api.model.module;

import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.Exclude;
import ru.elytrium.host.api.model.module.billing.ModuleBilling;
import ru.elytrium.host.api.model.module.params.ModuleConfigFile;
import ru.elytrium.host.api.model.module.params.ModuleMount;
import ru.elytrium.host.api.model.module.params.ModulePlugin;
import ru.elytrium.host.api.model.module.params.ModuleVersion;
import ru.elytrium.host.api.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ModuleInstance {
    private final User user;
    private final UUID uuid;
    private final String moduleName;
    private final String endpointDomain;
    private final ModuleVersion version;
    private final ModuleBilling billing;
    private final List<ModuleMount> mountsOverrides;
    private final List<ModuleConfigFile> configsOverrides;
    private final List<ModulePlugin> pluginsOverrides;

    @Exclude
    private Module module;

    public ModuleInstance(User user, String moduleName, ModuleVersion version, ModuleBilling billing, String endpointDomain) {
        this.user = user;
        this.uuid = UUID.randomUUID();
        this.endpointDomain = endpointDomain;
        this.moduleName = moduleName;
        this.version = version;
        this.billing = billing;
        this.mountsOverrides = new ArrayList<>();
        this.configsOverrides = new ArrayList<>();
        this.pluginsOverrides = new ArrayList<>();
    }

    public String getContainerId() {
        return getModule().getName() + " " + version.containerVersion;
    }


    public List<ModuleMount> getMountAndDownload() {
        List<ModuleMount> finalMounts = mountsOverrides.stream().filter(e -> e.enabled).collect(Collectors.toList());
        pluginsOverrides.stream()
            .filter(e -> e.enabled)
            .map(e -> e.mounts)
            .forEach(e -> e.stream()
                .filter(q -> q.enabled && q.checkVersion(version.containerVersion))
                .forEach(finalMounts::add));

        return finalMounts;
    }

    public Module getModule() {
        if (module == null) {
            ElytraHostAPI.getModules().getItem(moduleName);
        }

        return module;
    }

    public User getUser() {
        return user;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getEndpointDomain() {
        return endpointDomain;
    }

    public String getModuleName() {
        return moduleName;
    }

    public ModuleBilling getBilling() {
        return billing;
    }

    public ModuleVersion getVersion() {
        return version;
    }

    public List<ModuleMount> getMountsOverrides() {
        return mountsOverrides;
    }

    public List<ModuleConfigFile> getConfigsOverrides() {
        return configsOverrides;
    }

    public List<ModulePlugin> getPluginsOverrides() {
        return pluginsOverrides;
    }
}
