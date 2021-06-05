package ru.elytrium.host.api.model.module.params;

public class ModuleVersion {
    public String version;
    public String displayName;
    public String visibleVersion;

    public ModuleVersion(String version, String displayName, String visibleVersion) {
        this.version = version;
        this.displayName = displayName;
        this.visibleVersion = visibleVersion;
    }
}
