package net.elytrium.api.model.module.params;

import dev.morphia.annotations.Entity;

@Entity("module_versions")
public class ModuleVersion {
    private String version;
    private String displayName;

    public ModuleVersion() {}

    public ModuleVersion(String version, String displayName) {
        this.version = version;
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ModuleVersion) {
            return ((ModuleVersion) o).version.equals(version);
        }
        return false;
    }

    public String getVersion() {
        return version;
    }

    public String getDisplayName() {
        return displayName;
    }
}
