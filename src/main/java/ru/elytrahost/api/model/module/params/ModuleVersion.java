package ru.elytrahost.api.model.module.params;

public class ModuleVersion {
    public final String containerVersion;
    public final String displayName;

    public ModuleVersion(String containerVersion, String displayName) {
        this.containerVersion = containerVersion;
        this.displayName = displayName;
    }
}
