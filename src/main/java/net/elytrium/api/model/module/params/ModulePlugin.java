package net.elytrium.api.model.module.params;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Reference;

import java.util.List;

@Entity("module_plugin")
public class ModulePlugin {
    public String displayName;
    public String versionRange;
    public boolean enabled;

    @Reference
    public List<ModuleMount> mounts;

    @Reference
    public List<ModuleConfigFile> configFiles;
}
