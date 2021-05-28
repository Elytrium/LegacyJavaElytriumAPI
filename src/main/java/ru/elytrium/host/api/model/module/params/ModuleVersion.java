package ru.elytrium.host.api.model.module.params;

public class ModuleVersion {
    public String containerVersion;
    public String displayName;
    public String visibleVersion;

    public ModuleVersion(String containerVersion, String displayName, String visibleVersion) {
        this.containerVersion = containerVersion;
        this.displayName = displayName;
        this.visibleVersion = visibleVersion;
    }
}
