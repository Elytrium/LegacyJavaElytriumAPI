package ru.elytrium.host.api.model.module.params;

import java.util.List;

public class ModulePlugin {
    public String displayName;
    public String versionRange;
    public boolean enabled;
    public List<ModuleMount> mounts;
    public List<ModuleConfigFile> configFiles;
}
